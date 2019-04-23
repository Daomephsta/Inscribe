package io.github.daomephsta.inscribe.client.guide.xmlformat;

import java.util.OptionalInt;

import org.jdom2.*;

import net.minecraft.util.Identifier;

public class XmlAttributes
{
	/**
	 * Gets an xml attribute as an integer
	 * @param xml the element with the attribute
	 * @param attributeName the name of the attribute
	 * @throws 
	 * XmlSyntaxException if the attribute does not exist 
	 * or its value cannot be parsed as an integer
	 * @return the value of the attribute as an integer
	 */
	public static int asInt(Element xml, String attributeName)
	{
		Attribute attribute = xml.getAttribute(attributeName);
		if (attribute == null) 
			throw noAttributeException(xml, attributeName);
		try
		{
			return attribute.getIntValue();
		}
		catch (DataConversionException e) 
		{
			throw wrappedDataConversionException(attributeName, e);
		}
	}
	
	/**
	 * Gets an xml attribute as an OptionalInt
	 * @param xml the element with the attribute
	 * @param attributeName the name of the attribute
	 * @throws 
	 * XmlSyntaxException if the attribute's value cannot be parsed as an integer
	 * @return the value of the attribute as an OptionalInt, or {@link OptionalInt#empty()}
	 * if the attribute does not exist.
	 */
	public static OptionalInt asOptionalInt(Element xml, String attributeName)
	{
		Attribute attribute = xml.getAttribute(attributeName);
		if (attribute == null) 
			return OptionalInt.empty();
		try
		{
			return OptionalInt.of(attribute.getIntValue());
		}
		catch (DataConversionException e) 
		{
			throw wrappedDataConversionException(attributeName, e);
		}
	}
	
	/**
	 * Gets an xml attribute as an Identifier
	 * @param xml the element with the attribute
	 * @param attributeName the name of the attribute
	 * @throws 
	 * XmlSyntaxException if the attribute does not exist 
	 * or its value cannot be parsed as an Identifier
	 * @return the value of the attribute as an Identifier
	 */
	public static Identifier asIdentifier(Element xml, String attributeName)
	{
		Attribute attribute = xml.getAttribute(attributeName);
		if (attribute == null) 
			throw noAttributeException(xml, attributeName);
		return new Identifier(attribute.getValue());
	}

	private static RuntimeException noAttributeException(Element xml, String attributeName)
	{
		return new InscribeSyntaxException(String.format("No attribute named '%s' found in element of type %s", attributeName, xml.getQualifiedName()));
	}

	private static RuntimeException wrappedDataConversionException(String attributeName, DataConversionException e)
	{
		return new InscribeSyntaxException(String.format("Could not parse value of attribute '%s' as an integer", attributeName), e);
	}
}
