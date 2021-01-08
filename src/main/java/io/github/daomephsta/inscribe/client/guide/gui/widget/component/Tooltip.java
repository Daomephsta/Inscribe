package io.github.daomephsta.inscribe.client.guide.gui.widget.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.daomephsta.inscribe.client.InscribeRenderLayers;
import io.github.daomephsta.inscribe.client.guide.gui.RenderableElement;
import io.github.daomephsta.inscribe.common.util.Lighting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;

public class Tooltip extends WidgetComponent implements RenderableElement
{
    private static final int TEXT_COLOUR = 0xFFFFFFFF;
    private static final int OUTLINE_DARK = 0x5028007F,
                             OUTLINE_LIGHT = 0x505000FF,
                             BACKGROUND = 0x00000000;
    private final Consumer<Consumer<String>> tooltipAppender;

    public Tooltip(Consumer<Consumer<String>> tooltipAppender)
    {
        this.tooltipAppender = tooltipAppender;
    }

    @Override
    public void render(VertexConsumerProvider vertices, MatrixStack matrices, int mouseX, int mouseY, float lastFrameDuration, boolean mouseOver)
    {
        if (!mouseOver)
            return;
        MinecraftClient mc = MinecraftClient.getInstance();
        List<String> tooltip = new ArrayList<>();
        int maxLineWidth = mc.getWindow().getScaledWidth() - mouseX - 16;
        tooltipAppender.accept(
            line -> Collections.addAll(tooltip, mc.textRenderer.wrapStringToWidth(line, maxLineWidth).split("\n")));
        drawTooltip(vertices, matrices, mouseX, mouseY, mc, tooltip);
    }

    private void drawTooltip(VertexConsumerProvider vertices, MatrixStack matrices, int mouseX, int mouseY, MinecraftClient mc, List<String> lines)
    {
        if (!lines.isEmpty())
        {
            RenderSystem.disableRescaleNormal();
            matrices.push();
            int width = lines.stream()
                .mapToInt(mc.textRenderer::getStringWidth)
                .max().orElse(0);
            int left = mouseX + 12;
            int top = mouseY - 12;
            int height = 8 + (lines.size() - 1) * 10;

            if (left + width > mc.currentScreen.width)
                left -= 28 + width;

            if (top + height + 6 > mc.currentScreen.height)
                top = mc.currentScreen.height - height - 6;

            mc.getItemRenderer().zOffset = 300.0F;
            fillGradient(vertices, matrices, left - 3, top - 4, left + width + 3, top - 3, BACKGROUND, BACKGROUND, 300);
            fillGradient(vertices, matrices, left - 3, top + height + 3, left + width + 3, top + height + 4, BACKGROUND, BACKGROUND, 300);
            fillGradient(vertices, matrices, left - 3, top - 3, left + width + 3, top + height + 3, BACKGROUND, BACKGROUND, 300);
            fillGradient(vertices, matrices, left - 4, top - 3, left - 3, top + height + 3, BACKGROUND, BACKGROUND, 300);
            fillGradient(vertices, matrices, left + width + 3, top - 3, left + width + 4, top + height + 3, BACKGROUND, BACKGROUND, 300);
            fillGradient(vertices, matrices, left - 3, top - 3 + 1, left - 3 + 1, top + height + 3 - 1, OUTLINE_LIGHT, OUTLINE_DARK, 300);
            fillGradient(vertices, matrices, left + width + 2, top - 3 + 1, left + width + 3, top + height + 3 - 1, OUTLINE_LIGHT, OUTLINE_DARK, 300);
            fillGradient(vertices, matrices, left - 3, top - 3, left + width + 3, top - 3 + 1, OUTLINE_LIGHT, OUTLINE_LIGHT, 300);
            fillGradient(vertices, matrices, left - 3, top + height + 2, left + width + 3, top + height + 3, OUTLINE_DARK, OUTLINE_DARK, 300);
            matrices.translate(0.0D, 0.0D, mc.getItemRenderer().zOffset);
            Matrix4f model = matrices.peek().getModel();
            for (String line : lines)
            {
                mc.textRenderer.draw(line, left, top, TEXT_COLOUR,
                    true, model, vertices, false, /*No highlight*/ 0, Lighting.MAX);
                top += 10;
            }
            mc.getItemRenderer().zOffset = 0.0F;
            matrices.pop();
            RenderSystem.enableRescaleNormal();
        }
    }

    protected void fillGradient(VertexConsumerProvider vertices, MatrixStack matrices, float left, float top, float right, float bottom, int colourStart, int colourEnd, float blitOffset)
    {
        float alphaStart = (colourStart >> 24 & 255) / 255.0F;
        float redStart = (colourStart >> 16 & 255) / 255.0F;
        float blueStart = (colourStart >> 8 & 255) / 255.0F;
        float greenStart = (colourStart & 255) / 255.0F;
        float alphaEnd = (colourEnd >> 24 & 255) / 255.0F;
        float redEnd = (colourEnd >> 16 & 255) / 255.0F;
        float blueEnd = (colourEnd >> 8 & 255) / 255.0F;
        float greenEnd = (colourEnd & 255) / 255.0F;

        VertexConsumer vertexBuf = vertices.getBuffer(InscribeRenderLayers.COLOUR_QUADS);
        Matrix4f model = matrices.peek().getModel();
        vertexBuf.vertex(model, right, top, blitOffset).color(redStart, blueStart, greenStart, alphaStart).light(Lighting.MAX).next();
        vertexBuf.vertex(model, left, top, blitOffset).color(redStart, blueStart, greenStart, alphaStart).light(Lighting.MAX).next();
        vertexBuf.vertex(model, left, bottom, blitOffset).color(redEnd, blueEnd, greenEnd, alphaEnd).light(Lighting.MAX).next();
        vertexBuf.vertex(model, right, bottom, blitOffset).color(redEnd, blueEnd, greenEnd, alphaEnd).light(Lighting.MAX).next();
    }
}
