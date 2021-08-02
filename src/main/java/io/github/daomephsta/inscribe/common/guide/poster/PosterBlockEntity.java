package io.github.daomephsta.inscribe.common.guide.poster;

import java.util.stream.Stream;

import io.github.daomephsta.inscribe.common.Inscribe;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class PosterBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
    private BlockPos from = BlockPos.ORIGIN,
                     to = BlockPos.ORIGIN;
    private VoxelShape outline = null;
    private Spread spread = null;

    public PosterBlockEntity(BlockPos pos, BlockState state)
    {
        super(Inscribe.POSTER_BLOCK_ENTITY, pos, state);
    }

    public BlockPos getRenderOrigin()
    {
        return from;
    }

    public boolean isRenderOrigin()
    {
        return getPos().equals(from);
    }

    public void setBounds(BlockPos from, BlockPos to)
    {
        this.from = from;
        this.to = to;
    }

    public Stream<BlockPos> positions()
    {
        return BlockPos.stream(from, to);
    }

    public VoxelShape getOutlineShape(BlockState state)
    {
        if (outline == null)
        {
            Direction facing = state.get(Properties.HORIZONTAL_FACING);
            BlockPos offset = getRenderOrigin().subtract(pos);
            this.outline = (switch (facing)
            {
                case NORTH -> Block.createCuboidShape(-16 * 3, 0, 15, 16, 16 * 3, 16);
                case EAST -> Block.createCuboidShape(0, 0, -16 * 3, 1, 16 * 3, 16);
                case SOUTH -> Block.createCuboidShape(0, 0, 0, 16 * 4, 16 * 3, 1);
                case WEST -> Block.createCuboidShape(15, 0, 0, 16, 16 * 3, 16 * 4);
                default -> VoxelShapes.fullCube();
            }).offset(offset.getX(), offset.getY(), offset.getZ());
        }
        return outline;
    }

    public Spread getSpread()
    {
        return spread;
    }

    public void setSpread(Identifier guideId, Identifier partId, int leftPage)
    {
        this.spread = new Spread(guideId, partId, leftPage);
    }

    @Override
    public void fromClientTag(NbtCompound tag)
    {
        readNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        this.from = getBlockPos(tag, "from");
        this.to = getBlockPos(tag, "to");
        super.readNbt(tag);
    }

    private BlockPos getBlockPos(NbtCompound tag, String key)
    {
        NbtCompound posTag = tag.getCompound(key);
        return new BlockPos(posTag.getInt("x"), posTag.getInt("y"), posTag.getInt("z"));
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
        return writeNbt(tag);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        putBlockPos(tag, "from", from);
        putBlockPos(tag, "to", to);
        return super.writeNbt(tag);
    }

    private void putBlockPos(NbtCompound tag, String key, BlockPos pos)
    {
        NbtCompound posTag = new NbtCompound();
        posTag.putInt("x", pos.getX());
        posTag.putInt("y", pos.getY());
        posTag.putInt("z", pos.getZ());
        tag.put(key, posTag);
    }

    public static class Spread
    {
        public Identifier guideId;
        public Identifier partId;
        public int leftPage;

        Spread(Identifier guideId, Identifier partId, int leftPage)
        {
            this.guideId = guideId;
            this.partId = partId;
            this.leftPage = leftPage;
        }
    }
}
