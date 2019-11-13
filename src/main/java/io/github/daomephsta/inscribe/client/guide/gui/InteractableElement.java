package io.github.daomephsta.inscribe.client.guide.gui;

public interface InteractableElement
{
    default boolean mouseClicked(double mouseX, double mouseY, int button)
    {
       return false;
    }

    default boolean mouseReleased(double mouseX, double mouseY, int button)
    {
       return false;
    }

    default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
       return false;
    }

    default boolean mouseScrolled(double mouseX, double mouseY, double wheelDelta)
    {
       return false;
    }

    default boolean keyPressed(int key, int scancode, int modifiers)
    {
       return false;
    }

    default boolean keyReleased(int key, int scancode, int modifiers)
    {
       return false;
    }

    default boolean charTyped(char codepoint, int modifiers)
    {
       return false;
    }
}
