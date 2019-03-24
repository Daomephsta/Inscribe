package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;

public class XmlLineBreak implements IXmlRepresentation
{
	public static final XmlElementType<XmlLineBreak> XML_TYPE = 
		new SimpleXmlRepresentationType<>("br", XmlLineBreak.class, XmlLineBreak::new);
}
