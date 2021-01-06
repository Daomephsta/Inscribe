package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

import java.io.IOException;

import io.github.daomephsta.inscribe.common.util.Lighting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class InlineImage extends ElementHostNode
{
    private final Identifier imageLocation;
    private final NativeImageBackedTexture texture;
    private int width,
                height;

    public InlineImage(Identifier imageLocation)
    {
        this.imageLocation = imageLocation;
        try
        {
            MinecraftClient mc = MinecraftClient.getInstance();
            NativeImage image = NativeImage.read(mc.getResourceManager().getResource(imageLocation).getInputStream());
            this.width = image.getWidth();
            this.height = image.getHeight();
            this.texture = new NativeImageBackedTexture(image);
            mc.getTextureManager().registerTexture(imageLocation, texture);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void render(VertexConsumerProvider vertices, MatrixStack matrices, float x, float y, int mouseX, int mouseY, float lastFrameDuration)
    {
        VertexConsumer vertexBuf = vertices.getBuffer(RenderLayer.getText(imageLocation));
        float right = x + width, bottom = y + height;
        vertexBuf.vertex(x, bottom, 0).color(255, 255, 255, 255)
            .texture(0.0F, 1.0F).light(Lighting.MAX).next();
        vertexBuf.vertex(right, bottom, 0).color(255, 255, 255, 255)
            .texture(1.0F, 1.0F).light(Lighting.MAX).next();
        vertexBuf.vertex(right, y, 0).color(255, 255, 255, 255)
            .texture(1.0F, 0.0F).light(Lighting.MAX).next();
        vertexBuf.vertex(x, y, 0).color(255, 255, 255, 255)
            .texture(0.0F, 0.0F).light(Lighting.MAX).next();
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height + 2; //padding
    }

    @Override
    void dispose()
    {
        texture.close();
    }
}
