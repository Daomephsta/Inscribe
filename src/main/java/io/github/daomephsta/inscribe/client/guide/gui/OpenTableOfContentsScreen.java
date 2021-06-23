package io.github.daomephsta.inscribe.client.guide.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.DummyProfiler;

public class OpenTableOfContentsScreen extends PageSpreadScreen
{
    private final TableOfContents toc;

    public OpenTableOfContentsScreen(Guide guide, ItemStack guideStack, TableOfContents toc)
    {
        super(guide, guideStack);
        this.toc = toc;
    }

    @Override
    protected List<GuideFlow> buildPages()
    {
        List<GuideFlow> pageFlows = new ArrayList<>();

        GuideFlow page = addPage(pageFlows);
        GuideFlow column = addColumn(page);
        int usedColumns = 1;
        int usedY = 0;
        for (Iterator<Link> iter = toc.getLinks().iterator(); iter.hasNext();)
        {
            Link link = iter.next();
            GuideWidget linkElement = createLinkElement(link);
            linkElement.attach(new GotoEntry(link.destination));
            linkElement.margin().setVertical(2);
            usedY += linkElement.hintHeight();
            column.add(linkElement);
            if (usedY > 138 && iter.hasNext())
            {
                usedY = 0;
                if (usedColumns < toc.getColumns())
                {
                    column = addColumn(page);
                    usedColumns += 1;
                }
                else
                {
                    page = addPage(pageFlows);
                    column = addColumn(page);
                    usedColumns = 1;
                }
            }
        }

        return pageFlows;
    }

    private GuideFlow addPage(List<GuideFlow> pageFlows)
    {
        GuideFlow page = new GuideFlow(Direction.HORIZONTAL);
        page.padding().setVertical(2).setHorizontal(4);
        pageFlows.add(page);
        return page;
    }

    private GuideFlow addColumn(GuideFlow columns)
    {
        GuideFlow column = new GuideFlow(Direction.VERTICAL);
        columns.add(column, layout ->
        {
            layout.setPreferredSize(SizeConstraint.percentage(100D / toc.getColumns()));
        });
        return column;
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
    public void reopen()
    {
        Guide guide = GuideManager.INSTANCE.getGuide(getOpenGuideId());
        MinecraftClient.getInstance().openScreen(
            new OpenTableOfContentsScreen(guide, guideStack, guide.getMainTableOfContents()));
    }

    @Override
    public void reloadOpenGuide()
    {
        try
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            GuideManager.INSTANCE.reloadGuide(getOpenGuideId(), CompletableFuture::completedFuture, mc.getResourceManager(),
                DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, Util.getServerWorkerExecutor(), mc)
                .thenAccept(guide -> mc.openScreen(new OpenTableOfContentsScreen(guide, guideStack, guide.getMainTableOfContents())));
        }
        catch (GuideLoadingException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadOpenEntry()
    {
        try
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            GuideManager.INSTANCE.reloadTableOfContents(toc, CompletableFuture::completedFuture,
                mc.getResourceManager(), DummyProfiler.INSTANCE, DummyProfiler.INSTANCE,
                Util.getServerWorkerExecutor(), mc)
            .thenAccept(toc ->
            {
                Guide guide = GuideManager.INSTANCE.getGuide(getOpenGuideId());
                mc.openScreen(new OpenTableOfContentsScreen(guide, guideStack, toc));
            });
        }
        catch (GuideLoadingException e)
        {
            e.printStackTrace();
        }
    }
}
