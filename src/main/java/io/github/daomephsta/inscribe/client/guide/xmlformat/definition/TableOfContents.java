package io.github.daomephsta.inscribe.client.guide.xmlformat.definition;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

import io.github.daomephsta.inscribe.api.GuideFlags;
import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideIdentifier;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.LinkStyle;
import io.github.daomephsta.inscribe.client.guide.gui.GuideSession;
import io.github.daomephsta.inscribe.client.guide.gui.OpenTableOfContentsScreen;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.xmlformat.GuidePart;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public class TableOfContents implements GuidePart
{
    private final Identifier id;
    private final GuideIdentifier filePath;
    private final List<Link> links;
    private final int columns;

    public TableOfContents(Identifier id, GuideIdentifier filePath, List<Link> links, int columns)
    {
        this.id = id;
        this.filePath = filePath;
        this.links = ImmutableList.copyOf(links);
        this.columns = columns;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public GuideIdentifier getFilePath()
    {
        return filePath;
    }

    public Iterable<Link> getLinks()
    {
        return () -> links.stream().filter(link -> link.visible(this)).iterator();
    }

    public int getColumns()
    {
        return columns;
    }

    @Override
    public Screen toScreen(GuideSession session)
    {
        return new OpenTableOfContentsScreen(session.open(this));
    }
    
    @Override
    public String toString()
    {
        return String.format("TableOfContents %s", id);
    }

    public static class Link
    {
        public final String name;
        public final Identifier destination;
        public final LinkStyle style;
        private final Consumer<GuideFlow> iconFactory;
        private final Predicate<GuideFlags> visible;

        public Link(Consumer<GuideFlow> iconFactory, String name, Identifier destination, LinkStyle style,
            Predicate<GuideFlags> visible)
        {
            this.iconFactory = iconFactory;
            this.name = name;
            this.destination = destination;
            this.style = style;
            this.visible = visible;
        }

        public boolean visible(TableOfContents parent)
        {
            Guide guide = GuideManager.INSTANCE.getGuide(parent.getFilePath().getGuideId());
            return visible.test(guide.getFlags());
        }

        public void addIcon(GuideFlow output)
        {
            if (iconFactory == null)
                throw new NullPointerException("Icon not present");
            iconFactory.accept(output);
        }
    }
}
