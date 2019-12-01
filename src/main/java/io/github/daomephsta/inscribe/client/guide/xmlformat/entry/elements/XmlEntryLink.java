package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import java.util.List;

import io.github.daomephsta.inscribe.client.guide.xmlformat.base.XmlMixedContent;
import net.minecraft.util.Identifier;

public class XmlEntryLink extends XmlMixedContent
{
    private final Identifier entryId;
    private final String anchor;

    public XmlEntryLink(List<Object> content, Identifier entryId, String anchor)
    {
        super(content);
        this.entryId = entryId;
        this.anchor = anchor;
    }

    public Identifier getEntryId()
    {
        return entryId;
    }

    public String getAnchor()
    {
        return anchor;
    }
}
