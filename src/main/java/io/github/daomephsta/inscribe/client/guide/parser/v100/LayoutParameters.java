package io.github.daomephsta.inscribe.client.guide.parser.v100;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlElements;
import io.github.daomephsta.mosaic.EdgeSpacing;
import io.github.daomephsta.mosaic.ParseException;
import io.github.daomephsta.mosaic.Size;
import io.github.daomephsta.mosaic.SizeConstraint;

public class LayoutParameters
{
    private LayoutParameters(){}

    public static EdgeSpacing readPadding(Element xml) throws InscribeSyntaxException
    {
        EdgeSpacing padding = new EdgeSpacing();
        padding.setTop(XmlAttributes.asInt(xml, "paddingTop", 0));
        padding.setBottom(XmlAttributes.asInt(xml, "paddingBottom", 0));
        padding.setLeft(XmlAttributes.asInt(xml, "paddingLeft", 0));
        padding.setRight(XmlAttributes.asInt(xml, "paddingRight", 0));
        return padding;
    }

    public static EdgeSpacing readMargin(Element xml) throws InscribeSyntaxException
    {
        EdgeSpacing margin = new EdgeSpacing();
        margin.setTop(XmlAttributes.asInt(xml, "marginTop", 0));
        margin.setBottom(XmlAttributes.asInt(xml, "marginBottom", 0));
        margin.setLeft(XmlAttributes.asInt(xml, "marginLeft", 0));
        margin.setRight(XmlAttributes.asInt(xml, "marginRight", 0));
        return margin;
    }

    public static Size readSize(Element xml) throws InscribeSyntaxException
    {
        Size size = new Size();
        if (XmlElements.hasAttribute(xml, "minSize"))
            size.setMinSize(XmlAttributes.asInt(xml, "minSize"));
        if (XmlElements.hasAttribute(xml, "maxSize"))
            size.setMaxSize(XmlAttributes.asInt(xml, "maxSize"));
        if (XmlElements.hasAttribute(xml, "size"))
            size.setPreferredSize(readSizeConstraint(xml));
        return size;
    }

    private static SizeConstraint readSizeConstraint(Element xml) throws InscribeSyntaxException
    {
        try
        {
            return SizeConstraint.parse(xml.getAttribute("size"));
        }
        catch (DOMException | ParseException e)
        {
            throw new InscribeSyntaxException("Could not parse size attribute of " + XmlElements.getDebugString(xml), e);
        }
    }
}
