package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.net.MalformedURLException;
import java.net.URL;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.GuideLoadingException.Severity;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.*;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlWebLink;
import net.minecraft.util.Lazy;

class XmlWebLinkElementType extends XmlElementType<XmlWebLink>
{
	private final Lazy<ContentDeserialiser> contentDeserialiser;

	XmlWebLinkElementType()
	{
		super("web_link", XmlWebLink.class);
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
    public XmlWebLink fromXml(Element xml) throws GuideLoadingException
	{
		String url = XmlAttributes.getValue(xml, "target");
		try
		{
			URL targetUrl = new URL(url);
			return new XmlWebLink(contentDeserialiser.get().deserialise(xml.getContent()), targetUrl);
		}
		catch (MalformedURLException e)
		{
			throw new GuideLoadingException("Could not parse URL " + url, e, Severity.FATAL);
		}
	}
}
