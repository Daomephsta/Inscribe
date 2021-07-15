package io.github.daomephsta.inscribe.client.guide.parser;

import org.w3c.dom.Document;

import io.github.daomephsta.inscribe.client.guide.GuideIdentifier;
import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideDefinition;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.TableOfContents;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public interface Parser
{
    public GuideDefinition loadGuideDefinition(Document doc, ResourceManager resourceManager, GuideIdentifier filePath) throws GuideLoadingException;

    public XmlEntry loadEntry(Document doc, ResourceManager resourceManager, Identifier id, GuideIdentifier filePath) throws GuideLoadingException;

    public TableOfContents loadTableOfContents(Document doc, Identifier id, GuideIdentifier filePath) throws GuideLoadingException;
}
