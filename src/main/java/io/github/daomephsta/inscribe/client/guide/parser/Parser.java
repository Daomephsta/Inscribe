package io.github.daomephsta.inscribe.client.guide.parser;

import org.w3c.dom.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideDefinition;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public interface Parser
{
    public GuideDefinition loadGuideDefinition(Element xml, ResourceManager resourceManager, Identifier path) throws GuideLoadingException;

    public XmlEntry loadEntry(Element root, ResourceManager resourceManager, Identifier path) throws GuideLoadingException;
}
