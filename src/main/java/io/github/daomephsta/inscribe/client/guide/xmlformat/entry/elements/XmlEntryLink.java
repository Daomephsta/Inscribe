package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import java.util.List;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.DeserialisationManager;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.XmlMixedContent;
import net.minecraft.util.Identifier;

public class XmlEntryLink extends XmlMixedContent
{
	public static final XmlElementType<XmlEntryLink> XML_TYPE = new XmlType();
	
	private final Identifier entryId;
	private final String anchor;
	
	private XmlEntryLink(List<Object> content, Identifier entryId, String anchor)
	{
		super(content);
		this.entryId = entryId;
		this.anchor = anchor;
	}

	public Identifier getEntryId()
	{
		return entryId;
	}

	public String getAnchor()
	{
		return anchor;
	}
	
	private static class XmlType extends XmlElementType<XmlEntryLink>
	{
		private XmlType()
		{
			super("entry_link", XmlEntryLink.class);
		}

		@Override
		public XmlEntryLink fromXml(Element xml, DeserialisationManager manager)
		{
			Identifier entryId = new Identifier(xml.getAttributeValue("entry"));
			String anchor = xml.getAttributeValue("anchor");
			return new XmlEntryLink(manager.deserialiseContent(xml.getContent()), entryId, anchor);
		}
	}
}
