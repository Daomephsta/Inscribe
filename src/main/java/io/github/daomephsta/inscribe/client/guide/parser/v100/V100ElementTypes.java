package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.stream.IntStream;

import io.github.daomephsta.inscribe.client.guide.parser.SimpleXmlElementType;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideItemAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.NoGuideAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlImage;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlItemStack;

public class V100ElementTypes
{
    static final XmlElementType<XmlImage> IMAGE = new XmlImageElementType();
    static final XmlHeadingElementType[] HEADINGS = IntStream.rangeClosed(1, 6)
        .mapToObj(XmlHeadingElementType::new)
        .toArray(XmlHeadingElementType[]::new);
    static final XmlElementType<XmlItemStack> ITEMSTACK = new XmlItemStackElementType();
    static final XmlElementType<XmlEntityDisplay> ENTITY_DISPLAY = new XmlEntityDisplayElementType();

    static final XmlElementType<GuideItemAccessMethod> GUIDE_ITEM_ACCESS_METHOD = new GuideItemAccessMethodElementType();
    static final XmlElementType<NoGuideAccessMethod> NO_GUIDE_ACCESS_METHOD = new SimpleXmlElementType<>("none", NoGuideAccessMethod.class, NoGuideAccessMethod::new);
}
