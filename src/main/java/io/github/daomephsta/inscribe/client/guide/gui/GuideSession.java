package io.github.daomephsta.inscribe.client.guide.gui;

import java.util.ArrayDeque;
import java.util.Deque;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.xmlformat.GuidePart;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class GuideSession
{
    private static final int MAX_HISTORY_SIZE = 6;
    private Guide guide;
    private final ItemStack guideStack;
    private final Deque<GuidePart> history;

    public GuideSession(Guide guide, ItemStack guideStack)
    {
        this.guide = guide;
        this.guideStack = guideStack;
        this.history = new ArrayDeque<>(MAX_HISTORY_SIZE);
    }

    public GuideSession reload()
    {
        this.guide = GuideManager.INSTANCE.getGuide(guide.getIdentifier());
        for (int i = 0; i < history.size(); i++)
        {
            GuidePart part = history.pop();
            Guide partGuide = GuideManager.INSTANCE.getGuide(part.getFilePath().getGuideId());
            history.addLast(partGuide.getPart(part.getId()));
        }
        return this;
    }

    public Guide getGuide()
    {
        return guide;
    }

    public void openLast()
    {
        if (!hasHistory())
            return;
        // Close open part
        history.pop();
        GuidePart last = history.pop();
        MinecraftClient.getInstance().setScreen(last.toScreen(this));
    }

    public boolean hasHistory()
    {
        return history.size() > 1;
    }

    public GuideSession open(GuidePart part)
    {
        if (history.size() == MAX_HISTORY_SIZE)
            history.removeLast();
        history.push(part);
        return this;
    }

    public GuidePart getOpenPart()
    {
        return history.peek();
    }

    public void end()
    {
        if (!history.isEmpty())
            Inscribe.GUIDE_ITEM.setLastOpen(guideStack, history.pop());
    }
}
