package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.OptionalInt;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlImage;
import net.minecraft.util.Identifier;

final class XmlImageElementType extends XmlElementType<XmlImage>
{
	XmlImageElementType()
	{
		super("image", XmlImage.class);
	}

	@Override
	public XmlImage fromXml(Element xml)
	{
		Identifier src = new Identifier(xml.getAttributeValue("src"));
	    String alt_text = xml.getAttributeValue("alt_text");
		OptionalInt width = XmlAttributes.asOptionalInt(xml, "width"),
					height = XmlAttributes.asOptionalInt(xml, "height");
		return new XmlImage(src, alt_text, width, height);
	}
}