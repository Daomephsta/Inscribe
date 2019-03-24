package io.github.daomephsta.inscribe.client.guide.xmlformat;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.theme.Theme;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.util.Identifier;

public class GuideDefinitionClient
{
	private final Identifier guideId;
	private final Theme theme;
	
	private GuideDefinitionClient(Identifier guideId, Theme theme)
	{
		this.guideId = guideId;
		this.theme = theme;
	}

	public static GuideDefinitionClient fromXml(Element xml)
	{
		Identifier guideId = new Identifier(xml.getChildText("id", Inscribe.XML_NAMESPACE));
		Theme theme = null;
		return new GuideDefinitionClient(guideId, theme);
	}

	public Identifier getGuideId()
	{
		return guideId;
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
