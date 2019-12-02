package io.github.daomephsta.inscribe.client.guide.xmlformat.definition;

import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;

import io.github.daomephsta.inscribe.client.guide.LinkStyle;
import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import net.minecraft.util.Identifier;

public class TableOfContents
{
    private final List<Link> links;

    public TableOfContents(List<Link> links)
    {
        this.links = ImmutableList.copyOf(links);
    }

    public Iterable<Link> getLinks()
    {
        return links;
    }

    public static class Link
    {
        public final String name;
        public final Identifier destination;
        public final LinkStyle style;
        private final Supplier<GuideWidget> iconFactory;

        public Link(Supplier<GuideWidget> iconFactory, String name, Identifier destination, LinkStyle style)
        {
            this.iconFactory = iconFactory;
            this.name = name;
            this.destination = destination;
            this.style = style;
        }

        public GuideWidget getIcon()
        {
            if (iconFactory == null)
                throw new NullPointerException("Icon not present");
            return iconFactory.get();
        }
    }
}
