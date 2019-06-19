package io.github.daomephsta.inscribe.client.guide.parser.v100;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.*;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntryLink;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;

class XmlEntryLinkElementType extends XmlElementType<XmlEntryLink>
{
	private final Lazy<ContentDeserialiser> contentDeserialiser;
	
	XmlEntryLinkElementType()
	{
		super("entry_link", XmlEntryLink.class);
		this.contentDeserialiser = new Lazy<>
		(() -> 
			new ContentDeserialiser.Impl()
				.registerDeserialiser(V100ElementTypes.BOLD)
				.registerDeserialiser(V100ElementTypes.STRONG)
				.registerDeserialiser(V100ElementTypes.EMPHASIS)
				.registerDeserialiser(V100ElementTypes.ITALICS)
				.registerDeserialiser(V100ElementTypes.DEL)
				.registerDeserialiser(V100ElementTypes.IMAGE)
		);
	}
	
	@Override
	protected XmlEntryLink translate(Element xml) throws InscribeSyntaxException
	{
		Identifier entryId = XmlAttributes.asIdentifier(xml, "entry");
		String anchorId = xml.getAttributeValue("anchor");
		return new XmlEntryLink(contentDeserialiser.get().deserialise(xml.getContent()), entryId, anchorId);
	}
}