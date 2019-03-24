package io.github.daomephsta.inscribe.client.guide.xmlformat.base;

import java.util.function.Supplier;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.DeserialisationManager;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlElementType;

public interface IXmlRepresentation
{
	static class SimpleXmlRepresentationType<T extends IXmlRepresentation> extends XmlElementType<T>
	{
		private final Supplier<T> constructorHandle;

		public SimpleXmlRepresentationType(String elementName, Class<T> clazz, Supplier<T> constructorHandle)
		{
			super(elementName, clazz);
			this.constructorHandle = constructorHandle;
		}

		@Override
		public T fromXml(Element xml, DeserialisationManager manager)
		{
			return constructorHandle.get();
		}
	}
}
