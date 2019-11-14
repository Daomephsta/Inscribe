package io.github.daomephsta.inscribe.client.guide.gui.widget.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.github.daomephsta.inscribe.client.guide.gui.RenderableElement;
import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import net.minecraft.client.MinecraftClient;

public class Tooltip extends WidgetComponent implements RenderableElement
{
	private GuideWidget parent;
	private final Consumer<Consumer<String>> tooltipAppender;

	public Tooltip(Consumer<Consumer<String>> tooltipAppender)
	{
		this.tooltipAppender = tooltipAppender;
	}

	@Override
	public void onAttached(GuideWidget parent)
	{
		this.parent = parent;
	}

	@Override
	public void render(int mouseX, int mouseY, float lastFrameDuration)
	{
		if (!parent.contains(mouseX, mouseY))
			return;
		List<String> tooltip = new ArrayList<>();
		tooltipAppender.accept(tooltip::add);
		MinecraftClient.getInstance().currentScreen.renderTooltip(tooltip, mouseX, mouseY);
	}
}
