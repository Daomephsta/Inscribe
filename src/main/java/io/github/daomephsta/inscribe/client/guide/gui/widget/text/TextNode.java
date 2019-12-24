package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

public abstract class TextNode
{
    TextNode next;

    void render(float x, float y, int mouseX, int mouseY, float lastFrameDuration) {}

    abstract int getWidth();

    abstract int getHeight();

    TextNode next()
    {
        return next;
    }
}