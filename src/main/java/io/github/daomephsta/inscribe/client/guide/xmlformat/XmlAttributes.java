package io.github.daomephsta.inscribe.client.guide.xmlformat;

import java.util.OptionalInt;

import org.jdom2.*;

import net.minecraft.util.Identifier;

public class XmlAttributes
{
	private final Namespace defaultNamespace;
	
	public XmlAttributes(Namespace defaultNamespace)
	{
		this.defaultNamespace = defaultNamespace;
	}

	/**
	 * Gets an xml attribute as an integer
	 * @param xml the element with the attribute
	 * @param attributeName the name of the attribute
	 * @throws 
	 * XmlSyntaxException if the attribute does not exist 
	 * or its value cannot be parsed as an integer
	 * @return the value of the attribute as an integer
	 */
	public int asInt(Element xml, String attributeName)
	{
		Attribute attribute = xml.getAttribute(attributeName, defaultNamespace);
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
	public OptionalInt asOptionalInt(Element xml, String attributeName)
	{
		Attribute attribute = xml.getAttribute(attributeName, defaultNamespace);
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
	public Identifier asIdentifier(Element xml, String attributeName)
	{
		Attribute attribute = xml.getAttribute(attributeName, defaultNamespace);
		if (attribute == null) 
			throw noAttributeException(xml, attributeName);
		return new Identifier(attribute.getValue());
	}

	private XmlSyntaxException noAttributeException(Element xml, String attributeName)
	{
		return new XmlSyntaxException(String.format("No attribute named '%s' found in element of type %s", attributeName, xml.getQualifiedName()));
	}

	private XmlSyntaxException wrappedDataConversionException(String attributeName, DataConversionException e)
	{
		return new XmlSyntaxException(String.format("Could not parse value of attribute '%s' as an integer", attributeName), e);
	}
}
