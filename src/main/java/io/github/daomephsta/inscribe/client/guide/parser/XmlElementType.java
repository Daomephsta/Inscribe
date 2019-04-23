package io.github.daomephsta.inscribe.client.guide.parser;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;

public abstract class XmlElementType<T extends IXmlRepresentation>
{
	private final String elementName;
	private final Class<T> clazz;
	
	protected XmlElementType(String elementName, Class<T> clazz)
	{
		this.elementName = elementName;
		this.clazz = clazz;
	}
	
	public abstract T fromXml(Element xml);

	public String getElementName()
	{
		return elementName;
	}

	public Class<T> getClazz()
	{
		return clazz;
	}
}
