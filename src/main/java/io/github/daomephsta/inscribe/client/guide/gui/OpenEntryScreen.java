package io.github.daomephsta.inscribe.client.guide.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlPage;
import io.github.daomephsta.mosaic.flow.Flow.Direction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.DummyProfiler;

public class OpenEntryScreen extends PageSpreadScreen
{
    private final XmlEntry entry;

    public OpenEntryScreen(GuideSession session)
    {
        super(session);
        if (session.getOpenPart() instanceof XmlEntry entry)
            this.entry = entry;
        else
            throw new IllegalStateException("Expected entry as open part");
    }

    @Override
    protected List<GuideFlow> buildPages()
    {
        List<GuideFlow> pageFlows = new ArrayList<>(entry.getPages().size());
        for (XmlPage xmlPage : entry.getPages())
        {
            GuideFlow page = new GuideFlow(session.getGuide(), Direction.VERTICAL);
            page.padding().setAll(4);
            for (Object element : xmlPage.getContent())
                RenderFormatConverter.convert(page, element);
            pageFlows.add(page);
        }
        return pageFlows;
    }

    @Override
    public void reopen()
    {
        MinecraftClient.getInstance().openScreen(new OpenEntryScreen(session.reload()));
    }

    @Override
    public void reloadOpenPart()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        GuideManager.INSTANCE.reloadEntry(entry, CompletableFuture::completedFuture, mc.getResourceManager(),
            DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, Util.getMainWorkerExecutor(), mc)
        .thenRun(this::reopen);
    }

    Identifier getEntryId()
    {
        return entry.getId();
    }
}
