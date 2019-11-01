package io.github.daomephsta.inscribe.client.guide.xmlformat.theme;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlElements;
import net.minecraft.util.Identifier;

public class Theme
{
	public static final Theme DEFAULT = new Theme(new Identifier("minecraft:textures/gui/book.png"));

	private final Identifier guiTexture;

	private Theme(Identifier guiTexture)
	{
		this.guiTexture = guiTexture;
	}

	public static Theme fromXml(Element xml) throws InscribeSyntaxException
	{
		Identifier pageTexture = XmlElements.asIdentifier(xml, "gui_texture", DEFAULT.getGuiTexture());
		return new Theme(pageTexture);
	}

	public Identifier getGuiTexture()
	{
		return guiTexture;
	}
}
