package io.github.daomephsta.inscribe.common.guide.poster;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;

public class PosterItem extends BlockItem
{
    public PosterItem(Block block)
    {
        super(block, new Settings().group(ItemGroup.DECORATIONS));
    }
}
