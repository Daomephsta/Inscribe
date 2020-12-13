package io.github.daomephsta.inscribe.client.guide.gui.widget;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.daomephsta.inscribe.common.util.Colors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;

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
    protected void renderWidget(int mouseX, int mouseY, float lastFrameDuration)
    {
        int[] rgb = Colors.decodeRGB(this.colour);
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuf = tessellator.getBuffer();
        vertexBuf.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
        vertexBuf.vertex(x(), top(), 0).color(rgb[0], rgb[1], rgb[2], 255).next();
        vertexBuf.vertex(x() + WIDTH, top(), 0).color(rgb[0], rgb[1], rgb[2], 255).next();
        vertexBuf.vertex(x() + WIDTH, bottom(), 0).color(rgb[0], rgb[1], rgb[2], 255).next();
        vertexBuf.vertex(x(), bottom(), 0).color(rgb[0], rgb[1], rgb[2], 255).next();
        tessellator.draw();
        RenderSystem.enableTexture();
    }

    @Override
    public int hintWidth()
    {
        return WIDTH;
    }
}
