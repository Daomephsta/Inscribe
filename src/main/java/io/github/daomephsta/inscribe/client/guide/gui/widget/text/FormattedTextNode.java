package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

import io.github.daomephsta.inscribe.client.guide.parser.FormatFlags;
import io.github.daomephsta.inscribe.common.util.Lighting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FormattedTextNode extends ElementHostNode
{
    private final Text text;
    private final int colour;
    private int width = -1;

    public FormattedTextNode(String text, Identifier font, int color, FormatFlags... formatFlags)
    {
        this.colour = color;
        this.text = new LiteralText(text).styled(style -> 
        {
            for (FormatFlags flag : formatFlags)
                style = flag.apply(style);
            return style.withFont(font);
        });
    }

    @Override
    public void render(VertexConsumerProvider vertices, MatrixStack matrices, 
        float x, float y, int mouseX, int mouseY, float lastFrameDuration)
    {
        MinecraftClient.getInstance().textRenderer.draw(text, x, y, colour, false, 
            matrices.peek().getPositionMatrix(), vertices, false, /*No highlight*/ 0, Lighting.MAX);
    }

    @Override
    int getWidth()
    {
        //Must be lazy because the text renderer may be unavailable during markdown parsing
        if (width == -1)
            width = MinecraftClient.getInstance().textRenderer.getWidth(text);
        return width;
    }

    @Override
    int getHeight()
    {
        return MinecraftClient.getInstance().textRenderer.fontHeight;
    }

    @Override
    public String toString()
    {
        return String.format("FormattedTextNode('%s', color=%#08x)", text, colour);
    }
}
