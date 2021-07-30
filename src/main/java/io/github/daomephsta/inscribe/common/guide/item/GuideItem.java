package io.github.daomephsta.inscribe.common.guide.item;

import org.jetbrains.annotations.Nullable;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.gui.GuideSession;
import io.github.daomephsta.inscribe.client.guide.gui.OpenEntryScreen;
import io.github.daomephsta.inscribe.client.guide.gui.OpenTableOfContentsScreen;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideItemAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.TableOfContents;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
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
            MinecraftClient.getInstance().openScreen(createScreen(stack));
        return new TypedActionResult<>(ActionResult.SUCCESS, stack );
    }

    private Screen createScreen(ItemStack stack)
    {
        Guide guide = getGuide(stack);
        Object lastOpen = getLastOpen(stack);
        if (lastOpen instanceof XmlEntry entry)
            return new OpenEntryScreen(new GuideSession(guide, stack).openEntry(entry));
        else if (lastOpen instanceof TableOfContents toc)
            return new OpenTableOfContentsScreen(new GuideSession(guide, stack).openToC(toc));
        else
        {
            GuideSession session = new GuideSession(guide, stack).openToC(guide.getMainTableOfContents());
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
        if (!guideStack.hasTag() || (guideStack.hasTag() && !guideStack.getTag().contains(GUIDE_ID_TAG)))
            return Guide.INVALID_GUIDE_ID;
        return new Identifier(guideStack.getTag().getString(GUIDE_ID_TAG));
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
        guideStack.setTag(guideTag);
        return guideStack;
    }

    @Nullable
    private Object getLastOpen(ItemStack stack)
    {
        if (stack.getOrCreateTag().contains(LAST_OPEN_TAG))
        {
            Identifier entryId = new Identifier(stack.getTag().getString(LAST_OPEN_TAG));
            XmlEntry entry = getGuide(stack).getEntry(entryId);
            if (entry != null)
                return entry;
            TableOfContents toc = getGuide(stack).getTableOfContents(entryId);
            if (toc != null)
                return toc;
            stack.getTag().remove(LAST_OPEN_TAG);
        }
        return null;
    }
    
    public XmlEntry getLastEntry(ItemStack stack)
    {
        Object lastOpen = getLastOpen(stack);
        if (lastOpen instanceof XmlEntry lastEntry)
            return lastEntry;
        return null;
    }
    
    public TableOfContents getLastToC(ItemStack stack)
    {
        Object lastOpen = getLastOpen(stack);
        if (lastOpen instanceof TableOfContents lastToC)
            return lastToC;
        return null;
    }

    public ItemStack setLastOpen(ItemStack stack, XmlEntry last)
    {
        stack.getOrCreateTag().putString(LAST_OPEN_TAG, last.getId().toString());
        return stack;
    }

    public ItemStack setLastOpen(ItemStack stack, TableOfContents last)
    {
        stack.getOrCreateTag().putString(LAST_OPEN_TAG, last.getId().toString());
        return stack;
    }
}
