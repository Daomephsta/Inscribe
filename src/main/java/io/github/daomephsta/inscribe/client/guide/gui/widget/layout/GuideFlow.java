package io.github.daomephsta.inscribe.client.guide.gui.widget.layout;

import java.util.function.Consumer;

import io.github.daomephsta.inscribe.client.guide.gui.InteractableElement;
import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import io.github.daomephsta.mosaic.ParentWidget;
import io.github.daomephsta.mosaic.flow.Flow;
import io.github.daomephsta.mosaic.flow.Flow.Direction;
import io.github.daomephsta.mosaic.flow.FlowLayoutData;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class GuideFlow extends GuideWidget implements ParentWidget
{
    private final Flow<GuideWidget> elements;

    public GuideFlow(Direction direction)
    {
        this.elements = new Flow<>(direction);
        setPadding(elements.padding());
        setMargin(elements.margin());
    }

    public void add(GuideWidget element, FlowLayoutData layoutData)
    {
        elements.add(element, layoutData);
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
    public void dispose()
    {
        for (GuideWidget widget : elements.getChildren())
            widget.dispose();
    }

    @Override
    protected void renderWidget(VertexConsumerProvider vertices, MatrixStack matrices, int mouseX, int mouseY, float lastFrameDuration)
    {
        for (GuideWidget child : elements.getChildren())
        {
            child.render(vertices, matrices, mouseX, mouseY, lastFrameDuration, contains(mouseX, mouseY));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        for (GuideWidget child : elements.getChildren())
        {
            if (child.contains(mouseX, mouseY) && child instanceof InteractableElement)
                return child.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        for (GuideWidget child : elements.getChildren())
        {
            if (child.contains(mouseX, mouseY) && child instanceof InteractableElement)
                return child.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        for (GuideWidget child : elements.getChildren())
        {
            if (child.contains(mouseX, mouseY) && child instanceof InteractableElement)
                return child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double wheelDelta)
    {
        for (GuideWidget child : elements.getChildren())
        {
            if (child.contains(mouseX, mouseY) && child instanceof InteractableElement)
                return child.mouseScrolled(mouseX, mouseY, wheelDelta);
        }
        return super.mouseScrolled(mouseX, mouseY, wheelDelta);
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers)
    {
        boolean actionPerformed = false;
        for (GuideWidget child : elements.getChildren())
        {
            if (child instanceof InteractableElement)
                actionPerformed |= child.keyPressed(key, scancode, modifiers);
        }
        return actionPerformed | super.keyPressed(key, scancode, modifiers);
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers)
    {
        boolean actionPerformed = false;
        for (GuideWidget child : elements.getChildren())
        {
            if (child instanceof InteractableElement)
                actionPerformed |= child.keyReleased(key, scancode, modifiers);
        }
        return actionPerformed | super.keyReleased(key, scancode, modifiers);
    }

    @Override
    public boolean charTyped(char codepoint, int modifiers)
    {
        boolean actionPerformed = false;
        for (GuideWidget child : elements.getChildren())
        {
            if (child instanceof InteractableElement)
                actionPerformed |= child.charTyped(codepoint, modifiers);
        }
        return actionPerformed | super.charTyped(codepoint, modifiers);
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

    @Override
    public int hintHeight()
    {
        return elements.hintHeight();
    }

    @Override
    public int hintWidth()
    {
        return elements.hintWidth();
    }
}
