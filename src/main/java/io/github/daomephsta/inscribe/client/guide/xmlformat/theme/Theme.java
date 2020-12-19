package io.github.daomephsta.inscribe.client.guide.xmlformat.theme;

import org.w3c.dom.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.util.Identifier;

public class Theme
{
    public static final Theme DEFAULT = new Theme(new Identifier(Inscribe.MOD_ID, "textures/gui/guide.png"));

    private final Identifier guiTexture;

    private Theme(Identifier guiTexture)
    {
        this.guiTexture = guiTexture;
    }

    public static Theme fromXml(Element xml) throws InscribeSyntaxException
    {
        Identifier pageTexture = XmlAttributes.asIdentifier(xml, "gui_texture", DEFAULT.getGuiTexture());
        return new Theme(pageTexture);
    }

    public Identifier getGuiTexture()
    {
        return guiTexture;
    }
}
