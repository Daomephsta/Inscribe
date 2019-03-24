package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.DeserialisationManager;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;

public class XmlAnchor implements IXmlRepresentation
{
	public static final XmlElementType<XmlAnchor> XML_TYPE = new XmlType();
	
	private final String name;
	
	private XmlAnchor(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	private static class XmlType extends XmlElementType<XmlAnchor>
	{
		private XmlType()
		{
			super("anchor", XmlAnchor.class);
		}

		@Override
		public XmlAnchor fromXml(Element xml, DeserialisationManager manager)
		{
			String name = xml.getAttributeValue("name");
			return new XmlAnchor(name);
		}
	}
}
