package io.github.daomephsta.inscribe.client.guide.xmlformat.util;

import java.util.Collection;

import net.minecraft.item.ItemStack;

public interface StackSetProvider
{
	public Collection<ItemStack> getStacks();
}
