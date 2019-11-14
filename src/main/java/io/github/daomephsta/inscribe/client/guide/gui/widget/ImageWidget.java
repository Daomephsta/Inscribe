package io.github.daomephsta.inscribe.client.guide.gui.widget;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
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
	public void renderWidget(int mouseX, int mouseY, float lastFrameDuration)
	{
		MinecraftClient.getInstance().getTextureManager().bindTexture(src);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuf = tessellator.getBufferBuilder();
		vertexBuf.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV);
		vertexBuf.vertex(left(), bottom(), 0).texture(0.0D, 1.0D).next();
		vertexBuf.vertex(right(), bottom(), 0).texture(1.0D, 1.0D).next();
		vertexBuf.vertex(right(), top(), 0).texture(1.0D, 0.0D).next();
		vertexBuf.vertex(left(), top(), 0).texture(0.0D, 0.0D).next();
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
