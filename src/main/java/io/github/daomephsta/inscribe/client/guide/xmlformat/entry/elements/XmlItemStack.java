package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlGuideGuiElement;
import net.minecraft.item.ItemStack;

public class XmlItemStack implements XmlGuideGuiElement
{
    public final ItemStack stack;

    public XmlItemStack(ItemStack stack)
    {
        this.stack = stack;
    }
}
