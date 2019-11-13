package io.github.daomephsta.inscribe.client.guide.gui.widget.layout;

import java.util.function.Consumer;

import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import io.github.daomephsta.mosaic.ParentWidget;
import io.github.daomephsta.mosaic.flow.Flow;
import io.github.daomephsta.mosaic.flow.Flow.Direction;
import io.github.daomephsta.mosaic.flow.FlowLayoutData;

public class GuideFlow extends GuideWidget implements ParentWidget
{
	private final Flow<GuideWidget> elements;

	public GuideFlow(Direction direction)
    {
        this.elements = new Flow<>(direction);
        setPadding(elements.padding());
        setMargin(elements.margin());
    }

    public void add(GuideWidget element, Consumer<FlowLayoutData> layoutDataConfig)
    {
        elements.add(element, layoutDataConfig);
    }

    public Flow<GuideWidget> add(GuideWidget mosaicWidget)
    {
        return elements.add(mosaicWidget);
    }

    @Override
    protected void renderWidget(int mouseX, int mouseY, float lastFrameDuration)
    {
        for (GuideWidget child : elements.getChildren())
        {
            child.render(mouseX, mouseY, lastFrameDuration);
        }
    }

    @Override
    public void setLayoutParameters(int x, int y, int width, int height)
    {
        super.setLayoutParameters(x, y, width, height);
        elements.setLayoutParameters(x, y, width, height);
    }

    @Override
    public void layoutChildren()
    {
        elements.layoutChildren();
    }
}
