package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import java.util.OptionalInt;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.*;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.util.Identifier;

public class XmlImage implements IXmlRepresentation
{
	public static final XmlElementType<XmlImage> XML_TYPE = new XmlType();
	
	private final Identifier src;
    private final String altText;
    private final OptionalInt width,
    				  		  height;
    
	private XmlImage(Identifier src, String altText, OptionalInt width, OptionalInt height)
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
	
	private static class XmlType extends XmlElementType<XmlImage>
	{
		private XmlType()
		{
			super("image", XmlImage.class);
		}

		@Override
		public XmlImage fromXml(Element xml, DeserialisationManager manager)
		{
			Identifier src = new Identifier(xml.getAttributeValue("src"));
		    String alt_text = xml.getAttributeValue("alt_text");
			OptionalInt width = Inscribe.ATTRIBUTE_HELPER.asOptionalInt(xml, "width"),
						height = Inscribe.ATTRIBUTE_HELPER.asOptionalInt(xml, "height");
			return new XmlImage(src, alt_text, width, height);
		}
	}
}
