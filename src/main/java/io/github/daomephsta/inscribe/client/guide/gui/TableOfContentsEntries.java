package io.github.daomephsta.inscribe.client.guide.gui;

import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.component.GotoEntry;
import io.github.daomephsta.inscribe.client.guide.gui.widget.component.Tooltip;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.FormattedTextNode;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.LabelWidget;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.TableOfContents;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.TableOfContents.Link;
import io.github.daomephsta.mosaic.SizeConstraint;
import io.github.daomephsta.mosaic.flow.Flow.Direction;
import net.minecraft.client.MinecraftClient;

public class TableOfContentsEntries implements VisibleContent
{
    private final GuideFlow root;

    public TableOfContentsEntries(TableOfContents toc)
    {
        this.root = new GuideFlow(Direction.VERTICAL);
        root.padding().setLeft(13).setTop(10);
        GuideFlow links = new GuideFlow(Direction.VERTICAL);
        for (Link link : toc.getLinks())
        {
            GuideWidget linkElement = createLinkElement(link);
            linkElement.attach(new GotoEntry(link.destination));
            linkElement.margin().setVertical(2);
            links.add(linkElement);
        }
        root.add(links, d -> d.setPreferredSize(SizeConstraint.percentage(100)));
    }

    @Override
    public void setRenderArea(int x, int y, int width, int height)
    {
        root.setLayoutParameters(x, y, width, height);
        root.layoutChildren();
    }

    private GuideWidget createLinkElement(Link link)
    {
        switch (link.style)
        {
        case ICON_WITH_TEXT:
        {
            GuideFlow linkElement = new GuideFlow(Direction.HORIZONTAL);
            link.addIcon(linkElement);
            LabelWidget label = new LabelWidget(
                new FormattedTextNode(link.name, MinecraftClient.DEFAULT_TEXT_RENDERER_ID, 0x000000),
                Alignment.CENTER, Alignment.CENTER, 1.0F);
            label.margin().setLeft(1);
            linkElement.add(label);
            return linkElement;
        }
        case ICON_WITH_TOOLTIP:
        {
            GuideFlow linkElement = new GuideFlow(Direction.HORIZONTAL);
            link.addIcon(linkElement);
            linkElement.attach(new Tooltip(tooltip -> tooltip.accept(link.name)));
            return linkElement;
        }
        case TEXT:
            return new LabelWidget(
                new FormattedTextNode(link.name, MinecraftClient.DEFAULT_TEXT_RENDERER_ID, 0x000000),
                Alignment.LEADING, Alignment.CENTER, 1.0F);
        default:
            throw new IllegalArgumentException("Unknown link style " + link.style);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float lastFrameDuration, boolean mouseOver)
    {
        root.render(mouseX, mouseY, lastFrameDuration, root.contains(mouseX, mouseY));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        root.mouseClicked(mouseX, mouseY, button);
        return VisibleContent.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose()
    {
        root.dispose();
    }
}
