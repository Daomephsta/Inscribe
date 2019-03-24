package io.github.daomephsta.inscribe.client.guide.xmlformat.theme;

import java.util.Optional;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class Theme
{
	private final Identifier id;
	private final Optional<Identifier> pageTexture;
	private final Optional<ModelIdentifier> modelId;

	private Theme(Identifier id, Optional<Identifier> pageTexture, Optional<ModelIdentifier> modelId)
	{
		this.id = id;
		this.pageTexture = pageTexture;
		this.modelId = modelId;
	}

	public static Theme fromXml(Element xml)
	{
		Identifier id = new Identifier(xml.getChildText("id", Inscribe.XML_NAMESPACE));
		//TODO: FINISH THEMES
		//Identifier pageTexture = new Identifier(xml.getChildText("page_texture", Inscribe.XML_NAMESPACE));
		//ModelIdentifier modelId = new ModelIdentifier(xml.getChildText("model", Inscribe.XML_NAMESPACE));
		return new Theme(id, Optional.empty(), Optional.empty());
	}

	public Identifier getIdentifier()
	{
		return id;
	}
	
	public Optional<Identifier> getPageTexture()
	{
		return pageTexture;
	}
	
	public Optional<ModelIdentifier> getModelId()
	{
		return modelId;
	}
}
