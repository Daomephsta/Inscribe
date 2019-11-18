package io.github.daomephsta.inscribe.client.guide.parser.v100;

import org.jdom2.Element;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes.Preconditions;
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
	protected void configurePreconditions(Preconditions attributePreconditions)
	{
		attributePreconditions.required("item").optional("tag", "amount");
	}

	@Override
	protected XmlItemStack translate(Element xml) throws GuideLoadingException
	{
		try
		{
			String itemId = XmlAttributes.getValue(xml, "item");
			String itemString = itemId + XmlAttributes.asOptionalString(xml, "tag").orElse("");
			ItemStringReader itemStringReader = new ItemStringReader(new StringReader(itemString), false).consume();
			int amount = XmlAttributes.asOptionalInt(xml, "amount").orElse(1);
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