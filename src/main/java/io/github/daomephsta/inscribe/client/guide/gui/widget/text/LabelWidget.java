package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.common.util.Lighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class LabelWidget extends GuideWidget
{
    public static final float MAX_SCALE = 2.0F;
    private static final int FONT_HEIGHT = 7;
    private final TextNode text;
    private final Alignment horizontalAlignment,
                            verticalAlignment;
    private final float scale;

    public LabelWidget(TextNode textNode, Alignment horizontalAlignment, Alignment verticalAlignment, float scale)
    {
        this.text = textNode;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.scale = scale;
    }

    @Override
    public void renderWidget(VertexConsumerProvider vertices, MatrixStack matrices, int mouseX, int mouseY, float lastFrameDuration)
    {
        float x = horizontalAlignment.offsetX(x(), this, hintWidth());
        float y = verticalAlignment.offsetY(y(), this, hintHeight());
        matrices.push();
        matrices.scale(scale, scale, scale);
        text.render(vertices, matrices, x / scale, y / scale, mouseX, mouseY, Lighting.MAX);
        matrices.pop();
    }

    @Override
    public int hintHeight()
    {
        return (int) Math.ceil(FONT_HEIGHT * scale);
    }

    @Override
    public int hintWidth()
    {
        return (int) Math.ceil(text.getWidth() * scale);
    }

    @Override
    public void dispose()
    {
        text.dispose();
    }

    @Override
    public String toString()
    {
        return String.format("LabelWidget [text=%s, horizontalAlignment=%s, verticalAlignment=%s]", text, horizontalAlignment, verticalAlignment);
    }
}
