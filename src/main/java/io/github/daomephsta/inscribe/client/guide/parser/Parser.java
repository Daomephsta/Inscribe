package io.github.daomephsta.inscribe.client.guide.parser;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideDefinition;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;

public interface Parser
{
	public GuideDefinition loadGuideDefinition(Element xml);

	public XmlEntry loadEntry(Element root);
}
