package io.github.daomephsta.inscribe.client.guide.parser;

import java.util.function.Supplier;

import org.w3c.dom.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;

public class SimpleXmlElementType<T extends IXmlRepresentation> extends XmlElementType<T>
{
    private final Supplier<T> constructorHandle;

    public SimpleXmlElementType(String elementName, Class<T> clazz, Supplier<T> constructorHandle)
    {
        super(elementName);
        this.constructorHandle = constructorHandle;
    }

    @Override
    public T fromXml(Element xml) throws GuideLoadingException
    {
        return constructorHandle.get();
    }
}
