package io.github.daomephsta.inscribe.common.guide.poster;

import java.util.stream.Stream;

import io.github.daomephsta.inscribe.common.Inscribe;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public class PosterBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
    private BlockPos from = BlockPos.ORIGIN,
                     to = BlockPos.ORIGIN;

    public PosterBlockEntity()
    {
        super(Inscribe.POSTER_BLOCK_ENTITY);
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

    @Override
    public void fromClientTag(CompoundTag tag)
    {
        fromTag(tag);
    }

    @Override
    public void fromTag(CompoundTag tag)
    {
        this.from = getBlockPos(tag, "from");
        this.to = getBlockPos(tag, "to");
        super.fromTag(tag);
    }

    private BlockPos getBlockPos(CompoundTag tag, String key)
    {
        CompoundTag posTag = tag.getCompound(key);
        return new BlockPos(posTag.getInt("x"), posTag.getInt("y"), posTag.getInt("z"));
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag)
    {
        return toTag(tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag)
    {
        putBlockPos(tag, "from", from);
        putBlockPos(tag, "to", to);
        return super.toTag(tag);
    }

    private void putBlockPos(CompoundTag tag, String key, BlockPos pos)
    {
        CompoundTag posTag = new CompoundTag();
        posTag.putInt("x", pos.getX());
        posTag.putInt("y", pos.getY());
        posTag.putInt("z", pos.getZ());
        tag.put(key, posTag);
    }
}
