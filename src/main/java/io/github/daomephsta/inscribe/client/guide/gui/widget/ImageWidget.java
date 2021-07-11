package io.github.daomephsta.inscribe.client.guide.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ImageWidget extends GuideWidget
{
    private final Identifier src;
    private final String altText;
    private final int imageWidth,
                      imageHeight;

    public ImageWidget(Identifier src, String altText, int width, int height)
    {
        this.src = src;
        this.altText = altText;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    @Override
    public void renderWidget(VertexConsumerProvider vertices, MatrixStack matrices, int mouseX, int mouseY, float lastFrameDuration)
    {
        RenderSystem.setShaderTexture(0, src);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuf = tessellator.getBuffer();
        vertexBuf.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        vertexBuf.vertex(left(), bottom(), 0).texture(0.0F, 1.0F).next();
        vertexBuf.vertex(right(), bottom(), 0).texture(1.0F, 1.0F).next();
        vertexBuf.vertex(right(), top(), 0).texture(1.0F, 0.0F).next();
        vertexBuf.vertex(left(), top(), 0).texture(0.0F, 0.0F).next();
        tessellator.draw();
    }

    @Override
    public int hintHeight()
    {
        return imageHeight;
    }

    @Override
    public int hintWidth()
    {
        return imageWidth;
    }
}
