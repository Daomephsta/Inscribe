package io.github.daomephsta.inscribe.client.guide.parser.v100;

import org.w3c.dom.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlImage;
import io.github.daomephsta.mosaic.EdgeSpacing;
import net.minecraft.util.Identifier;

final class XmlImageElementType extends XmlElementType<XmlImage>
{
    XmlImageElementType()
    {
        super("image", XmlImage.class);
    }

    @Override
    public XmlImage fromXml(Element xml) throws GuideLoadingException
    {
        XmlAttributes.requireAttributes(xml, "src", "alt_text", "width", "height");
        Identifier src = XmlAttributes.asIdentifier(xml, "src");
        String alt_text = XmlAttributes.getValue(xml, "alt_text");
        int width = XmlAttributes.asInt(xml, "width"),
            height = XmlAttributes.asInt(xml, "height");
        EdgeSpacing padding = LayoutParameters.readPadding(xml);
        EdgeSpacing margin = LayoutParameters.readMargin(xml);
        return new XmlImage(src, alt_text, width, height, padding, margin, LayoutParameters.readSize(xml));
    }
}