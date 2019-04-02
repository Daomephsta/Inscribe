package io.github.daomephsta.inscribe.client.guide.xmlformat;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.*;
import org.jdom2.Content.CType;

import com.google.common.collect.ImmutableMap;

public class DeserialisationManager
{	
	private final Map<String, XmlElementType<?>> deserialisers;
	private static final Logger LOGGER = LogManager.getLogger();
	
	public DeserialisationManager(XmlElementType<?>... xmlElementTypes)
	{
		this.deserialisers = Arrays.stream(xmlElementTypes)
			.collect(ImmutableMap.toImmutableMap(XmlElementType::getElementName, type -> type));
	}
	
	public List<Object> deserialiseContent(List<Content> list)
	{
		List<Object> result = new ArrayList<>();
		for (Content content : list)
		{
			if (isMetadata(content))
			{
				Object[] args = {content};
				LOGGER.debug("[Inscribe] {} not parsed as element content as it is metadata", args);
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
					result.add(deserialiser.fromXml(element, this));
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
	
	public <T> T deserialise(Element element, Class<T> expectedType, boolean allowUnknowns)
	{
		XmlElementType<?> deserialiser = deserialisers.get(element.getName());
		if (deserialiser == null)
		{
			if (allowUnknowns)
			{
				LOGGER.debug("[Inscribe] Ignored unknown element {}", element);
				return null;
			}
			else 
				throw new InscribeXmlParseException("Unknown element " + element);
		}
		else
		{
			Object object = deserialiser.fromXml(element, this);
			if (expectedType.isInstance(object))
				return expectedType.cast(object);
			else
				throw new InscribeXmlParseException(String.format("Cannot parse %s as an instance of %s", element, expectedType.getName()));
		}
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
