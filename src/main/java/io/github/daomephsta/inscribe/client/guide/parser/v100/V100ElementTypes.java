package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.stream.IntStream;

import io.github.daomephsta.inscribe.client.guide.parser.SimpleXmlElementType;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.ContentDeserialiser;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.XmlMixedContent.XmlBold;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.XmlMixedContent.XmlDel;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.XmlMixedContent.XmlEmphasis;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.XmlMixedContent.XmlItalics;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.XmlMixedContent.XmlParagraph;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.XmlMixedContent.XmlStrong;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideItemAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.NoGuideAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlAnchor;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntryLink;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlImage;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlItemStack;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlLineBreak;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlWebLink;

public class V100ElementTypes
{
    static final XmlElementType<XmlImage> IMAGE = new XmlImageElementType();
    static final XmlElementType<XmlAnchor> ANCHOR = new XmlAnchorElementType();
    static final XmlElementType<XmlEntryLink> ENTRY_LINK = new XmlEntryLinkElementType();
    static final XmlElementType<XmlWebLink> WEB_LINK = new XmlWebLinkElementType();
    static final XmlElementType<XmlLineBreak> LINE_BREAK = new SimpleXmlElementType<>("br", XmlLineBreak.class, XmlLineBreak::new);
    static final XmlElementType<XmlDel> DEL = new TextFormattingXmlElementType<>("del", XmlDel.class, XmlDel::new);
    static final XmlElementType<XmlStrong> STRONG = new TextFormattingXmlElementType<>("strong", XmlStrong.class, XmlStrong::new);
    static final XmlElementType<XmlBold> BOLD = new TextFormattingXmlElementType<>("b", XmlBold.class, XmlBold::new);
    static final XmlElementType<XmlEmphasis> EMPHASIS = new TextFormattingXmlElementType<>("em", XmlEmphasis.class, XmlEmphasis::new);
    static final XmlElementType<XmlItalics> ITALICS = new TextFormattingXmlElementType<>("i", XmlItalics.class, XmlItalics::new);
    static final XmlHeadingElementType[] HEADINGS = IntStream.rangeClosed(1, 6)
        .mapToObj(XmlHeadingElementType::new)
        .toArray(XmlHeadingElementType[]::new);
    static final ContentDeserialiser TEXT_FORMATTING = new ContentDeserialiser.Impl()
        .registerDeserialisers(V100ElementTypes.BOLD, V100ElementTypes.STRONG, 
            V100ElementTypes.EMPHASIS, V100ElementTypes.ITALICS, V100ElementTypes.DEL)
        .registerDeserialisers(V100ElementTypes.HEADINGS);
    static final XmlElementType<XmlParagraph> PARAGRAPH = new XmlParagraphElementType();
    static final XmlElementType<XmlItemStack> ITEMSTACK = new XmlItemStackElementType();
    static final XmlElementType<XmlEntityDisplay> ENTITY_DISPLAY = new XmlEntityDisplayElementType();

    static final XmlElementType<GuideItemAccessMethod> GUIDE_ITEM_ACCESS_METHOD = new GuideItemAccessMethodElementType();
    static final XmlElementType<NoGuideAccessMethod> NO_GUIDE_ACCESS_METHOD = new SimpleXmlElementType<>("none", NoGuideAccessMethod.class, NoGuideAccessMethod::new);
}
