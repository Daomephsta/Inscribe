package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import java.util.List;

import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextBlockWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextNode;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;

public class XmlHeading implements IXmlRepresentation
{ 
    private final List<TextNode> text;
    private final int level; 
    private final Alignment horizontal, vertical;
    
    public XmlHeading(List<TextNode> text, int level, Alignment horizontal, Alignment vertical)
    {
        this.text = text;
        this.level = level;
        this.horizontal = horizontal;
        this.vertical = vertical;
    }
    
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
