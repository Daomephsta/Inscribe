package io.github.daomephsta.inscribe.client.guide.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;

public class GuideScreen extends Screen implements GuideGui
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final Guide guide;
    private VisibleContent visibleContent;
    private int guideLeft, guideTop, guideWidth, guideHeight;

    public GuideScreen(Guide guide)
    {
        super(new TranslatableTextComponent(guide.getTranslationKey()));
        this.guide = guide;
        this.visibleContent = new TableOfContentsEntries(guide.getMainTableOfContents());
    }

    @Override
    public void init(MinecraftClient minecraft, int width, int height)
    {
        super.init(minecraft, width, height);
        this.guideHeight = 180;
        this.guideWidth = 146;
        this.guideTop = 2;
        this.guideLeft = (width - guideWidth) / 2;
        this.visibleContent.setRenderArea(guideLeft, guideTop, guideWidth, guideHeight);
    }

    @Override
    public void render(int mouseX, int mouseY, float lastFrameDuration)
    {
        GlStateManager.disableLighting();
        minecraft.getTextureManager().bindTexture(guide.getTheme().getGuiTexture());
        this.blit(guideLeft, guideTop, 20, 1, guideWidth, guideHeight);
        if (visibleContent != null)
            visibleContent.render(mouseX, mouseY, lastFrameDuration);
        GlStateManager.enableLighting();
    }

    @Override
    public void openEntry(Identifier entryId)
    {
        XmlEntry entry = guide.getEntry(entryId);
        if (entry != null)
        {
            visibleContent = new OpenEntry(entry, guideLeft, guideTop, guideWidth, guideHeight);
            visibleContent.setRenderArea(guideLeft, guideTop, guideWidth, guideHeight);
        }
        else
            LOGGER.error("Could not open unknown entry {}", entryId);
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers)
    {
        if (super.keyPressed(key, scancode, modifiers))
            return true;
        return visibleContent != null && visibleContent.keyPressed(key, scancode, modifiers);
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers)
    {
        if (super.keyReleased(key, scancode, modifiers))
            return true;
        return visibleContent != null && visibleContent.keyReleased(key, scancode, modifiers);
    }

    @Override
    public boolean charTyped(char codepoint, int modifiers)
    {
        if (super.charTyped(codepoint, modifiers))
            return true;
        return visibleContent != null && visibleContent.charTyped(codepoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (super.mouseClicked(mouseX, mouseY, button))
            return true;
        return visibleContent != null && visibleContent.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (super.mouseReleased(mouseX, mouseY, button))
            return true;
        return visibleContent != null && visibleContent.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
            return true;
        return visibleContent != null
                && visibleContent.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double wheelDelta)
    {
        if (super.mouseScrolled(mouseX, mouseY, wheelDelta))
            return true;
        return visibleContent != null && visibleContent.mouseScrolled(mouseX, mouseY, wheelDelta);
    }
}
