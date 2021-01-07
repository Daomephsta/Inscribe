package io.github.daomephsta.inscribe.client.guide.gui.widget;

import io.github.daomephsta.inscribe.client.InscribeRenderLayers;
import io.github.daomephsta.inscribe.common.util.Colors;
import io.github.daomephsta.inscribe.common.util.Lighting;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class VerticalRuleWidget extends GuideWidget
{
    private static final int WIDTH = 2;
    private final int colour;

    public VerticalRuleWidget(int colour)
    {
        this.colour = colour;
        margin().setAll(1);
    }

    @Override
    protected void renderWidget(VertexConsumerProvider vertices, MatrixStack matrices, int mouseX, int mouseY, float lastFrameDuration)
    {
        int[] rgb = Colors.decodeRGB(this.colour);
        VertexConsumer vertexBuf = vertices.getBuffer(InscribeRenderLayers.COLOUR_QUADS);
        vertexBuf.vertex(x(), top(), 0).color(rgb[0], rgb[1], rgb[2], 255).light(Lighting.MAX).next();
        vertexBuf.vertex(x() + WIDTH, top(), 0).color(rgb[0], rgb[1], rgb[2], 255).light(Lighting.MAX).next();
        vertexBuf.vertex(x() + WIDTH, bottom(), 0).color(rgb[0], rgb[1], rgb[2], 255).light(Lighting.MAX).next();
        vertexBuf.vertex(x(), bottom(), 0).color(rgb[0], rgb[1], rgb[2], 255).light(Lighting.MAX).next();
    }

    @Override
    public int hintWidth()
    {
        return WIDTH;
    }
}
