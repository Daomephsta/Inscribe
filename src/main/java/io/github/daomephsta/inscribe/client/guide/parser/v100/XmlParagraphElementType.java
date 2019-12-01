package io.github.daomephsta.inscribe.client.guide.parser.v100;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.ContentDeserialiser;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.XmlMixedContent.XmlParagraph;
import net.minecraft.util.Lazy;

class XmlParagraphElementType extends XmlElementType<XmlParagraph>
{
	private final Lazy<ContentDeserialiser> contentDeserialiser;

	public XmlParagraphElementType()
	{
		super("p", XmlParagraph.class);
		this.contentDeserialiser = new Lazy<>
		(() ->
			new ContentDeserialiser.Impl()
				.registerDeserialiser(V100ElementTypes.BOLD)
				.registerDeserialiser(V100ElementTypes.STRONG)
				.registerDeserialiser(V100ElementTypes.EMPHASIS)
				.registerDeserialiser(V100ElementTypes.ITALICS)
				.registerDeserialiser(V100ElementTypes.DEL)
				.registerDeserialiser(V100ElementTypes.ANCHOR)
				.registerDeserialiser(V100ElementTypes.ENTRY_LINK)
				.registerDeserialiser(V100ElementTypes.WEB_LINK)
				.registerDeserialiser(V100ElementTypes.IMAGE)
		);
	}

	@Override
    public XmlParagraph fromXml(Element xml) throws GuideLoadingException
	{
		return new XmlParagraph(contentDeserialiser.get().deserialise(xml.getContent()));
	}
}
