package io.github.daomephsta.inscribe.client.guide.xmlformat.definition;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.theme.Theme;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.util.Identifier;

public class GuideDefinition
{
	private final Identifier guideId;
	private final GuideAccessMethod guideAccess;
	private final Theme theme;

	private GuideDefinition(Identifier guideId, GuideAccessMethod guideAccess, Theme theme)
	{
		this.guideId = guideId;
		this.guideAccess = guideAccess;
		this.theme = theme;
	}

	public static GuideDefinition fromXml(Element xml)
	{
		Identifier guideId = Inscribe.ELEMENT_HELPER.asIdentifier(xml, "id");
		//Safe to assume the list has exactly one element thanks to schemas
		GuideAccessMethod guideAccess = GuideAccessMethod.deserialiseGuideAccess(Inscribe.ELEMENT_HELPER.getChild(xml, "access_method").getChildren().get(0));
		Element themeXml = xml.getChild("theme", Inscribe.XML_NAMESPACE);
		Theme theme = themeXml != null ? Theme.fromXml(themeXml) : Theme.DEFAULT;
		return new GuideDefinition(guideId, guideAccess, theme);
	}

	public Identifier getGuideId()
	{
		return guideId;
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
