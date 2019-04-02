package io.github.daomephsta.inscribe.client.guide.xmlformat.theme;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.util.Identifier;

public class Theme
{
	private final Identifier id;

	private Theme(Identifier id)
	{
		this.id = id;
	}

	public static Theme fromXml(Element xml)
	{
		Identifier id = new Identifier(xml.getChildText("id", Inscribe.XML_NAMESPACE));
		//TODO: FINISH THEMES
		return new Theme(id);
	}

	public Identifier getIdentifier()
	{
		return id;
	}
}
