package io.github.daomephsta.inscribe.client.guide.xmlformat.entry;

import java.util.List;
import java.util.Set;

import io.github.daomephsta.inscribe.client.guide.GuideIdentifier;
import io.github.daomephsta.inscribe.client.guide.gui.GuideSession;
import io.github.daomephsta.inscribe.client.guide.gui.OpenEntryScreen;
import io.github.daomephsta.inscribe.client.guide.xmlformat.GuidePart;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public class XmlEntry implements IXmlRepresentation, GuidePart
{
    private final Identifier id;
    private final GuideIdentifier filePath;
    private final Set<String> tags;
    private final List<XmlPage> pages;

    public XmlEntry(Identifier id, GuideIdentifier filePath, Set<String> tags, List<XmlPage> pages)
    {
        this.id = id;
        this.filePath = filePath;
        this.tags = tags;
        this.pages = pages;
    }

    @Override
    public String toString()
    {
        return String.format("XmlEntry [id=%s, tags=%s]", id, tags);
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public GuideIdentifier getFilePath()
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

    @Override
    public Screen toScreen(GuideSession session)
    {
        return new OpenEntryScreen(session.open(this));
    }
}
