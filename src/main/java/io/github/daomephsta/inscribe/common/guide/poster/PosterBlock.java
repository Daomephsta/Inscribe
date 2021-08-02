package io.github.daomephsta.inscribe.common.guide.poster;

import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.inscribe.common.util.BlockEntities;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class PosterBlock extends Block implements BlockEntityProvider
{
    public PosterBlock()
    {
        super(FabricBlockSettings.of(Material.CARPET, MapColor.WHITE)
            .breakByHand(true)
            .breakInstantly()
            .nonOpaque());
        setDefaultState(stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView view, BlockPos pos)
    {
        BlockPos from = pos;
        BlockPos to = pos.up(2).offset(state.get(Properties.HORIZONTAL_FACING).rotateYCounterclockwise(), 3);
        return BlockPos.stream(from, to).allMatch(view::isAir);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return getDefaultState().with(Properties.HORIZONTAL_FACING, context.getPlayerFacing().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        BlockPos from = pos;
        BlockPos to = pos.up(2).offset(state.get(Properties.HORIZONTAL_FACING).rotateYCounterclockwise(), 3);
        Iterable<BlockPos> positions = BlockPos.stream(from, to)::iterator;
        for (BlockPos board : positions)
        {
            world.setBlockState(board, state);
            ((PosterBlockEntity) world.getBlockEntity(board)).setBounds(from, to);
        }
    }
    
    @Override
    public void onStateReplaced(BlockState replacement, World world, BlockPos pos, BlockState removed, boolean moved)
    {
        // Judging by Mojang code the BE may be null or some other BE
        if (world.getBlockEntity(pos) instanceof PosterBlockEntity poster)
        {
            Iterable<BlockPos> positions = poster.positions()::iterator;
            for (BlockPos board : positions)
                world.setBlockState(board, Blocks.AIR.getDefaultState());
        }
        super.onStateReplaced(replacement, world, pos, removed, moved);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() == Inscribe.GUIDE_ITEM)
        {
            BlockEntities.get(world, pos, PosterBlockEntity.class)
                .flatMap(pbe -> BlockEntities.get(world, pbe.getRenderOrigin(), PosterBlockEntity.class))
                .ifPresent(pbe ->
                {
                    // ToCs are renderable, but there's no point
                    Identifier lastOpen = Inscribe.GUIDE_ITEM.getLastOpenId(stack);
                    if (lastOpen != null)
                        pbe.setSpread(Inscribe.GUIDE_ITEM.getGuideId(stack), lastOpen, 0);
                });
        }
        return ActionResult.PASS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        if (world.getBlockEntity(pos) instanceof PosterBlockEntity poster)
            return poster.getOutlineShape(state);
        return VoxelShapes.fullCube();
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> stateManagerBuilder)
    {
        stateManagerBuilder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new PosterBlockEntity(pos, state);
    }
}
