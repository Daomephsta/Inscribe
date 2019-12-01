package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;

public class XmlAnchor implements IXmlRepresentation
{
    private final String name;

    public XmlAnchor(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
