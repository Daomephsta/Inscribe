package io.github.daomephsta.inscribe.client.guide.parser.v100;

import org.w3c.dom.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.ContentDeserialiser;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntryLink;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;

class XmlEntryLinkElementType extends XmlElementType<XmlEntryLink>
{
    private final Lazy<ContentDeserialiser> contentDeserialiser;

    XmlEntryLinkElementType()
    {
        super("entry_link", XmlEntryLink.class);
        this.contentDeserialiser = new Lazy<>
        (() ->
            new ContentDeserialiser.Impl()
                .registerDeserialiser(V100ElementTypes.BOLD)
                .registerDeserialiser(V100ElementTypes.STRONG)
                .registerDeserialiser(V100ElementTypes.EMPHASIS)
                .registerDeserialiser(V100ElementTypes.ITALICS)
                .registerDeserialiser(V100ElementTypes.DEL)
                .registerDeserialiser(V100ElementTypes.IMAGE)
        );
    }

    @Override
    public XmlEntryLink fromXml(Element xml) throws GuideLoadingException
    {
        Identifier entryId = XmlAttributes.asIdentifier(xml, "entry");
        String anchorId = xml.getAttribute("anchor");
        return new XmlEntryLink(contentDeserialiser.get().deserialise(xml.getChildNodes()), entryId, anchorId);
    }
}
