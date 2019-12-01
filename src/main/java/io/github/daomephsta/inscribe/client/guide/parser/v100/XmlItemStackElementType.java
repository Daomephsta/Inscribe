package io.github.daomephsta.inscribe.client.guide.parser.v100;

import org.jdom2.Element;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlItemStack;
import net.minecraft.command.arguments.ItemStringReader;
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
            ItemStringReader itemStringReader = new ItemStringReader(new StringReader(itemString), false).consume();
            int amount = XmlAttributes.asInt(xml, "amount", 1);
            ItemStack stack = new ItemStack(itemStringReader.getItem(), amount);
            stack.setTag(itemStringReader.getTag());
            return new XmlItemStack(stack);
        }
        catch (CommandSyntaxException e)
        {
            throw new InscribeSyntaxException("Failed to parse itemstack", e);
        }
    }
}