package io.github.daomephsta.inscribe.client.guide.xmlformat.entry;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;
import net.minecraft.util.Identifier;

public class XmlEntry implements IXmlRepresentation
{
    private final Identifier id, filePath;
    private final Set<String> tags;
    private final List<XmlPage> pages;

    public XmlEntry(Identifier id, Identifier path, Collection<String> tags, List<XmlPage> pages)
    {
        this.id = id;
        this.filePath = path;
        this.tags = new HashSet<>(tags);
        this.pages = pages;
    }

    @Override
    public String toString()
    {
        return String.format("XmlEntry [id=%s, tags=%s]", tags);
    }

    public Identifier getId()
    {
        return id;
    }

    public Identifier getFilePath()
    {
        return filePath;
    }

    public Set<String> getTags()
    {
        return tags;
    }

    public List<XmlPage> getPages()
    {
        return pages;
    }
}
