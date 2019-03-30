package io.github.daomephsta.inscribe.common.guide.item;

import java.util.Arrays;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.common.guide.xmlformat.ItemSpecification;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;

public class GuideItem extends Item
{
	public GuideItem()
	{
		super(new Settings().stackSize(1));
	}

	@Override
	public void appendItemsForGroup(ItemGroup itemGroup, DefaultedList<ItemStack> items)
	{
		if (itemGroup == ItemGroup.SEARCH)
		{
			for (Guide guide : GuideManager.INSTANCE.getGuides())
			{
				items.add(this.forGuide(guide));
			}
		}
		else
		{
			for (Guide guide : GuideManager.INSTANCE.getGuides())
			{
				if (itemGroup == getGuideItemGroup(guide))
					items.add(this.forGuide(guide));
			}
		}
	}

	private ItemGroup getGuideItemGroup(Guide guide)
	{
		return guide.getItemSpecification()
			.filter(ItemSpecification.Standard.class::isInstance)
			.map(spec -> 
			{
				String itemGroupId = ((ItemSpecification.Standard) spec).getItemGroupId();
				return Arrays.stream(ItemGroup.GROUPS)
					.filter(group -> group.getId().equals(itemGroupId))
					.findFirst().get();
			})
			.orElse(null);
	}
	
	public Identifier getGuideIdentifier(ItemStack guideStack)
	{
		return new Identifier(guideStack.getTag().getString("guide_id"));
	}
	
	private ItemStack forGuide(Guide guide)
	{
		ItemStack guideStack = new ItemStack(this);
		CompoundTag guideTag = new CompoundTag();
		guideTag.putString("guide_id", guide.getIdentifier().toString());
		guideStack.setTag(guideTag);
		return guideStack;
	}
}
