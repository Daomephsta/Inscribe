package io.github.daomephsta.inscribe.common.guide.poster;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class PosterBlock extends Block implements BlockEntityProvider
{
    private static final VoxelShape NORTH_RAYSHAPE = Block.createCuboidShape(0, 0, 16, 16, 16, 15),
                                    EAST_HITSHAPE = Block.createCuboidShape(0, 0, 0, 1, 16, 16),
                                    SOUTH_HITSHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 1),
                                    WEST_HITSHAPE = Block.createCuboidShape(16, 0, 0, 15, 16, 16);

    public PosterBlock()
    {
        super(FabricBlockSettings.of(Material.CARPET, MaterialColor.WHITE)
            .breakByHand(true)
            .breakInstantly()
            .nonOpaque()
            .build());
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
    public void onBlockRemoved(BlockState replacement, World world, BlockPos pos, BlockState removed, boolean boolean_1)
    {
        // Judging by Mojang code, sometimes the BE is null, or some other BE
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PosterBlockEntity)
        {
            PosterBlockEntity poster = (PosterBlockEntity) blockEntity;
            Iterable<BlockPos> positions = poster.positions()::iterator;
            for (BlockPos board : positions)
                world.setBlockState(board, Blocks.AIR.getDefaultState());
        }
        super.onBlockRemoved(replacement, world, pos, removed, boolean_1);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView blockView, BlockPos pos, EntityContext context)
    {
        switch (state.get(Properties.HORIZONTAL_FACING))
        {
        case NORTH:
            return NORTH_RAYSHAPE;
        case EAST:
            return EAST_HITSHAPE;
        case SOUTH:
            return SOUTH_HITSHAPE;
        case WEST:
            return WEST_HITSHAPE;
        default:
            return VoxelShapes.fullCube();
        }
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> stateManagerBuilder)
    {
        stateManagerBuilder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView)
    {
        return new PosterBlockEntity();
    }
}
