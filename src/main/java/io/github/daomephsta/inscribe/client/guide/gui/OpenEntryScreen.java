package io.github.daomephsta.inscribe.client.guide.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlPage;
import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.mosaic.flow.Flow.Direction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.DummyProfiler;

public class OpenEntryScreen extends PageSpreadScreen
{
    private final XmlEntry entry;

    public OpenEntryScreen(Guide guide, ItemStack guideStack, XmlEntry entry)
    {
        super(guide, guideStack);
        this.entry = entry;
    }

    @Override
    protected List<GuideFlow> buildPages()
    {
        List<GuideFlow> pageFlows = new ArrayList<>(entry.getPages().size());
        for (XmlPage xmlPage : entry.getPages())
        {
            GuideFlow page = new GuideFlow(guide, Direction.VERTICAL);
            page.padding().setAll(4);
            for (Object element : xmlPage.getContent())
                RenderFormatConverter.convert(page, element);
            pageFlows.add(page);
        }
        return pageFlows;
    }

    @Override
    public void onClose()
    {
        Inscribe.GUIDE_ITEM.setLastEntry(guideStack, entry);
        super.onClose();
    }

    @Override
    public void reopen()
    {
        Guide guide = GuideManager.INSTANCE.getGuide(getOpenGuideId());
        MinecraftClient.getInstance().openScreen(
            new OpenEntryScreen(guide, guideStack, guide.getEntry(entry.getId())));
    }

    @Override
    public void reloadOpenGuide()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        GuideManager.INSTANCE.reloadGuide(getOpenGuideId(), CompletableFuture::completedFuture, mc.getResourceManager(),
            DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, Util.getMainWorkerExecutor(), mc)
            .thenAccept(guide -> mc.openScreen(new OpenEntryScreen(guide, guideStack, guide.getEntry(entry.getId()))));
    }

    @Override
    public void reloadOpenEntry()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        GuideManager.INSTANCE.reloadEntry(entry, CompletableFuture::completedFuture, mc.getResourceManager(),
            DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, Util.getMainWorkerExecutor(), mc)
        .thenAccept(entry ->
        {
            Guide guide = GuideManager.INSTANCE.getGuide(getOpenGuideId());
            mc.openScreen(new OpenEntryScreen(guide, guideStack, entry));
        });
    }

    Identifier getEntryId()
    {
        return entry.getId();
    }
}
