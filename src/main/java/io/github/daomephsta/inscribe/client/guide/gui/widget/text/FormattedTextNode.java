package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

import java.util.Arrays;

import io.github.daomephsta.inscribe.client.guide.parser.FormatFlags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

public class FormattedTextNode extends TextNode
{
    private final String text;
    private final int color;
    private final FormatFlags[] formatFlags;
    private int width = -1;

    public FormattedTextNode(String text, int color, FormatFlags... formatFlags)
    {
        StringBuilder textBuilder = new StringBuilder(text.length() + (formatFlags.length + 1) * 2);
        for (FormatFlags flag : formatFlags)
            textBuilder.append(flag.getMCFormatCode());
        textBuilder.append(text);
        textBuilder.append(Formatting.RESET);
        this.text = textBuilder.toString();
        this.color = color;
        this.formatFlags = formatFlags;
    }

    @Override
    public void render(float x, float y, int mouseX, int mouseY, float lastFrameDuration)
    {
        MinecraftClient.getInstance().textRenderer.draw(text, x, y, color);
    }

    @Override
    int getWidth()
    {
        if (width == -1)
            width = MinecraftClient.getInstance().textRenderer.getStringWidth(text);
        return width;
    }

    @Override
    public String toString()
    {
        return String.format("FormattedTextNode('%s', %s, color=%#08x)", text, Arrays.toString(formatFlags), color);
    }
}
