package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlGuideGuiElement;
import io.github.daomephsta.mosaic.EdgeSpacing;
import io.github.daomephsta.mosaic.Size;
import net.minecraft.util.Identifier;

public class XmlImage implements XmlGuideGuiElement
{
    private final Identifier src;
    private final String altText;
    private final int width,
                      height;
    public final EdgeSpacing padding,
                              margin;
    public final Size size;

    public XmlImage(Identifier src, String altText, int width, int height, EdgeSpacing padding, EdgeSpacing margin, Size size)
    {
        this.src = src;
        this.altText = altText;
        this.width = width;
        this.height = height;
        this.padding = padding;
        this.margin = margin;
        this.size = size;
    }

    public Identifier getSrc()
    {
        return src;
    }

    public String getAltText()
    {
        return altText;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }
}
