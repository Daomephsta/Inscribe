package io.github.daomephsta.inscribe.client.guide.parser;

import org.w3c.dom.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;

@FunctionalInterface
public interface XmlElementType<T extends IXmlRepresentation>
{
    public T fromXml(Element xml) throws GuideLoadingException;
}
