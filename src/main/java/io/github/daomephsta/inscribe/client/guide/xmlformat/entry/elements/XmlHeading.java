package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import java.util.List;

import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextBlockWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextNode;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlGuideGuiElement;

public record XmlHeading(
    List<TextNode> text, 
    int level,           
    Alignment horizontal,
    Alignment vertical   
)
implements XmlGuideGuiElement
{  
    @Override
    public void acceptPage(GuideFlow output)
    {
        output.add(new TextBlockWidget(horizontal, vertical, text, levelToScale(level)));
    }

    public static float levelToScale(int level)
    {
        return (7 - level) / 6.0F * TextBlockWidget.MAX_SCALE;
    }
}
