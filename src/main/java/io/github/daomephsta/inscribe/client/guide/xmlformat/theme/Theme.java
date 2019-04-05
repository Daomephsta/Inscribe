package io.github.daomephsta.inscribe.client.guide.xmlformat.theme;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.util.Identifier;

public class Theme
{
	private static final Identifier DEFAULT_GUI_TEXTURE = new Identifier("minecraft:textures/gui/book.png");
	public static final Theme DEFAULT = new Theme(DEFAULT_GUI_TEXTURE);
	
	private final Identifier guiTexture;

	private Theme(Identifier guiTexture)
	{
		this.guiTexture = guiTexture;
	}

	public static Theme fromXml(Element xml)
	{
		Identifier pageTexture = Inscribe.ELEMENT_HELPER.asIdentifier(xml, "gui_texture", DEFAULT_GUI_TEXTURE);
		return new Theme(pageTexture);
	}
	
	public Identifier getGuiTexture()
	{
		return guiTexture;
	}
}
