package io.github.daomephsta.inscribe.client.guide.gui;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.common.util.Identifiers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public abstract class PageSpreadScreen extends Screen implements GuideGui
{
    private static final Logger LOGGER = LogManager.getLogger();
    protected final Guide guide;
    private PageSpreads pageSpreads;
    private AbstractButtonWidget prevPage, nextPage;

    public PageSpreadScreen(Guide guide)
    {
        super(new TranslatableText(guide.getTranslationKey()));
        this.guide = guide;
    }

    protected abstract List<GuideFlow> buildPages();

    @Override
    public void init(MinecraftClient minecraft, int width, int height)
    {
        super.init(minecraft, width, height);
        this.pageSpreads = new PageSpreads(buildPages());
        int guideHeight = 232;
        int pageWidth = 176;
        int guideTop = (height - guideHeight) / 2;
        int guideLeft = width / 2 - pageWidth;
        for (Pair<GuideFlow, GuideFlow> spread : pageSpreads)
        {
            spread.getLeft().setLayoutParameters(guideLeft, guideTop, pageWidth, guideHeight);
            spread.getLeft().layoutChildren();
            spread.getRight().setLayoutParameters(guideLeft + pageWidth + 4, guideTop, pageWidth, guideHeight);
            spread.getRight().layoutChildren();
        }
        int controlsX = pageSpreads.rightPage().right() + 13;
        int controlsY = pageSpreads.rightPage().bottom() - 21;
        this.prevPage = addButton(new TexturedButtonWidget(controlsX, controlsY, 18, 18, 402, 211, 0, guide.getTheme().getGuiTexture(), 440, 256, b ->
        {
            pageSpreads.previous();
            updateButtonVisibility();
        }));
        this.nextPage = addButton(new TexturedButtonWidget(controlsX, controlsY - 18, 18, 18, 402, 193, 0, guide.getTheme().getGuiTexture(), 440, 256, b ->
        {
            pageSpreads.next();
            updateButtonVisibility();
        }));
        updateButtonVisibility();
    }

    private void updateButtonVisibility()
    {
        prevPage.visible = pageSpreads.hasPrevious();
        nextPage.visible = pageSpreads.hasNext();
    }

    @Override
    public void render(int mouseX, int mouseY, float lastFrameDuration)
    {
        renderBackground();
        GlStateManager.disableLighting();
        minecraft.getTextureManager().bindTexture(guide.getTheme().getGuiTexture());
        blit((width - 381) / 2, (height - 232) / 2, 0, 0, 401, 232, 440, 256);
        pageSpreads.leftPage().render(mouseX, mouseY, lastFrameDuration,
            pageSpreads.leftPage().contains(mouseX, mouseY));
        pageSpreads.rightPage().render(mouseX, mouseY, lastFrameDuration,
            pageSpreads.rightPage().contains(mouseX, mouseY));
        super.render(mouseX, mouseY, lastFrameDuration);
        GlStateManager.enableLighting();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (super.mouseClicked(mouseX, mouseY, button))
            return true;
        if (pageSpreads.leftPage().contains(mouseX, mouseY))
            return pageSpreads.leftPage().mouseClicked(mouseX, mouseY, button);
        if (pageSpreads.rightPage().contains(mouseX, mouseY))
            return pageSpreads.rightPage().mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (super.mouseClicked(mouseX, mouseY, button))
            return true;
        if (pageSpreads.leftPage().contains(mouseX, mouseY))
            return pageSpreads.leftPage().mouseClicked(mouseX, mouseY, button);
        if (pageSpreads.rightPage().contains(mouseX, mouseY))
            return pageSpreads.rightPage().mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (super.mouseClicked(mouseX, mouseY, button))
            return true;
        if (pageSpreads.leftPage().contains(mouseX, mouseY))
            return pageSpreads.leftPage().mouseClicked(mouseX, mouseY, button);
        if (pageSpreads.rightPage().contains(mouseX, mouseY))
            return pageSpreads.rightPage().mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double wheelDelta)
    {
        if (super.mouseScrolled(mouseX, mouseY, wheelDelta))
            return true;
        if (pageSpreads.leftPage().contains(mouseX, mouseY))
            return pageSpreads.leftPage().mouseScrolled(mouseX, mouseY, wheelDelta);
        if (pageSpreads.rightPage().contains(mouseX, mouseY))
            return pageSpreads.rightPage().mouseScrolled(mouseX, mouseY, wheelDelta);
        return false;
    }

    @Override
    public void openEntry(Identifier entryId)
    {
        Identifier guideId = Identifiers.builder(entryId).subPath(0, 1).build();
        Guide owningGuide = GuideManager.INSTANCE.getGuide(guideId);
        XmlEntry entry = owningGuide.getEntry(entryId);
        if (entry != null)
            MinecraftClient.getInstance().openScreen(new OpenEntryScreen(owningGuide, entry));
        else
            LOGGER.error("Could not open unknown entry {}", entryId);
    }

    @Override
    public void onClose()
    {
        for (Pair<GuideFlow, GuideFlow> spread : pageSpreads)
        {
            spread.getLeft().dispose();
            spread.getRight().dispose();
        }
        super.onClose();
    }

    @Override
    public Identifier getOpenGuideId()
    {
        return guide.getIdentifier();
    }
}
