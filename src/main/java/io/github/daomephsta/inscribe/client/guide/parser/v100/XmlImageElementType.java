package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.OptionalInt;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
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
	protected XmlImage translate(Element xml) throws InscribeSyntaxException
	{
		Identifier src = XmlAttributes.asIdentifier(xml, "src");
	    String alt_text = XmlAttributes.getValue(xml, "alt_text");
		OptionalInt width = XmlAttributes.asOptionalInt(xml, "width"),
					height = XmlAttributes.asOptionalInt(xml, "height");
		return new XmlImage(src, alt_text, width, height);
	}
}