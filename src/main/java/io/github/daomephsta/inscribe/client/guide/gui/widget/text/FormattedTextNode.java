package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

import java.util.Arrays;

import io.github.daomephsta.inscribe.client.guide.parser.FormatFlags;
import io.github.daomephsta.inscribe.common.util.Lighting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class FormattedTextNode extends ElementHostNode
{
    private final String text;
    private final TextRenderer font;
    private final int colour;
    private final FormatFlags[] formatFlags;
    private int width = -1;

    public FormattedTextNode(String text, Identifier font, int color, FormatFlags... formatFlags)
    {
        this.colour = color;
        this.font = MinecraftClient.getInstance().getFontManager().getTextRenderer(font);
        this.formatFlags = formatFlags;
        StringBuilder textBuilder = new StringBuilder(text.length() + (formatFlags.length + 1) * 2);
        for (FormatFlags flag : formatFlags)
            textBuilder.append(flag.getMCFormatCode());
        textBuilder.append(text);
        this.text = textBuilder.toString();
    }

    @Override
    public void render(VertexConsumerProvider vertices, MatrixStack matrices, float x, float y, int mouseX, int mouseY, float lastFrameDuration)
    {
        font.draw(text, x, y, colour, false, matrices.peek().getModel(), vertices, false, /*No highlight*/ 0, Lighting.MAX);
    }

    @Override
    int getWidth()
    {
        //Must be lazy because the text renderer may be unavailable during markdown parsing
        if (width == -1)
            width = font.getStringWidth(text);
        return width;
    }

    @Override
    int getHeight()
    {
        return font.fontHeight;
    }

    @Override
    public String toString()
    {
        return String.format("FormattedTextNode('%s', %s, color=%#08x)", text, Arrays.toString(formatFlags), colour);
    }
}
