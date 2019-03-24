package io.github.daomephsta.inscribe.client.guide.xmlformat.base;

import java.util.List;
import java.util.function.Function;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.DeserialisationManager;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlElementType;

public abstract class XmlMixedContent implements IXmlRepresentation
{
	private final List<Object> content;

	public XmlMixedContent(List<Object> content)
	{
		this.content = content;
	}
	
	public List<Object> getContent()
	{
		return content;
	}
	
	public static class XmlParagraph extends XmlMixedContent
	{
		public static final XmlElementType<XmlParagraph> XML_TYPE = 
			new SimpleXmlMixedContentType<>("p", XmlParagraph.class, XmlParagraph::new);
			
		private XmlParagraph(List<Object> content)
		{
			super(content);
		}
	}
	
	public static class XmlBold extends XmlMixedContent
	{
		public static final XmlElementType<XmlBold> XML_TYPE = 
			new SimpleXmlMixedContentType<>("b", XmlBold.class, XmlBold::new);
		
		private XmlBold(List<Object> content)
		{
			super(content);
		}
	}
	
	public static class XmlStrong extends XmlBold
	{
		public static final XmlElementType<XmlStrong> XML_TYPE = 
			new SimpleXmlMixedContentType<>("strong", XmlStrong.class, XmlStrong::new);
		
		private XmlStrong(List<Object> content)
		{
			super(content);
		}
	}
	
	public static class XmlItalics extends XmlMixedContent
	{
		public static final XmlElementType<XmlItalics> XML_TYPE = 
			new SimpleXmlMixedContentType<>("i", XmlItalics.class, XmlItalics::new);
		
		private XmlItalics(List<Object> content)
		{
			super(content);
		}
	}	
	
	public static class XmlEmphasis extends XmlItalics
	{
		public static final XmlElementType<XmlEmphasis> XML_TYPE = 
			new SimpleXmlMixedContentType<>("em", XmlEmphasis.class, XmlEmphasis::new);
		
		private XmlEmphasis(List<Object> content)
		{
			super(content);
		}
	}	
	
	public static class XmlDel extends XmlMixedContent
	{
		public static final XmlElementType<XmlDel> XML_TYPE = 
			new SimpleXmlMixedContentType<>("del", XmlDel.class, XmlDel::new);
		
		private XmlDel(List<Object> content)
		{
			super(content);
		}
	}
	
	private static class SimpleXmlMixedContentType<T extends XmlMixedContent> extends XmlElementType<T>
	{
		private final Function<List<Object>, T> constructorHandle;

		private SimpleXmlMixedContentType(String elementName, Class<T> clazz, Function<List<Object>, T> constructorHandle)
		{
			super(elementName, clazz);
			this.constructorHandle = constructorHandle;
		}

		@Override
		public T fromXml(Element xml, DeserialisationManager manager)
		{
			return constructorHandle.apply(manager.deserialiseContent(xml.getContent()));
		}
	}
}
