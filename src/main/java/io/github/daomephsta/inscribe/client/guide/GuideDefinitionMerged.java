package io.github.daomephsta.inscribe.client.guide;

import java.util.Optional;

import io.github.daomephsta.inscribe.client.guide.xmlformat.GuideDefinitionClient;
import io.github.daomephsta.inscribe.common.guide.xmlformat.CommonGuideDefinition;
import io.github.daomephsta.inscribe.common.guide.xmlformat.ItemSpecification;
import net.minecraft.util.Identifier;

public class GuideDefinitionMerged
{
	private final GuideDefinitionClient client;
	private Optional<CommonGuideDefinition> common;
	
	public GuideDefinitionMerged(GuideDefinitionClient client)
	{
		this.client = client;
		this.common = Optional.empty();
	}
	
	public void loadCommonDefinition(CommonGuideDefinition common)
	{
		this.common = Optional.of(common);
	}

	public Identifier getGuideId()
	{
		return client.getGuideId();
	}

	public Optional<ItemSpecification> getItemSpecification()
	{
		return common.map(CommonGuideDefinition::getItemSpecification);
	}
}
