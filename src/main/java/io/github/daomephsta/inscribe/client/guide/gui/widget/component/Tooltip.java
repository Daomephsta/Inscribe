package io.github.daomephsta.inscribe.client.guide.gui.widget.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import io.github.daomephsta.inscribe.client.guide.gui.RenderableElement;
import net.minecraft.client.MinecraftClient;

public class Tooltip extends WidgetComponent implements RenderableElement
{
    private final Consumer<Consumer<String>> tooltipAppender;

    public Tooltip(Consumer<Consumer<String>> tooltipAppender)
    {
        this.tooltipAppender = tooltipAppender;
    }

    @Override
    public void render(int mouseX, int mouseY, float lastFrameDuration, boolean mouseOver)
    {
        if (!mouseOver)
            return;
        MinecraftClient mc = MinecraftClient.getInstance();
        List<String> tooltip = new ArrayList<>();
        int maxLineWidth = mc.getWindow().getScaledWidth() - mouseX - 16;
        tooltipAppender.accept(
            line -> Collections.addAll(tooltip, mc.textRenderer.wrapStringToWidth(line, maxLineWidth).split("\n")));
        mc.currentScreen.renderTooltip(tooltip, mouseX, mouseY);
    }
}
