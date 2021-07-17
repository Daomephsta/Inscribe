package io.github.daomephsta.inscribe.client.guide.gui.widget;

import java.util.ArrayList;
import java.util.Collection;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.gui.InteractableElement;
import io.github.daomephsta.inscribe.client.guide.gui.RenderableElement;
import io.github.daomephsta.mosaic.MosaicWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public abstract class GuideWidget extends MosaicWidget implements GuideGuiElement
{
    private final Collection<InteractableElement> attachedInteractables = new ArrayList<>();
    private final Collection<RenderableElement> attachedRenderables = new ArrayList<>();
    private Guide guide;

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

    @Override
    public final void render(VertexConsumerProvider vertices, MatrixStack matrices, int mouseX, int mouseY, float lastFrameDuration, boolean mouseOver)
    {
        for (RenderableElement element : attachedRenderables)
            element.render(vertices, matrices, mouseX, mouseY, lastFrameDuration, contains(mouseX, mouseY));
        renderWidget(vertices, matrices, mouseX, mouseY, lastFrameDuration);
        if (Screen.hasAltDown())
            drawDebugBounds(vertices, matrices);
    }

    private void drawDebugBounds(VertexConsumerProvider vertices, MatrixStack matrices)
    {
        drawBox(vertices, matrices, 0, left() - margin().left(), top() - margin().top(), 
            right() + margin().right(), bottom() + margin().bottom(), 0xFF0000FF);
        drawBox(vertices, matrices, 1, left() + padding().left(), top() + padding().top(), 
            right() - padding().right(), bottom() - padding().bottom(), 0x00FF00FF);
        drawBox(vertices, matrices, 2, left(), top(), right(), bottom(), 0x0000FFFF);
    }

    private void drawBox(VertexConsumerProvider verticesProvider, MatrixStack matrices, float z, 
        int left, int top, int right, int bottom, int color)
    {
        VertexConsumer boxBuilder = verticesProvider.getBuffer(RenderLayer.LINES);
        int r = (color & 0xFF000000) >> 24;
        int g = (color & 0x00FF0000) >> 16;
        int b = (color & 0x0000FF00) >> 8;
        int a =  color & 0x000000FF;
        Matrix4f model = matrices.peek().getModel();
        Matrix3f normal = matrices.peek().getNormal();
        boxBuilder.vertex(model, left, top, z).color(r, g, b, a).normal(normal, 0, 0, 0).next();
        boxBuilder.vertex(model, right, top, z).color(r, g, b, a).normal(normal, 0, 0, 0).next();
        boxBuilder.vertex(model, right, bottom, z).color(r, g, b, a).normal(normal, 0, 0, 0).next();
        boxBuilder.vertex(model, left, bottom, z).color(r, g, b, a).normal(normal, 0, 0, 0).next();
    }

    protected abstract void renderWidget(VertexConsumerProvider vertices, MatrixStack matrices, int mouseX, int mouseY, float lastFrameDuration);

    /**Called when the widget is no longer needed, to allow it to cleanup (e.g mark entities created solely for rendering as removed)*/
    public void dispose() {}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        boolean actionPerformed = false;
        for (InteractableElement element : attachedInteractables)
            actionPerformed |= element.mouseClicked(mouseX, mouseY, button);
        return actionPerformed;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        boolean actionPerformed = false;
        for (InteractableElement element : attachedInteractables)
            actionPerformed |= element.mouseReleased(mouseX, mouseY, button);
        return actionPerformed;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        boolean actionPerformed = false;
        for (InteractableElement element : attachedInteractables)
            actionPerformed |= element.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return actionPerformed;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double wheelDelta)
    {
        boolean actionPerformed = false;
        for (InteractableElement element : attachedInteractables)
            actionPerformed |= element.mouseScrolled(mouseX, mouseY, wheelDelta);
        return actionPerformed;
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers)
    {
        boolean actionPerformed = false;
        for (InteractableElement element : attachedInteractables)
            actionPerformed |= element.keyPressed(key, scancode, modifiers);
        return actionPerformed;
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers)
    {
        boolean actionPerformed = false;
        for (InteractableElement element : attachedInteractables)
            actionPerformed |= element.keyReleased(key, scancode, modifiers);
        return actionPerformed;
    }

    @Override
    public boolean charTyped(char codepoint, int modifiers)
    {
        boolean actionPerformed = false;
        for (InteractableElement element : attachedInteractables)
            actionPerformed |= element.charTyped(codepoint, modifiers);
        return actionPerformed;
    }

    public boolean contains(int x, int y)
    {
        return this.x() <= x && x <= (this.x() + width())
                && this.y() <= y && y <= (this.y() + height());
    }

    public boolean contains(double x, double y)
    {
        return this.x() <= x && x <= (this.x() + width())
            && this.y() <= y && y <= (this.y() + height());
    }

    public int left()
    {
        return x();
    }

    public int top()
    {
        return y();
    }

    public int right()
    {
        return x() + width();
    }

    public int bottom()
    {
        return y() + height();
    }

    protected Guide getGuide()
    {
        return guide;
    }

    public void setGuide(Guide guide)
    {
        this.guide = guide;
    }
}
