package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import io.github.daomephsta.inscribe.client.guide.gui.InteractableElement;
import io.github.daomephsta.inscribe.client.guide.gui.RenderableElement;
import io.github.daomephsta.inscribe.client.guide.parser.FormatFlags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

public class FormattedTextNode extends TextNode
{
    private String unformatted, formatted;
    private int colour;
    private final Set<FormatFlags> formatFlags;
    private int width = -1;
    private final Collection<InteractableElement> attachedInteractables = new ArrayList<>();
    private final Collection<RenderableElement> attachedRenderables = new ArrayList<>();

    public FormattedTextNode(String text, int color, FormatFlags... formatFlags)
    {
        this.colour = color;
        this.formatFlags = Sets.newHashSet(formatFlags);
        setText(text);
    }

    public void attach(InteractableElement component)
    {
        attachedInteractables.add(component);
        if (component instanceof RenderableElement)
            attachedRenderables.add((RenderableElement) component);
    }

    public void attach(RenderableElement component)
    {
        attachedRenderables.add(component);
        if (component instanceof InteractableElement)
            attachedInteractables.add((InteractableElement) component);
    }

    public boolean mouseClicked(float x, float y, int mouseX, int mouseY, int button)
    {
        boolean mouseOver = contains(x, y, mouseX, mouseY);
        for (InteractableElement element : attachedInteractables)
        {
            if (mouseOver)
                return element.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public void render(float x, float y, int mouseX, int mouseY, float lastFrameDuration)
    {
        MinecraftClient.getInstance().textRenderer.draw(formatted, x, y, colour);
        boolean mouseOver = contains(x, y, mouseX, mouseY);
        for (RenderableElement element : attachedRenderables)
            element.render(mouseX, mouseY, lastFrameDuration, mouseOver);
    }

    private boolean contains(float x, float y, int mouseX, int mouseY)
    {
        return x <= mouseX && mouseX <= x + getWidth() && y <= mouseY && mouseY <= y + getHeight();
    }

    @Override
    int getWidth()
    {
        //Must be lazy because the text renderer may be unavailable during markdown parsing
        if (width == -1)
            width = MinecraftClient.getInstance().textRenderer.getStringWidth(formatted);
        return width;
    }

    @Override
    int getHeight()
    {
        return MinecraftClient.getInstance().textRenderer.fontHeight;
    }

    public void setText(String text)
    {
        this.unformatted = text;
        formatText();
    }

    public FormattedTextNode withColour(int colour)
    {
        this.colour = colour;
        return this;
    }

    public FormattedTextNode clearFormatting()
    {
        formatFlags.clear();
        return this;
    }

    public FormattedTextNode withFormatting(FormatFlags flag)
    {
        formatFlags.add(flag);
        formatText();
        return this;
    }

    private void formatText()
    {
        StringBuilder textBuilder = new StringBuilder(unformatted.length() + (formatFlags.size() + 1) * 2);
        for (FormatFlags flag : formatFlags)
            textBuilder.append(flag.getMCFormatCode());
        textBuilder.append(unformatted);
        textBuilder.append(Formatting.RESET);
        this.formatted = textBuilder.toString();
    }

    @Override
    public String toString()
    {
        return String.format("FormattedTextNode('%s', %s, color=%#08x)", unformatted, formatFlags, colour);
    }
}
