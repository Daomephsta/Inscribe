package io.github.daomephsta.inscribe.client.guide.gui;

public interface InteractableElement
{
    public default boolean mouseClicked(double mouseX, double mouseY, int button)
    {
       return false;
    }

    public default boolean mouseReleased(double mouseX, double mouseY, int button)
    {
       return false;
    }

    public default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
       return false;
    }

    public default boolean mouseScrolled(double mouseX, double mouseY, double wheelDelta)
    {
       return false;
    }

    public default boolean keyPressed(int key, int scancode, int modifiers)
    {
       return false;
    }

    public default boolean keyReleased(int key, int scancode, int modifiers)
    {
       return false;
    }

    public default boolean charTyped(char codepoint, int modifiers)
    {
       return false;
    }
}
