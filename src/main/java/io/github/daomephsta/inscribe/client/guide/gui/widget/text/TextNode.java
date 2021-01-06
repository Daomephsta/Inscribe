package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public abstract class TextNode
{
    TextNode next;

    void render(VertexConsumerProvider vertices, MatrixStack matrices, float x, float y, int mouseX, int mouseY, float lastFrameDuration) {}

    abstract int getWidth();

    abstract int getHeight();

    void dispose() {}

    TextNode next()
    {
        return next;
    }
}