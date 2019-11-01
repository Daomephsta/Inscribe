package io.github.daomephsta.inscribe.client.guide.xmlformat;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.*;
import org.jdom2.Content.CType;

import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;

public interface ContentDeserialiser
{
	public List<Object> deserialise(List<Content> list) throws InscribeSyntaxException;

	public static class Impl implements ContentDeserialiser
	{
		private final Map<String, XmlElementType<?>> deserialisers = new HashMap<>();
		private static final Logger LOGGER = LogManager.getLogger();

		public Impl registerDeserialiser(XmlElementType<?> elementType)
		{
			deserialisers.put(elementType.getElementName(), elementType);
			return this;
		}

		@Override
		public List<Object> deserialise(List<Content> list) throws InscribeSyntaxException
		{
			List<Object> result = new ArrayList<>();
			for (Content content : list)
			{
				if (isMetadata(content))
				{
					LOGGER.debug("[Inscribe] {} not parsed as element content as it is metadata", content);
					continue;
				}
				else switch (content.getCType())
				{
				case Element:
					Element element = (Element) content;
					XmlElementType<?> deserialiser = deserialisers.get(element.getName());
					if (deserialiser == null)
					{
						LOGGER.debug("[Inscribe] Ignored unknown element {}", element);
						continue;
					}
					else
						result.add(deserialiser.fromXml(element));
					break;
				case Text:
					result.add(content.getValue());
					break;
				default:
					Object[] args = {content};
					LOGGER.debug("[Inscribe] Ignored {} as it is not text or an element", args);
					break;
				}
			}
			return result;
		}

		private boolean isMetadata(Content content)
		{
			if (content.getCType() == CType.Element) try
			{
				Attribute metadata = ((Element) content).getAttribute("metadata");
				return metadata != null && metadata.getBooleanValue();
			}
			catch (DataConversionException e)
			{
				e.printStackTrace();
			}
			return false;
		}
	}
}
