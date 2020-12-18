package io.github.daomephsta.inscribe.client.guide.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlPage;
import io.github.daomephsta.mosaic.flow.Flow.Direction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.PageTurnWidget;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.DummyProfiler;

public class OpenEntryScreen extends GuideScreen
{
    private final XmlEntry entry;
    private final PageStore pages;
    private AbstractButtonWidget prevPage, nextPage;

    public OpenEntryScreen(Guide guide, XmlEntry entry)
    {
        super(guide);
        this.entry = entry;
        List<GuideFlow> pageFlows = new ArrayList<>(entry.getPages().size());
        for (XmlPage xmlPage : entry.getPages())
        {
            GuideFlow page = new GuideFlow(Direction.VERTICAL);
            page.padding()
                .setHorizontal(13)
                .setVertical(10);
            for (Object element : xmlPage.getContent())
                RenderFormatConverter.convert(page, element);
            pageFlows.add(page);
        }
        this.pages = new PageStore(pageFlows);
    }

    @Override
    public void init(MinecraftClient minecraft, int width, int height)
    {
        super.init(minecraft, width, height);
        int guideHeight = 180;
        int guideWidth = 146;
        int guideTop = 2;
        int guideLeft = (width - guideWidth) / 2;
        for (GuideFlow page : pages)
        {
            page.setLayoutParameters(guideLeft, guideTop, guideWidth, guideHeight);
            page.layoutChildren();
        }
        int controlsX = pages.current().right();
        int controlsY = pages.current().bottom() - 23;
        this.prevPage = addButton(new PageTurnWidget(controlsX, controlsY, false, b ->
            {
                pages.previous();
                updateButtonVisibility();
            }, true));
        this.nextPage = addButton(new PageTurnWidget(controlsX, controlsY - 16, true, b ->
            {
                pages.next();
                updateButtonVisibility();
            }, true));
        updateButtonVisibility();
    }

    private void updateButtonVisibility()
    {
        prevPage.visible = pages.hasPrevious();
        nextPage.visible = pages.hasNext();
    }

    @Override
    public void reopen()
    {
        Guide guide = GuideManager.INSTANCE.getGuide(getOpenGuideId());
        MinecraftClient.getInstance().openScreen(
            new OpenEntryScreen(guide, guide.getEntry(entry.getId())));
    }

    @Override
    public void reloadOpenGuide()
    {
        try
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            GuideManager.INSTANCE.reloadGuide(getOpenGuideId(), CompletableFuture::completedFuture, mc.getResourceManager(),
                DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, Util.getServerWorkerExecutor(), mc)
                .thenAccept(guide -> mc.openScreen(new OpenEntryScreen(guide, guide.getEntry(entry.getId()))));
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
            GuideManager.INSTANCE.reloadEntry(getOpenGuideId(), entry.getFilePath(),
                CompletableFuture::completedFuture, mc.getResourceManager(), DummyProfiler.INSTANCE, DummyProfiler.INSTANCE,
                Util.getServerWorkerExecutor(), mc)
                .thenAccept(entry ->
                {
                    Guide guide = GuideManager.INSTANCE.getGuide(getOpenGuideId());
                    mc.openScreen(new OpenEntryScreen(guide, entry));
                });
        }
        catch (GuideLoadingException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return super.mouseClicked(mouseX, mouseY, button) ||
            pages.current().mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        return super.mouseReleased(mouseX, mouseY, button) ||
            pages.current().mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) ||
            pages.current().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double wheelDelta)
    {
        return mouseScrolled(mouseX, mouseY, wheelDelta) ||
            pages.current().mouseScrolled(mouseX, mouseY, wheelDelta);
    }

    @Override
    public void render(int mouseX, int mouseY, float lastFrameDuration)
    {
        GlStateManager.disableLighting();
        minecraft.getTextureManager().bindTexture(guide.getTheme().getGuiTexture());
        this.blit((width - 146) / 2, 2, 20, 1, 146, 180);
        pages.current().render(mouseX, mouseY, lastFrameDuration, pages.current().contains(mouseX, mouseY));
        super.render(mouseX, mouseY, lastFrameDuration);
        GlStateManager.enableLighting();
    }

    @Override
    public void onClose()
    {
        for (GuideFlow page : pages)
            page.dispose();
        super.onClose();
    }

    Identifier getEntryId()
    {
        return entry.getId();
    }
}
