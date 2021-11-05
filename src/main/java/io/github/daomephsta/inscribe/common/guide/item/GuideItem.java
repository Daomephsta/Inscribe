package io.github.daomephsta.inscribe.common.guide.item;

import org.jetbrains.annotations.Nullable;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.gui.GuideSession;
import io.github.daomephsta.inscribe.client.guide.gui.OpenTableOfContentsScreen;
import io.github.daomephsta.inscribe.client.guide.xmlformat.GuidePart;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideItemAccessMethod;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class GuideItem extends Item
{
    public static final Identifier INVALID_GUIDE = new Identifier(Inscribe.MOD_ID, "invalid");
    private static final String GUIDE_ID_TAG = "guide_id",
                                LAST_OPEN_TAG = "last_open";

    public GuideItem()
    {
        super(new Settings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand)
    {
        ItemStack stack = playerEntity.getStackInHand(hand);
        if (world.isClient)
            MinecraftClient.getInstance().setScreen(createScreen(stack));
        return new TypedActionResult<>(ActionResult.SUCCESS, stack );
    }

    private Screen createScreen(ItemStack stack)
    {
        Guide guide = getGuide(stack);
        GuidePart lastOpen = getLastOpen(stack);
        if (lastOpen != null)
            return lastOpen.toScreen(new GuideSession(guide, stack));
        else
        {
            GuideSession session = new GuideSession(guide, stack).open(guide.getMainTableOfContents());
            return new OpenTableOfContentsScreen(session);
        }
    }

    @Override
    public void appendStacks(ItemGroup itemGroup, DefaultedList<ItemStack> items)
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
                GuideAccessMethod accessMethod = guide.getAccessMethod();
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
        return getGuide(itemStack).getTranslationKey();
    }

    public Guide getGuide(ItemStack guideStack)
    {
        return GuideManager.INSTANCE.getGuide(getGuideId(guideStack));
    }

    public Identifier getGuideId(ItemStack guideStack)
    {
        if (!guideStack.hasNbt() || (guideStack.hasNbt() && !guideStack.getNbt().contains(GUIDE_ID_TAG)))
            return Guide.INVALID_GUIDE_ID;
        return new Identifier(guideStack.getNbt().getString(GUIDE_ID_TAG));
    }

    public ItemStack forGuide(Guide guide)
    {
        return forGuide(guide.getIdentifier());
    }

    public ItemStack forGuide(Identifier id)
    {
        ItemStack guideStack = new ItemStack(this);
        NbtCompound guideTag = new NbtCompound();
        guideTag.putString(GUIDE_ID_TAG, id.toString());
        guideStack.setNbt(guideTag);
        return guideStack;
    }

    @Nullable
    private GuidePart getLastOpen(ItemStack stack)
    {
        if (stack.getOrCreateNbt().contains(LAST_OPEN_TAG))
        {
            Identifier partId = new Identifier(stack.getNbt().getString(LAST_OPEN_TAG));
            GuidePart lastOpen = getGuide(stack).getPart(partId);
            if (lastOpen != null)
                return lastOpen;
            else
                stack.getNbt().remove(LAST_OPEN_TAG);
        }
        return null;
    }

    public Identifier getLastOpenId(ItemStack stack)
    {
        GuidePart lastOpen = getLastOpen(stack);
        return lastOpen != null ? lastOpen.getId() : null;
    }

    public ItemStack setLastOpen(ItemStack stack, GuidePart last)
    {
        stack.getOrCreateNbt().putString(LAST_OPEN_TAG, last.getId().toString());
        return stack;
    }
}
