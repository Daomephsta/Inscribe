package io.github.daomephsta.inscribe.client.guide.xmlformat.util;

import java.util.Collection;
import java.util.Collections;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class XmlItemStack implements StackSetProvider
{
	private final Collection<ItemStack> stacks;

	public XmlItemStack(ItemStack stack)
	{
		this.stacks = Collections.singleton(stack);
	}
	
	public static XmlItemStack fromXml(Element xml)
	{
		Identifier itemId = XmlAttributes.asIdentifier(xml, "id");
		Item item = Registry.ITEM.getOrEmpty(itemId)
			.orElseThrow(() -> new InscribeSyntaxException("Invalid item id " + itemId));
		int count = XmlAttributes.asInt(xml, "count");
		ItemStack stack = new ItemStack(item, count);
		return new XmlItemStack(stack);
	}
	
	@Override
	public Collection<ItemStack> getStacks()
	{
		return stacks;
	}
	
	public ItemStack getStack()
	{
		return stacks.iterator().next();
	}
}
