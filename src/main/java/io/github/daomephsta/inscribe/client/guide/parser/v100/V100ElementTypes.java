package io.github.daomephsta.inscribe.client.guide.parser.v100;

import io.github.daomephsta.inscribe.client.guide.parser.*;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.XmlMixedContent.*;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideItemAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.NoGuideAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.*;

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
    static final XmlElementType<XmlParagraph> PARAGRAPH = new XmlParagraphElementType();
    static final XmlElementType<XmlItemStack> ITEMSTACK = new XmlItemStackElementType();
    static final XmlElementType<XmlEntityDisplay> ENTITY_DISPLAY = new XmlEntityDisplayElementType();

    static final XmlElementType<GuideItemAccessMethod> GUIDE_ITEM_ACCESS_METHOD = new GuideItemAccessMethodElementType();
    static final XmlElementType<NoGuideAccessMethod> NO_GUIDE_ACCESS_METHOD = new SimpleXmlElementType<>("none", NoGuideAccessMethod.class, NoGuideAccessMethod::new);
}
