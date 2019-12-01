package io.github.daomephsta.inscribe.client.guide.parser.v100;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlAnchor;

class XmlAnchorElementType extends XmlElementType<XmlAnchor>
{
    XmlAnchorElementType()
    {
        super("anchor", XmlAnchor.class);
    }

    @Override
    public XmlAnchor fromXml(Element xml) throws GuideLoadingException
    {
        String name = XmlAttributes.getValue(xml, "name");
        return new XmlAnchor(name);
    }
}
