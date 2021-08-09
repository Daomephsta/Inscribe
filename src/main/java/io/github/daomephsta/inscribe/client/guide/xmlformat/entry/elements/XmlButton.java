package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import java.util.List;

import io.github.daomephsta.inscribe.client.guide.gui.InteractableElement;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextBlockWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextNode;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlGuideGuiElement;

public record XmlButton(InteractableElement handler, List<TextNode> label) implements XmlGuideGuiElement
{
    @Override
    public void acceptPage(GuideFlow output)
    {
        TextBlockWidget button = new TextBlockWidget(Alignment.CENTER, Alignment.CENTER, label, 1.0F);
        button.attach(handler);
        output.add(button);
    }
}
