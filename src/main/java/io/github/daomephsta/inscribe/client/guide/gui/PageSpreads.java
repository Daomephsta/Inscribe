package io.github.daomephsta.inscribe.client.guide.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.mosaic.flow.Flow.Direction;

class PageSpreads implements Iterable<Pair<GuideFlow, GuideFlow>>
{
    private final List<Pair<GuideFlow, GuideFlow>> spreads;
    private int cursor;

    public PageSpreads(Guide guide, List<GuideFlow> pages)
    {
        this.spreads = new ArrayList<>((int) Math.ceil(pages.size() / 2.0));
        for (Iterator<GuideFlow> iter = pages.iterator(); iter.hasNext();)
        {
            spreads.add(Pair.of(
                iter.next(), 
                iter.hasNext() ? iter.next() : new GuideFlow(guide, Direction.VERTICAL)));
        }
        this.cursor = 0;
    }

    public boolean hasPrevious()
    {
        return cursor > 0;
    }

    public void previous()
    {
        cursor -= 1;
    }

    public boolean hasNext()
    {
        return cursor < spreads.size() - 1;
    }

    public void next()
    {
        cursor += 1;
    }

    public GuideFlow leftPage()
    {
        return spreads.get(cursor).getLeft();
    }

    public GuideFlow rightPage()
    {
        return spreads.get(cursor).getRight();
    }

    @Override
    public Iterator<Pair<GuideFlow, GuideFlow>> iterator()
    {
        return spreads.iterator();
    }
}