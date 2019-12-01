package io.github.daomephsta.inscribe.client.guide.parser.v100;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
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
    public XmlImage fromXml(Element xml) throws GuideLoadingException
	{
	    XmlAttributes.requireAttributes(xml, "src", "alt_text", "width", "height");
		Identifier src = XmlAttributes.asIdentifier(xml, "src");
	    String alt_text = XmlAttributes.getValue(xml, "alt_text");
		int width = XmlAttributes.asInt(xml, "width"),
		    height = XmlAttributes.asInt(xml, "height");
		return new XmlImage(src, alt_text, width, height);
	}
}