package io.github.daomephsta.inscribe.common.util;

import java.util.Optional;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEntities
{
    public static <B extends BlockEntity> Optional<B> get(World world, BlockPos pos, Class<B> expected)
    {
        return Optional.ofNullable(world.getBlockEntity(pos))
            .filter(expected::isInstance)
            .map(expected::cast);
    }
}
