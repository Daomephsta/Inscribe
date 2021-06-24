package io.github.daomephsta.inscribe.common.guide.poster;

import java.util.stream.Stream;

import io.github.daomephsta.inscribe.common.Inscribe;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PosterBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
    private BlockPos from = BlockPos.ORIGIN,
                     to = BlockPos.ORIGIN;
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

    public Spread getSpread()
    {
        return spread;
    }

    public void setSpread(Identifier guideId, Identifier entryId, int leftPage)
    {
        this.spread = new Spread(guideId, entryId, leftPage);
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
        public Identifier entryId;
        public int leftPage;

        Spread(Identifier guideId, Identifier entryId, int leftPage)
        {
            this.guideId = guideId;
            this.entryId = entryId;
            this.leftPage = leftPage;
        }
    }
}
