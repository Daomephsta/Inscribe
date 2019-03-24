package io.github.daomephsta.inscribe.client.guide;

import java.util.Collection;

import io.github.daomephsta.inscribe.client.guide.xmlformat.GuideDefinitionClient;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.common.guide.GuideDefinitionCommon;
import net.minecraft.util.Identifier;

public class Guide
{
	private final Collection<XmlEntry> entries;
	private GuideDefinitionMerged definition;
	
	public Guide(Collection<XmlEntry> entries)
	{
		this.entries = entries;
	}
	
	public void loadClientDefinition(GuideDefinitionClient clientDefinition)
	{
		definition = new GuideDefinitionMerged(clientDefinition);
	}
	
	public void loadCommonDefinition(GuideDefinitionCommon commonDefinition)
	{
		definition.loadCommonDefinition(commonDefinition);
	}
	
	public Identifier getIdentifier()
	{
		return definition.getGuideId();
	}
}
