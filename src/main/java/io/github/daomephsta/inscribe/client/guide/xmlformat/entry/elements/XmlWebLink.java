package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import java.net.URL;
import java.util.List;

import io.github.daomephsta.inscribe.client.guide.xmlformat.base.XmlMixedContent;

public class XmlWebLink extends XmlMixedContent
{
	private final URL target;

	public XmlWebLink(List<Object> content, URL target)
	{
		super(content);
		this.target = target;
	}

	public URL getTarget()
	{
		return target;
	}
}
