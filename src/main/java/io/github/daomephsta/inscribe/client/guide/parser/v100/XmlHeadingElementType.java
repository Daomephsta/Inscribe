package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.Locale;
import java.util.Optional;

import org.w3c.dom.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlHeading;

public class XmlHeadingElementType extends XmlElementType<XmlHeading>
{
    private final int level;

    protected XmlHeadingElementType(int level)
    {
        super("h" + level);
        this.level = level;
    }

    @Override
    public XmlHeading fromXml(Element xml) throws GuideLoadingException
    {
        Alignment hAlign = Optional.ofNullable(xml.getAttributeNode("hAlign"))
            .map(attr -> Alignment.valueOf(attr.getValue().toUpperCase(Locale.ROOT)))
            .orElse(Alignment.LEADING);
        Alignment vAlign = Optional.ofNullable(xml.getAttributeNode("vAlign"))
            .map(attr -> Alignment.valueOf(attr.getValue().toUpperCase(Locale.ROOT)))
            .orElse(Alignment.CENTER);
        return new XmlHeading(V100Parser.parseContentAsText(xml, 0x000000), level, hAlign, vAlign);
    }
}

