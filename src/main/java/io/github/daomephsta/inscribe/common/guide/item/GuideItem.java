package io.github.daomephsta.inscribe.common.guide.item;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideItemAccessMethod;
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
				GuideAccessMethod accessMethod = guide.getDefinition().getGuideAccess();
				if (accessMethod instanceof GuideItemAccessMethod
					&& ((GuideItemAccessMethod) accessMethod).getItemGroup() == itemGroup)
				{
					items.add(this.forGuide(guide));
				}
			}
		}
	}
	
	@Override
	public String getTranslationKey(ItemStack itemStack)
	{
		Identifier guideId = getGuideId(itemStack);
		return guideId.getNamespace() + ".guide." + guideId.getPath() + ".name";
	}
	
	private Guide getGuide(ItemStack guideStack)
	{
		return GuideManager.INSTANCE.getGuide(getGuideId(guideStack));
	}

	public Identifier getGuideId(ItemStack guideStack)
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
