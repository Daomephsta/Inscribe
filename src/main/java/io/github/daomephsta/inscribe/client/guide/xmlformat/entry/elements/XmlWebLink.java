package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.*;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.XmlMixedContent;

public class XmlWebLink extends XmlMixedContent
{
	public static final XmlElementType<XmlWebLink> XML_TYPE = new XmlType();
	
	private final URL target;

	private XmlWebLink(List<Object> content, URL target)
	{
		super(content);
		this.target = target;
	}
	
	public URL getTarget()
	{
		return target;
	}
	
	private static class XmlType extends XmlElementType<XmlWebLink>
	{
		private XmlType()
		{
			super("web_link", XmlWebLink.class);
		}

		@Override
		public XmlWebLink fromXml(Element xml, DeserialisationManager manager)
		{
			try
			{
				URL targetUrl = new URL(xml.getAttributeValue("target"));
				return new XmlWebLink(manager.deserialiseContent(xml.getContent()), targetUrl);
			}
			catch (MalformedURLException e)
			{
				throw new InscribeXmlParseException("Bother", e);
			}
		}
	}
}
