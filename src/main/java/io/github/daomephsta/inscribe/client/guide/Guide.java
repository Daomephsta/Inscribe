package io.github.daomephsta.inscribe.client.guide;

import java.util.Collection;

import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideDefinition;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import net.minecraft.util.Identifier;

public class Guide
{
	private final GuideDefinition definition;
	private final Collection<XmlEntry> entries;
	
	public Guide(GuideDefinition definition, Collection<XmlEntry> entries)
	{
		this.definition = definition;
		this.entries = entries;
	}
	
	public GuideDefinition getDefinition()
	{
		return definition;
	}
	
	public Identifier getIdentifier()
	{
		return definition.getGuideId();
	}
}
