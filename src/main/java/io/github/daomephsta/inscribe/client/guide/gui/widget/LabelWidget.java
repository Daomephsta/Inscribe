package io.github.daomephsta.inscribe.client.guide.gui.widget;

import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import net.minecraft.client.MinecraftClient;

public class LabelWidget extends GuideWidget
{
	private static final int FONT_HEIGHT = 7;
    private final String text;
	private final Alignment horizontalAlignment,
	                        verticalAlignment;
    private final int color;

    public LabelWidget(String text, Alignment horizontalAlignment, Alignment verticalAlignment, int color)
    {
        this.text = text;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.color = color;
    }

    @Override
	public void renderWidget(int mouseX, int mouseY, float lastFrameDuration)
	{
        MinecraftClient.getInstance().textRenderer.draw(text, horizontalAlignment.offsetX(x(), this, width()),
            verticalAlignment.offsetY(y(), this, height()), color);
	}

    @Override
    public int hintHeight()
    {
        return FONT_HEIGHT;
    }

    @Override
    public int hintWidth()
    {
        return MinecraftClient.getInstance().textRenderer.getStringWidth(text);
    }
}
