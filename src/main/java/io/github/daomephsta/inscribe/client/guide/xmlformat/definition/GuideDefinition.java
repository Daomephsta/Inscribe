package io.github.daomephsta.inscribe.client.guide.xmlformat.definition;

import io.github.daomephsta.inscribe.client.guide.xmlformat.theme.Theme;
import net.minecraft.util.Identifier;

public class GuideDefinition
{
	private final Identifier guideId;
	private final String translationKey;
	private final GuideAccessMethod guideAccess;
	private final Theme theme;

	public GuideDefinition(Identifier guideId, GuideAccessMethod guideAccess, Theme theme)
	{
		this.guideId = guideId;
		this.translationKey = guideId.getNamespace() + ".guide." + guideId.getPath() + ".name";
		this.guideAccess = guideAccess;
		this.theme = theme;
	}

	public Identifier getGuideId()
	{
		return guideId;
	}
	
	public String getTranslationKey()
	{
		return translationKey;
	}
	
	public GuideAccessMethod getAccessMethod()
	{
		return guideAccess;
	}

	public Theme getTheme()
	{
		return theme;
	}

	@Override
	public String toString()
	{
		return String.format("GuideDefinitionClient [guideId=%s, theme=%s]", guideId, theme);
	}
}
