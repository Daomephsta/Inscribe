package io.github.daomephsta.inscribe.client.guide.gui.widget;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;

public class HorizontalRuleWidget extends GuideWidget
{
    public HorizontalRuleWidget()
    {
        margin().setBottom(1);
    }

    @Override
    protected void renderWidget(int mouseX, int mouseY, float lastFrameDuration)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuf = tessellator.getBuffer();
        float y = top() + height()  / 2.0F;
        vertexBuf.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
        vertexBuf.vertex(left(), y, 0).color(0, 0, 0, 255).next();
        vertexBuf.vertex(right(), y, 0).color(0, 0, 0, 255).next();
        vertexBuf.vertex(right(), y + 1, 0).color(0, 0, 0, 255).next();
        vertexBuf.vertex(left(), y + 1, 0).color(0, 0, 0, 255).next();
        tessellator.draw();
    }

    @Override
    public int hintHeight()
    {
        return 1;
    }
}
