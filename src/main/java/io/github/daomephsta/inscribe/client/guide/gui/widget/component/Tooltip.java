package io.github.daomephsta.inscribe.client.guide.gui.widget.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.github.daomephsta.inscribe.client.InscribeRenderLayers;
import io.github.daomephsta.inscribe.client.guide.gui.RenderableElement;
import io.github.daomephsta.inscribe.common.util.Lighting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Matrix4f;

public class Tooltip extends WidgetComponent implements RenderableElement
{
    private static final int TEXT_COLOUR = 0xFFFFFFFF;
    private static final int OUTLINE_DARK = 0x5028007F,
                             OUTLINE_LIGHT = 0x505000FF,
                             BACKGROUND = 0xF0100010;
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
        List<OrderedText> tooltip = new ArrayList<>();
        int maxLineWidth = mc.getWindow().getScaledWidth() - mouseX - 16;
        tooltipAppender.accept(line -> 
            tooltip.addAll(mc.textRenderer.wrapLines(new LiteralText(line), maxLineWidth)));
        drawTooltip(vertices, matrices, mouseX, mouseY, mc, tooltip);
    }

    private void drawTooltip(VertexConsumerProvider vertices, 
        MatrixStack matrices, int mouseX, int mouseY, MinecraftClient mc, List<OrderedText> lines)
    {
        if (!lines.isEmpty())
        {
            matrices.push();
            int width = lines.stream()
                .mapToInt(mc.textRenderer::getWidth)
                .max().orElse(0);
            int left = mouseX + 12;
            int top = mouseY - 12;
            int height = 8 + (lines.size() - 1) * 10;

            if (left + width > mc.currentScreen.width)
                left -= 28 + width;

            if (top + height + 6 > mc.currentScreen.height)
                top = mc.currentScreen.height - height - 6;

            mc.getItemRenderer().zOffset = 300.0F;
            // X bar of the background cross
            fillGradient(vertices, matrices, left - 3, top - 4, left + width + 3, top + height + 4, BACKGROUND, BACKGROUND, 300);
            // Y bar of the background cross
            fillGradient(vertices, matrices, left - 4, top - 3, left + width + 4, top + height + 3, BACKGROUND, BACKGROUND, 300);
            // Left highlight
            fillGradient(vertices, matrices, left - 3, top - 3 + 1, left - 3 + 1, top + height + 3 - 1, OUTLINE_LIGHT, OUTLINE_DARK, 300);
            // Right highlight
            fillGradient(vertices, matrices, left + width + 2, top - 3 + 1, left + width + 3, top + height + 3 - 1, OUTLINE_LIGHT, OUTLINE_DARK, 300);
            // Upper highlight
            fillGradient(vertices, matrices, left - 3, top - 3, left + width + 3, top - 3 + 1, OUTLINE_LIGHT, OUTLINE_LIGHT, 300);
            // Lower highlight
            fillGradient(vertices, matrices, left - 3, top + height + 2, left + width + 3, top + height + 3, OUTLINE_DARK, OUTLINE_DARK, 300);
            matrices.translate(0.0D, 0.0D, mc.getItemRenderer().zOffset);
            Matrix4f model = matrices.peek().getPositionMatrix();
            for (OrderedText line : lines)
            {
                mc.textRenderer.draw(line, left, top, TEXT_COLOUR,
                    true, model, vertices, false, /*No highlight*/ 0, Lighting.MAX);
                top += 10;
            }
            mc.getItemRenderer().zOffset = 0.0F;
            matrices.pop();
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
        Matrix4f model = matrices.peek().getPositionMatrix();
        vertexBuf.vertex(model, right, top, blitOffset).color(redStart, blueStart, greenStart, alphaStart).light(Lighting.MAX).next();
        vertexBuf.vertex(model, left, top, blitOffset).color(redStart, blueStart, greenStart, alphaStart).light(Lighting.MAX).next();
        vertexBuf.vertex(model, left, bottom, blitOffset).color(redEnd, blueEnd, greenEnd, alphaEnd).light(Lighting.MAX).next();
        vertexBuf.vertex(model, right, bottom, blitOffset).color(redEnd, blueEnd, greenEnd, alphaEnd).light(Lighting.MAX).next();
    }
}
