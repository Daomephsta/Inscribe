package io.github.daomephsta.inscribe.client.guide.gui;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public interface RenderableElement
{
    public void render(VertexConsumerProvider vertices, MatrixStack matrices, int mouseX, int mouseY, float lastFrameDuration, boolean mouseOver);
}
