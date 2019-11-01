package io.github.daomephsta.inscribe.client.guide.xmlformat.base;

import java.util.List;

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
		public XmlParagraph(List<Object> content)
		{
			super(content);
		}
	}

	public static class XmlBold extends XmlMixedContent
	{
		public XmlBold(List<Object> content)
		{
			super(content);
		}
	}

	public static class XmlStrong extends XmlBold
	{
		public XmlStrong(List<Object> content)
		{
			super(content);
		}
	}

	public static class XmlItalics extends XmlMixedContent
	{
		public XmlItalics(List<Object> content)
		{
			super(content);
		}
	}

	public static class XmlEmphasis extends XmlItalics
	{
		public XmlEmphasis(List<Object> content)
		{
			super(content);
		}
	}

	public static class XmlDel extends XmlMixedContent
	{
		public XmlDel(List<Object> content)
		{
			super(content);
		}
	}
}
