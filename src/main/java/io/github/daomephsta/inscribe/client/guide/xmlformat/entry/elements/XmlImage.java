package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import java.util.OptionalInt;

import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;
import net.minecraft.util.Identifier;

public class XmlImage implements IXmlRepresentation
{
	private final Identifier src;
    private final String altText;
    private final OptionalInt width,
    				  		  height;
    
	public XmlImage(Identifier src, String altText, OptionalInt width, OptionalInt height)
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

	public OptionalInt getWidth()
	{
		return width;
	}

	public OptionalInt getHeight()
	{
		return height;
	}
}
