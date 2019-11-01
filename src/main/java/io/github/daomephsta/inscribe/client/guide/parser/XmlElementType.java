package io.github.daomephsta.inscribe.client.guide.parser;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;

public abstract class XmlElementType<T extends IXmlRepresentation>
{
	private final String elementName;
	private final Class<T> clazz;
	private final XmlAttributes.Preconditions attributePreconditions = XmlAttributes.preconditions();

	protected XmlElementType(String elementName, Class<T> clazz)
	{
		this.elementName = elementName;
		this.clazz = clazz;
		configurePreconditions(attributePreconditions);
	}

	protected void configurePreconditions(XmlAttributes.Preconditions attributePreconditions) {}

	public final T fromXml(Element xml) throws InscribeSyntaxException
	{
		attributePreconditions.validate(xml);
		return translate(xml);
	}

	protected abstract T translate(Element xml) throws InscribeSyntaxException;

	public String getElementName()
	{
		return elementName;
	}

	public Class<T> getClazz()
	{
		return clazz;
	}
}
