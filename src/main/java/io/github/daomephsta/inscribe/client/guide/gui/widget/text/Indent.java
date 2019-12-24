package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

import net.minecraft.client.MinecraftClient;

public class Indent extends TextNode
{
    private final int level;

    public Indent(int level)
    {
        this.level = level;
    }

    @Override
    int getHeight()
    {
        return MinecraftClient.getInstance().textRenderer.fontHeight;
    }

    @Override
    int getWidth()
    {
        return level * 4;
    }

    @Override
    public String toString()
    {
        return String.format("Indent(level: %d)", level);
    }
}
