package io.github.daomephsta.inscribe.client.guide.parser.v100;

import org.w3c.dom.Element;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlItemStack;
import io.github.daomephsta.mosaic.EdgeSpacing;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.ItemStack;

final class XmlItemStackElementType extends XmlElementType<XmlItemStack>
{
    XmlItemStackElementType()
    {
        super("itemstack", XmlItemStack.class);
    }

    @Override
    public XmlItemStack fromXml(Element xml) throws GuideLoadingException
    {
        try
        {
            String itemId = XmlAttributes.getValue(xml, "item");
            String itemString = itemId + XmlAttributes.getValue(xml, "tag", "");
            ItemStringReader itemReader = new ItemStringReader(new StringReader(itemString), false).consume();
            int amount = XmlAttributes.asInt(xml, "amount", 1);
            ItemStack stack = new ItemStack(itemReader.getItem(), amount);
            stack.setTag(itemReader.getNbt());
            EdgeSpacing padding = LayoutParameters.readPadding(xml);
            EdgeSpacing margin = LayoutParameters.readMargin(xml);
            return new XmlItemStack(stack, padding, margin, LayoutParameters.readSize(xml));
        }
        catch (CommandSyntaxException e)
        {
            throw new InscribeSyntaxException("Failed to parse itemstack", e);
        }
    }
}