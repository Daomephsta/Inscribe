package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;

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
	public void renderWidget(int mouseX, int mouseY, float lastFrameDuration)
	{
        float x = horizontalAlignment.offsetX(x(), this, hintWidth());
        float y = verticalAlignment.offsetY(y(), this, hintHeight());
        GlStateManager.pushMatrix();
        GlStateManager.scalef(scale, scale, scale);
        text.render(x / scale, y / scale, mouseX, mouseY, lastFrameDuration);
        GlStateManager.popMatrix();
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
    public String toString()
    {
        return String.format("LabelWidget [text=%s, horizontalAlignment=%s, verticalAlignment=%s]", text, horizontalAlignment, verticalAlignment);
    }
}
