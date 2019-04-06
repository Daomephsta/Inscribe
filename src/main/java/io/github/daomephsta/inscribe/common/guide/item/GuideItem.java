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
	private static final Identifier INVALID_GUIDE = new Identifier(Inscribe.MOD_ID, "invalid");

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
			if (guide != null)
				MinecraftClient.getInstance().openScreen(new GuideScreen(guide));
			else 
				playerEntity.addChatMessage(new TranslatableTextComponent(Inscribe.MOD_ID + ".chat_message.invalid_guide"), false);
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
		Identifier guideId = getGuideId(itemStack);
		return guideId.getNamespace() + ".guide." + guideId.getPath() + ".name";
	}
	
	public Guide getGuide(ItemStack guideStack)
	{
		return GuideManager.INSTANCE.getGuide(getGuideId(guideStack));
	}

	private Identifier getGuideId(ItemStack guideStack)
	{
		if (!guideStack.hasTag())
			return INVALID_GUIDE;
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
