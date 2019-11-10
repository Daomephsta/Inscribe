package io.github.daomephsta.inscribe.client.guide.xmlformat;

import java.util.*;
import java.util.stream.Collectors;

import org.jdom2.*;

import com.google.common.collect.Sets;

import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class XmlAttributes
{
	/**
	 * Gets an xml attribute as an integer
	 * @param xml the element with the attribute
	 * @param attributeName the name of the attribute
	 * @throws
	 * InscribeSyntaxException if the attribute does not exist
	 * or its value cannot be parsed as an integer
	 * @return the value of the attribute as an integer
	 */
	public static int asInt(Element xml, String attributeName) throws InscribeSyntaxException
	{
		Attribute attribute = get(xml, attributeName);
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
	 * InscribeSyntaxException if the attribute's value cannot be parsed as an integer
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
     * Gets an xml attribute as an Optional<String>
     * @param xml the element with the attribute
     * @param attributeName the name of the attribute
     * @return the value of the attribute as an Optional<String>, or {@link Optional#empty()}
     * if the attribute does not exist.
     */
    public static Optional<String> asOptionalString(Element xml, String attributeName)
    {
        Attribute attribute = xml.getAttribute(attributeName);
        if (attribute == null)
            return Optional.empty();
        return Optional.of(attribute.getValue());
    }

	/**
	 * Gets an xml attribute as a ModelIdentifier
	 * @param xml the element with the attribute
	 * @param attributeName the name of the attribute
	 * @throws
	 * InscribeSyntaxException if the attribute does not exist
	 * or its value cannot be parsed as a ModelIdentifier
	 * @return the value of the attribute as a ModelIdentifier
	 */
	public static ModelIdentifier asModelIdentifier(Element xml, String attributeName) throws InscribeSyntaxException
	{
		String attributeValue = getValue(xml, attributeName);
		try
		{
			return new ModelIdentifier(attributeValue);
		}
		catch (InvalidIdentifierException e)
		{
			throw new InscribeSyntaxException(e.getMessage());
		}
	}

	/**
	 * Gets an xml attribute as an Identifier
	 * @param xml the element with the attribute
	 * @param attributeName the name of the attribute
	 * @throws
	 * InscribeSyntaxException if the attribute does not exist
	 * or its value cannot be parsed as an Identifier
	 * @return the value of the attribute as an Identifier
	 */
	public static Identifier asIdentifier(Element xml, String attributeName) throws InscribeSyntaxException
	{
		String attributeValue = getValue(xml, attributeName);
		try
		{
			return new Identifier(attributeValue);
		}
		catch (InvalidIdentifierException e)
		{
			throw new InscribeSyntaxException(e.getMessage());
		}
	}

	/**
	 * Gets the attribute named {@code attributeName}.
	 * @param xml the parent element
	 * @param attributeName the name of the child element
	 * @throws
	 * InscribeSyntaxException if the attribute does not exist
	 * @return the attribute
	 */
	public static Attribute get(Element xml, String attributeName) throws InscribeSyntaxException
	{
		Attribute attribute = xml.getAttribute(attributeName);
		if (attribute == null)
			throw noAttributeException(xml, attributeName);
		return attribute;
	}

	/**
	 * Gets the value of the attribute named {@code attributeName}.
	 * @param xml the parent element
	 * @param attributeName the name of the child element
	 * @throws
	 * InscribeSyntaxException if the attribute does not exist
	 * @return the value of the attribute
	 */
	public static String getValue(Element xml, String attributeName) throws InscribeSyntaxException
	{
		return get(xml, attributeName).getValue();
	}

	public static Preconditions preconditions()
	{
		return new Preconditions();
	}

	private static InscribeSyntaxException noAttributeException(Element xml, String attributeName)
	{
		return new InscribeSyntaxException(String.format("No attribute named '%s' found in element of type %s", attributeName, xml.getQualifiedName()));
	}

	private static RuntimeException wrappedDataConversionException(String attributeName, DataConversionException e)
	{
		return new InscribeXmlParseException(String.format("Could not parse value of attribute '%s' as an integer", attributeName), e);
	}

	public static class Preconditions
	{
		private Collection<String> requiredAttributes = Collections.emptySet(),
								   optionalAttributes = Collections.emptySet();

		private Preconditions() {}

		public Preconditions required(String... required)
		{
			this.requiredAttributes = Sets.newHashSet(required);
			return this;
		}

		public Preconditions optional(String... optional)
		{
			this.optionalAttributes = Sets.newHashSet(optional);
			return this;
		}

		public void validate(Element xml) throws InscribeSyntaxException
		{
			String unknown = xml.getAttributes().stream()
								.map(Attribute::getName)
								.filter(a -> !(requiredAttributes.contains(a) || optionalAttributes.contains(a)))
								.collect(Collectors.joining(", "));
			if (!unknown.isEmpty())
				throw new InscribeSyntaxException("Unknown attributes for " + xml + ": " + unknown);

			String missing = requiredAttributes.stream()
								.filter(a -> xml.getAttribute(a) == null)
								.collect(Collectors.joining(", "));
			if (!missing.isEmpty())
				throw new InscribeSyntaxException("Missing required attributes for " + xml + ": " + missing);
		}
	}
}
