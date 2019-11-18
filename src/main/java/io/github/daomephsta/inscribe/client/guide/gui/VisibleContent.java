package io.github.daomephsta.inscribe.client.guide.gui;

import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideGuiElement;

public interface VisibleContent extends GuideGuiElement
{
    void setRenderArea(int x, int y, int width, int height);
}
