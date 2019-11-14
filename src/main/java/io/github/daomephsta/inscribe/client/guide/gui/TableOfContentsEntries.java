package io.github.daomephsta.inscribe.client.guide.gui;

import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.LabelWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.component.Tooltip;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.TableOfContents;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.TableOfContents.Link;
import io.github.daomephsta.mosaic.Size;
import io.github.daomephsta.mosaic.flow.Flow.Direction;

public class TableOfContentsEntries implements VisibleContent
{
    private final GuideFlow root;

	public TableOfContentsEntries(TableOfContents toc, int x, int y, int width, int height)
	{
        this.root = new GuideFlow(Direction.VERTICAL);
		root.setLayoutParameters(x, y, width, height);
		root.padding().setLeft(13).setTop(10);
		GuideFlow links = new GuideFlow(Direction.VERTICAL);
		for (Link link : toc.getLinks())
		{
		    GuideWidget linkElement = createLinkElement(link);
		    linkElement.margin().setVertical(2);
            links.add(linkElement);
		}
		root.add(links, d -> d.setSize(Size.percentage(100)));
		root.layoutChildren();
	}

	private GuideWidget createLinkElement(Link link)
	{
		switch (link.style)
		{
		case ICON_WITH_TEXT:
		{
			GuideFlow linkElement = new GuideFlow(Direction.HORIZONTAL);
			//TODO make copy
			GuideWidget icon = link.getIcon();

			linkElement.add(icon, d -> d.setSize(Size.pixels(16)));
			LabelWidget label = new LabelWidget(link.name, Alignment.CENTER, Alignment.CENTER, 0x000000);
			label.margin().setLeft(1);
			linkElement.add(label);
			return linkElement;
		}
		case ICON_WITH_TOOLTIP:
		{
			GuideWidget linkElement = link.getIcon();
			linkElement.attach(new Tooltip(tooltip -> tooltip.accept(link.name)));
			return linkElement;
		}
		case TEXT:
			return new LabelWidget(link.name, Alignment.LEADING, Alignment.CENTER, 0x000000);
		default:
			throw new IllegalArgumentException("Unknown link style " + link.style);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float lastFrameDuration)
	{
	    root.render(mouseX, mouseY, lastFrameDuration);
	}
}
