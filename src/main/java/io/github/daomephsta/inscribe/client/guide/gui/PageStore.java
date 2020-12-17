package io.github.daomephsta.inscribe.client.guide.gui;

import java.util.Iterator;
import java.util.List;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;

class PageStore implements Iterable<GuideFlow>
{
    private final List<GuideFlow> pages;
    private int cursor;

    PageStore(List<GuideFlow> pages)
    {
        this.pages = pages;
        this.cursor = 0;
    }

    public boolean hasPrevious()
    {
        return cursor > 0;
    }

    public GuideFlow previous()
    {
        cursor -= 1;
        return current();
    }

    public GuideFlow current()
    {
        return pages.get(cursor);
    }

    public boolean hasNext()
    {
        return cursor < pages.size() - 1;
    }

    public GuideFlow next()
    {
        cursor += 1;
        return current();
    }

    @Override
    public Iterator<GuideFlow> iterator()
    {
        return pages.iterator();
    }
}