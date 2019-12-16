package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlGuideGuiElement;
import io.github.daomephsta.mosaic.EdgeSpacing;
import io.github.daomephsta.mosaic.Size;
import net.minecraft.item.ItemStack;

public class XmlItemStack implements XmlGuideGuiElement
{
    public final ItemStack stack;
    public final EdgeSpacing padding,
                              margin;
    public final Size size;

    public XmlItemStack(ItemStack stack, EdgeSpacing padding, EdgeSpacing margin, Size size)
    {
        this.stack = stack;
        this.padding = padding;
        this.margin = margin;
        this.size = size;
    }
}
