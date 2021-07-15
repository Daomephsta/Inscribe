package io.github.daomephsta.inscribe.client.guide.xmlformat.base;

import java.util.List;

public abstract class XmlMixedContent implements IXmlRepresentation
{
    private final List<Object> content;

    public XmlMixedContent(List<Object> content)
    {
        this.content = content;
    }

    public List<Object> getContent()
    {
        return content;
    }
}
