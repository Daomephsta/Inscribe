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
    private int width,
                height;

    public InlineImage(Identifier imageLocation)
    {
        this.imageLocation = imageLocation;
        try
        {
            NativeImage ni = NativeImage.read(MinecraftClient.getInstance().getResourceManager().getResource(imageLocation).getInputStream());
            this.width = ni.getWidth();
            this.height = ni.getHeight();
            MinecraftClient.getInstance().getTextureManager().registerTexture(imageLocation, new NativeImageBackedTexture(ni));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void render(float x, float y, int mouseX, int mouseY, float lastFrameDuration)
    {
        MinecraftClient.getInstance().getTextureManager().bindTexture(imageLocation);
        DrawableHelper.blit((int) x, (int) y, 0, 0, width, height, width, height);
        super.render(x, y, mouseX, mouseY, lastFrameDuration);
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
}
