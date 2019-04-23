package io.github.daomephsta.inscribe.client.guide.parser;

import java.util.function.Supplier;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;

public class SimpleXmlElementType<T extends IXmlRepresentation> extends XmlElementType<T>
{
	private final Supplier<T> constructorHandle;

	public SimpleXmlElementType(String elementName, Class<T> clazz, Supplier<T> constructorHandle)
	{
		super(elementName, clazz);
		this.constructorHandle = constructorHandle;
	}

	@Override
	public T fromXml(Element xml)
	{
		return constructorHandle.get();
	}
}
