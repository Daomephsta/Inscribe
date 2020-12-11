package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

import java.util.ArrayList;
import java.util.Collection;
import io.github.daomephsta.inscribe.client.guide.gui.InteractableElement;
import io.github.daomephsta.inscribe.client.guide.gui.RenderableElement;

public abstract class ElementHostNode extends TextNode
{
    private final Collection<InteractableElement> attachedInteractables = new ArrayList<>();
    private final Collection<RenderableElement> attachedRenderables = new ArrayList<>();

    public void attach(InteractableElement component)
    {
        attachedInteractables.add(component);
        if (component instanceof RenderableElement)
            attachedRenderables.add((RenderableElement) component);
    }

    public void attach(RenderableElement component)
    {
        attachedRenderables.add(component);
        if (component instanceof InteractableElement)
            attachedInteractables.add((InteractableElement) component);
    }

    public boolean mouseClicked(float x, float y, int mouseX, int mouseY, int button)
    {
        boolean mouseOver = contains(x, y, mouseX, mouseY);
        for (InteractableElement element : attachedInteractables)
        {
            if (mouseOver)
                return element.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public void render(float x, float y, int mouseX, int mouseY, float lastFrameDuration)
    {
        boolean mouseOver = contains(x, y, mouseX, mouseY);
        for (RenderableElement element : attachedRenderables)
            element.render(mouseX, mouseY, lastFrameDuration, mouseOver);
    }

    private boolean contains(float x, float y, int mouseX, int mouseY)
    {
        return x <= mouseX && mouseX <= x + getWidth() && y <= mouseY && mouseY <= y + getHeight();
    }
}
