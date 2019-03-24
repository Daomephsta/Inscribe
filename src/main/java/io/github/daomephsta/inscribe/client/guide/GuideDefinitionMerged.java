package io.github.daomephsta.inscribe.client.guide;

import io.github.daomephsta.inscribe.client.guide.xmlformat.GuideDefinitionClient;
import io.github.daomephsta.inscribe.common.guide.GuideDefinitionCommon;
import net.minecraft.util.Identifier;

public class GuideDefinitionMerged
{
	private final GuideDefinitionClient client;
	private GuideDefinitionCommon common;
	
	public GuideDefinitionMerged(GuideDefinitionClient client)
	{
		this.client = client;
		this.common = null;
	}
	
	public void loadCommonDefinition(GuideDefinitionCommon common)
	{
		this.common = common;
		checkInvariants();
	}
	
	private void checkInvariants()
	{
		if (!common.getGuideId().equals(client.getGuideId()))
			throw new IllegalArgumentException("");
	}

	public Identifier getGuideId()
	{
		return client.getGuideId();
	}
}
