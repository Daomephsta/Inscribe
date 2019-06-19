package io.github.daomephsta.inscribe.common.guide.item;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.gui.GuideScreen;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideItemAccessMethod;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class GuideItem extends Item
{
	public static final Identifier INVALID_GUIDE = new Identifier(Inscribe.MOD_ID, "invalid");
	private static final String GUIDE_ID_TAG = "guide_id";

	public GuideItem()
	{
		super(new Settings().stackSize(1));
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand)
	{
		ItemStack stack = playerEntity.getStackInHand(hand);
		if (world.isClient)
		{
			Guide guide = getGuide(stack);
			MinecraftClient.getInstance().openScreen(new GuideScreen(guide));
		}
		return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, stack );
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
				GuideAccessMethod accessMethod = guide.getDefinition().getAccessMethod();
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
		return getGuide(itemStack).getDefinition().getTranslationKey();
	}
	
	public Guide getGuide(ItemStack guideStack)
	{
		return GuideManager.INSTANCE.getGuide(getGuideId(guideStack));
	}

	private Identifier getGuideId(ItemStack guideStack)
	{
		if (!guideStack.hasTag() || (guideStack.hasTag() && !guideStack.getTag().containsKey(GUIDE_ID_TAG)))
			return Guide.INVALID_GUIDE_ID;
		return new Identifier(guideStack.getTag().getString(GUIDE_ID_TAG));
	}
	
	private ItemStack forGuide(Guide guide)
	{
		ItemStack guideStack = new ItemStack(this);
		CompoundTag guideTag = new CompoundTag();
		guideTag.putString(GUIDE_ID_TAG, guide.getIdentifier().toString());
		guideStack.setTag(guideTag);
		return guideStack;
	}
}
