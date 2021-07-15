package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import io.github.daomephsta.inscribe.client.guide.gui.widget.ImageWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlGuideGuiElement;
import io.github.daomephsta.mosaic.EdgeSpacing;
import io.github.daomephsta.mosaic.Size;
import net.minecraft.util.Identifier;

public record XmlImage(
    Identifier src,     
    String altText,     
    int width,          
    int height,         
    EdgeSpacing padding,
    EdgeSpacing margin, 
    Size size          
) 
implements XmlGuideGuiElement
{
    @Override
    public void acceptPage(GuideFlow output)
    {
        ImageWidget widget = new ImageWidget(src, altText, width, height);
        widget.setPadding(padding);
        widget.setMargin(margin);
        addWidget(output, widget, size);
    }
}
