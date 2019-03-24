package io.github.daomephsta.inscribe.client.guide.xmlformat.util;

import java.util.Collection;
import java.util.Collections;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlSyntaxException;
import io.github.daomephsta.inscribe.common.Inscribe;
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
		Identifier itemId = Inscribe.ATTRIBUTE_HELPER.asIdentifier(xml, "id");
		Item item = Registry.ITEM.getOrEmpty(itemId)
			.orElseThrow(() -> new XmlSyntaxException("Invalid item id " + itemId));
		int count = Inscribe.ATTRIBUTE_HELPER.asInt(xml, "count");
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
