package io.github.daomephsta.inscribe.client.guide.gui.widget;

import io.github.daomephsta.inscribe.client.InscribeRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class HorizontalRuleWidget extends GuideWidget
{
    public HorizontalRuleWidget()
    {
        margin().setBottom(1);
    }

    @Override
    protected void renderWidget(VertexConsumerProvider vertices, MatrixStack matrices, int mouseX, int mouseY, float lastFrameDuration)
    {
        float y = top() + height()  / 2.0F;
        matrices.push();
        VertexConsumer vertexBuf = vertices.getBuffer(InscribeRenderLayers.COLOUR_QUADS);
        vertexBuf.vertex(matrices.peek().getPositionMatrix(), left(), y, 0)
            .color(0, 0, 0, 255).next();
        vertexBuf.vertex(matrices.peek().getPositionMatrix(), right(), y, 0)
            .color(0, 0, 0, 255).next();
        vertexBuf.vertex(matrices.peek().getPositionMatrix(), right(), y + 1, 0)
            .color(0, 0, 0, 255).next();
        vertexBuf.vertex(matrices.peek().getPositionMatrix(), left(), y + 1, 0)
            .color(0, 0, 0, 255).next();
        matrices.pop();
    }

    @Override
    public int hintHeight()
    {
        return 1;
    }
}
