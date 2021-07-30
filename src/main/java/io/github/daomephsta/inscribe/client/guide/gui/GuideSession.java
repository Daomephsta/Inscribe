package io.github.daomephsta.inscribe.client.guide.gui;

import java.util.ArrayDeque;
import java.util.Deque;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.TableOfContents;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class GuideSession
{
    private static final int MAX_HISTORY_SIZE = 6;
    private Guide guide;
    private final ItemStack guideStack;
    private final Deque<Object> history;

    public GuideSession(Guide guide, ItemStack guideStack)
    {
        this.guide = guide;
        this.guideStack = guideStack;
        this.history = new ArrayDeque<>(MAX_HISTORY_SIZE);
    }
    
    public GuideSession reload()
    {
        this.guide = GuideManager.INSTANCE.getGuide(getGuide().getIdentifier());
        for (int i = 0; i < history.size(); i++)
        {
            Object element = history.pop();
            if (element instanceof XmlEntry entry)
                history.addLast(getGuide().getEntry(entry.getId()));
            else if (element instanceof TableOfContents toc)
                history.addLast(getGuide().getTableOfContents(toc.getId()));
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
        // Close current opened entry
        history.pop();
        Object last = history.peek();
        if (last instanceof XmlEntry entry)
            MinecraftClient.getInstance().openScreen(new OpenEntryScreen(this));
        else if (last instanceof TableOfContents toc)
            MinecraftClient.getInstance().openScreen(new OpenTableOfContentsScreen(this));
    }

    public boolean hasHistory()
    {
        return history.size() > 1;
    }
    
    public GuideSession openEntry(XmlEntry entry)
    {
        open(entry);
        return this;
    }

    public GuideSession openToC(TableOfContents toc)
    {
        open(toc);
        return this;
    }

    private void open(Object element)
    {
        if (history.size() == MAX_HISTORY_SIZE)
            history.removeLast();
        history.push(element);
    }
    
    public XmlEntry getOpenEntry()
    {
        if (history.peek() instanceof XmlEntry entry)
            return entry;
        throw new IllegalStateException("Entry expected");
    }

    public TableOfContents getOpenToC()
    {
        if (history.peek() instanceof TableOfContents toc)
            return toc;
        throw new IllegalStateException("Table of contents expected");
    }

    public void end()
    {
        if (!history.isEmpty())
        {
            Object last = history.pop();
            if (last instanceof XmlEntry entry)
                Inscribe.GUIDE_ITEM.setLastOpen(guideStack, entry);
            else if (last instanceof TableOfContents toc)
                Inscribe.GUIDE_ITEM.setLastOpen(guideStack, toc);
        }
    }
}
