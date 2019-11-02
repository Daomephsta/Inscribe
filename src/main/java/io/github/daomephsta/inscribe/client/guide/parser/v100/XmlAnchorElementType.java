package io.github.daomephsta.inscribe.client.guide.parser.v100;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes.Preconditions;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlAnchor;

class XmlAnchorElementType extends XmlElementType<XmlAnchor>
{
	private static final String NAME = "name";

	XmlAnchorElementType()
	{
		super("anchor", XmlAnchor.class);
	}

	@Override
	protected void configurePreconditions(Preconditions attributePreconditions)
	{
		attributePreconditions.required(NAME);
	}

	@Override
	protected XmlAnchor translate(Element xml) throws GuideLoadingException
	{
		String name = xml.getAttributeValue(NAME);
		return new XmlAnchor(name);
	}
}
