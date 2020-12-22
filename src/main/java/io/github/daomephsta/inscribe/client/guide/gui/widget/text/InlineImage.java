package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

import java.io.IOException;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
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
    public void render(float x, float y, int mouseX, int mouseY, float lastFrameDuration)
    {
        MinecraftClient.getInstance().getTextureManager().bindTexture(imageLocation);
        DrawableHelper.blit((int) x, (int) y, 0, 0, width, height, width, height);
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
