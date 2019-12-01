package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlGuideGuiElement;
import net.minecraft.util.Identifier;

public class XmlImage implements XmlGuideGuiElement
{
    private final Identifier src;
    private final String altText;
    private final int width,
                      height;

    public XmlImage(Identifier src, String altText, int width, int height)
    {
        this.src = src;
        this.altText = altText;
        this.width = width;
        this.height = height;
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
