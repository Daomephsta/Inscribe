package io.github.daomephsta.inscribe.client.guide.xmlformat.util;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.item.ItemStack;

public class XmlItemStack implements StackSetProvider
{
	private final Collection<ItemStack> stacks;

	public XmlItemStack(ItemStack stack)
	{
		this.stacks = Collections.singleton(stack);
	}

	/* TODO: Extract to separate class
	 * public static XmlItemStack fromXml(Element xml)
	{
		Identifier itemId = XmlAttributes.asIdentifier(xml, "id");
		Item item = Registry.ITEM.getOrEmpty(itemId)
			.orElseThrow(() -> new InscribeSyntaxException("Invalid item id " + itemId));
		int count = XmlAttributes.asInt(xml, "count");
		ItemStack stack = new ItemStack(item, count);
		return new XmlItemStack(stack);
	}*/

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
