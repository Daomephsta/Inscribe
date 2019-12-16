package io.github.daomephsta.inscribe.client.guide.xmlformat;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.google.common.base.Splitter;

import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class XmlAttributes
{
    /**
     * Gets an xml attribute as a boolean
     * @param xml the element with the attribute
     * @param attributeName the name of the attribute
     * @throws InscribeSyntaxException if the attribute does not exist
     * or its value cannot be parsed as a boolean
     * @return the value of the attribute as a boolean
     */
    public static boolean asBoolean(Element xml, String attributeName) throws InscribeSyntaxException
    {
        String attributeValue = getValue(xml, attributeName);
        if (attributeValue.equals("true"))
            return true;
        else if (attributeValue.equals("false"))
            return false;
        else
            throw new InscribeSyntaxException("Boolean values must be either 'true' or 'false'");
    }

    /**
     * Gets an xml attribute as a boolean
     * @param xml the element with the attribute
     * @param attributeName the name of the attribute
     * @param fallback the return value if the attribute does not exist
     * @throws InscribeSyntaxException if the attribute's value cannot be parsed as a boolean
     * @return the value of the attribute as a boolean or {@code fallback} if it does not exist
     */
    public static boolean asBoolean(Element xml, String attributeName, boolean fallback) throws InscribeSyntaxException
    {
        Attr attribute = xml.getAttributeNode(attributeName);
        if (attribute == null)
            return fallback;
        if (attribute.getValue().equals("true"))
            return true;
        else if (attribute.getValue().equals("false"))
            return false;
        else
            throw new InscribeSyntaxException("Boolean values must be either 'true' or 'false'");
    }

    /**
     * Gets an xml attribute as an integer
     * @param xml the element with the attribute
     * @param attributeName the name of the attribute
     * @throws InscribeSyntaxException if the attribute does not exist
     * or its value cannot be parsed as an integer
     * @return the value of the attribute as an integer
     */
    public static int asInt(Element xml, String attributeName) throws InscribeSyntaxException
    {
        Attr attribute = get(xml, attributeName);
        try
        {
            return Integer.parseInt(attribute.getValue());
        }
        catch (NumberFormatException e)
        {
            throw wrappedNumberFormatException(xml, attributeName, e);
        }
    }

    /**
     * Gets an xml attribute as an integer
     * @param xml the element with the attribute
     * @param attributeName the name of the attribute
     * @param fallback the return value if the attribute does not exist
     * @throws InscribeSyntaxException if the attribute's value cannot be parsed as an integer
     * @return the value of the attribute as an integer or {@code fallback} if it does not exist
     */
    public static int asInt(Element xml, String attributeName, int fallback) throws InscribeSyntaxException
    {
        Attr attribute = xml.getAttributeNode(attributeName);
        if (attribute == null)
            return fallback;
        try
        {
            return Integer.parseInt(attribute.getValue());
        }
        catch (NumberFormatException e)
        {
            throw wrappedNumberFormatException(xml, attributeName, e);
        }
    }

    /**
     * Gets an xml attribute as a long
     * @param xml the element with the attribute
     * @param attributeName the name of the attribute
     * @throws InscribeSyntaxException if the attribute does not exist
     * or its value cannot be parsed as a long
     * @return the value of the attribute as a long
     */
    public static long asLong(Element xml, String attributeName) throws InscribeSyntaxException
    {
        Attr attribute = get(xml, attributeName);
        try
        {
            return Long.parseLong(attribute.getValue());
        }
        catch (NumberFormatException e)
        {
            throw wrappedNumberFormatException(xml, attributeName, e);
        }
    }

    /**
     * Gets an xml attribute as a long
     * @param xml the element with the attribute
     * @param attributeName the name of the attribute
     * @param fallback the return value if the attribute does not exist
     * @throws InscribeSyntaxException if the attribute's value cannot be parsed as a long
     * @return the value of the attribute as a long or {@code fallback} if it does not exist
     */
    public static long asLong(Element xml, String attributeName, long fallback) throws InscribeSyntaxException
    {
        Attr attribute = xml.getAttributeNode(attributeName);
        if (attribute == null)
            return fallback;
        try
        {
            return Long.parseLong(attribute.getValue());
        }
        catch (NumberFormatException e)
        {
            throw wrappedNumberFormatException(xml, attributeName, e);
        }
    }

    /**
     * Gets an xml attribute as a float
     * @param xml the element with the attribute
     * @param attributeName the name of the attribute
     * @throws InscribeSyntaxException if the attribute does not exist
     * or its value cannot be parsed as a float
     * @return the value of the attribute as a float
     */
    public static float asFloat(Element xml, String attributeName) throws InscribeSyntaxException
    {
        Attr attribute = get(xml, attributeName);
        try
        {
            return Float.parseFloat(attribute.getValue());
        }
        catch (NumberFormatException e)
        {
            throw wrappedNumberFormatException(xml, attributeName, e);
        }
    }

    /**
     * Gets an xml attribute as a float
     * @param xml the element with the attribute
     * @param attributeName the name of the attribute
     * @param fallback the return value if the attribute does not exist
     * @throws InscribeSyntaxException if the attribute's value cannot be parsed as a float
     * @return the value of the attribute as a float or {@code fallback} if it does not exist
     */
    public static float asFloat(Element xml, String attributeName, float fallback) throws InscribeSyntaxException
    {
        Attr attribute = xml.getAttributeNode(attributeName);
        if (attribute == null)
            return fallback;
        try
        {
            return Float.parseFloat(attribute.getValue());
        }
        catch (NumberFormatException e)
        {
            throw wrappedNumberFormatException(xml, attributeName, e);
        }
    }

    /**
     * Gets an xml attribute as a double
     * @param xml the element with the attribute
     * @param attributeName the name of the attribute
     * @throws InscribeSyntaxException if the attribute does not exist
     * or its value cannot be parsed as a double
     * @return the value of the attribute as a double
     */
    public static double asDouble(Element xml, String attributeName) throws InscribeSyntaxException
    {
        Attr attribute = get(xml, attributeName);
        try
        {
            return Double.parseDouble(attribute.getValue());
        }
        catch (NumberFormatException e)
        {
            throw wrappedNumberFormatException(xml, attributeName, e);
        }
    }

    /**
     * Gets an xml attribute as a double
     * @param xml the element with the attribute
     * @param attributeName the name of the attribute
     * @param fallback the return value if the attribute does not exist
     * @throws InscribeSyntaxException if the attribute's value cannot be parsed as a double
     * @return the value of the attribute as a double or {@code fallback} if it does not exist
     */
    public static double asDouble(Element xml, String attributeName, double fallback) throws InscribeSyntaxException
    {
        Attr attribute = xml.getAttributeNode(attributeName);
        if (attribute == null)
            return fallback;
        try
        {
            return Double.parseDouble(attribute.getValue());
        }
        catch (NumberFormatException e)
        {
            throw wrappedNumberFormatException(xml, attributeName, e);
        }
    }

    /**
     * Gets an xml attribute as a ModelIdentifier
     * @param xml the element with the attribute
     * @param attributeName the name of the attribute
     * @throws InscribeSyntaxException if the attribute does not exist
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
     * @throws InscribeSyntaxException if the attribute does not exist
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
     * Gets an xml attribute as an Identifier
     * @param xml the element with the attribute
     * @param attributeName the name of the attribute
     * @param fallback the return value if the attribute does not exist
     * @throws InscribeSyntaxException if the attribute's value cannot be parsed as an Identifier
     * @return the value of the attribute as an Identifier or {@code fallback} if it does not exist
     */
    public static Identifier asIdentifier(Element xml, String attributeName, Identifier fallback) throws InscribeSyntaxException
    {
        Attr attribute = xml.getAttributeNode(attributeName);
        if (attribute == null)
            return fallback;
        try
        {
            return new Identifier(attribute.getValue());
        }
        catch (InvalidIdentifierException e)
        {
            throw new InscribeSyntaxException(e.getMessage());
        }
    }

    /**
     * Gets an xml attribute as an enum constant
     * @param <E> the enum type
     * @param xml the element with the attribute
     * @param attributeName the name of the attribute
     * @param getter a function to convert a string to an enum constant
     * @throws InscribeSyntaxException if the attribute does not exist
     * or its value cannot be parsed as an enum constant
     * @return the value of the attribute as an enum constant
     */
    public static <E extends Enum<E>> E asEnum(Element xml, String attributeName, Function<String, E> getter) throws InscribeSyntaxException
    {
        String attributeValue = getValue(xml, attributeName);
        E result = getter.apply(attributeValue);
        if (result == null)
             throw new InscribeSyntaxException("Could not parse " + attributeValue + " as an enum constant");
        return result;
    }

    /**
     * Gets an xml attribute as a String array
     * @param xml the parent element
     * @param attributeName the name of the attribute
     * @param fallback supplies a fallback value
     * @return the value of the attribute as a String array or
     * the result of {@code fallback} if it does not exist
     */
    public static List<String> asStringList(Element xml, String attributeName, Supplier<List<String>> fallback)
    {
        Attr attribute = xml.getAttributeNode(attributeName);
        if (attribute == null)
            return fallback.get();
        return toStringList(attribute.getValue());
    }

    /**
     * Gets an xml attribute as a String array
     * @param xml the parent element
     * @param attributeName the name of the attribute
     * @throws InscribeSyntaxException if the attribute does not exist
     * @return the value of the attribute as a String array
     */
    public static List<String> asStringList(Element xml, String attributeName) throws InscribeSyntaxException
    {
        return toStringList(getValue(xml, attributeName));
    }

    private static final Splitter ON_COMMA = Splitter.on(',').trimResults();
    private static List<String> toStringList(String s)
    {
        return ON_COMMA.splitToList(s);
    }


    /**
     * Gets the attribute named {@code attributeName}.
     * @param xml the parent element
     * @param attributeName the name of the child element
     * @throws InscribeSyntaxException if the attribute does not exist
     * @return the attribute
     */
    public static Attr get(Element xml, String attributeName) throws InscribeSyntaxException
    {
        Attr attribute = xml.getAttributeNode(attributeName);
        if (attribute == null)
            throw noAttributeException(xml, attributeName);
        return attribute;
    }

    /**
     * Gets the value of the attribute named {@code attributeName}.
     * @param xml the parent element
     * @param attributeName the name of the child element
     * @param fallback the return value if the attribute does not exist
     * @return the value of the attribute or {@code fallback} if it does not exist
     */
    public static String getValue(Element xml, String attributeName, String fallback)
    {
        Attr attribute = xml.getAttributeNode(attributeName);
        return attribute != null ? attribute.getValue() : fallback;
    }

    /**
     * Gets the value of the attribute named {@code attributeName}.
     * @param xml the parent element
     * @param attributeName the name of the child element
     * @throws InscribeSyntaxException if the attribute does not exist
     * @return the value of the attribute
     */
    public static String getValue(Element xml, String attributeName) throws InscribeSyntaxException
    {
        return get(xml, attributeName).getValue();
    }

    /**
     * Ensures that certain attributes are specified
     * @param xml the parent element
     * @param required an array of attribute names that are required to be specified
     * @throws InscribeSyntaxException if any of the specified attributes do not exist
     */
    public static void requireAttributes(Element xml, String... required) throws InscribeSyntaxException
    {
        String missing = Arrays.stream(required)
            .filter(a -> xml.getAttributeNode(a) == null)
            .collect(Collectors.joining(", "));
        if (!missing.isEmpty())
            throw new InscribeSyntaxException("Missing required attributes for " + xml + ": " + missing);
    }

    private static InscribeSyntaxException noAttributeException(Element xml, String attributeName)
    {
        return new InscribeSyntaxException(String.format("No attribute named '%s' found in element %s",
            attributeName, XmlElements.getDebugString(xml)));
    }

    private static RuntimeException wrappedNumberFormatException(Element parent, String attributeName, NumberFormatException e)
    {
        return new InscribeXmlParseException(String.format("Could not parse value of attribute '%s' of %s as an integer",
            attributeName, XmlElements.getDebugString(parent)), e);
    }
}
