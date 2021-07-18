package io.github.daomephsta.inscribe.client.guide.parser;

import org.w3c.dom.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;

public abstract class XmlElementType<T extends IXmlRepresentation>
{
    private final String elementName;

    protected XmlElementType(String elementName)
    {
        this.elementName = elementName;
    }

    public abstract T fromXml(Element xml) throws GuideLoadingException;

    public String getElementName()
    {
        return elementName;
    }
}
