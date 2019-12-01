package io.github.daomephsta.inscribe.client.guide.xmlformat;

import java.util.List;
import java.util.function.Supplier;

import org.jdom2.Element;

import com.google.common.base.Splitter;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class XmlElements
{
	private static final Splitter ON_COMMA = Splitter.on(',').trimResults();

	/**
	 * Gets an xml element as a String array
	 * @param xml the parent element
	 * @param childName the name of the child element
	 * @param fallback supplies a fallback value
	 * @return the value of the element as a String array
	 */
	public static List<String> asStringList(Element xml, String childName, Supplier<List<String>> fallback)
	{
		Element child = xml.getChild(childName);
		if (child == null)
			return fallback.get();
		return toStringList(child.getText());
	}

	/**
	 * Gets an xml element as a String array
	 * @param xml the parent element
	 * @param childName the name of the child element
	 * @throws
	 * InscribeSyntaxException if the child element does not exist
	 * @return the value of the element as a String array
	 */
	public static List<String> asStringList(Element xml, String childName) throws InscribeSyntaxException
	{
		return toStringList(getChild(xml, childName).getText());
	}

	public static Identifier asIdentifier(Element xml, String childName, Identifier fallback) throws InscribeSyntaxException
	{
		try
		{
			Element child = xml.getChild(childName);
			return child != null ? new Identifier(child.getText()) : fallback;
		}
		catch (InvalidIdentifierException e)
		{
			throw new InscribeSyntaxException(e.getMessage());
		}
	}

	/**
	 * Gets an xml element as an Identifier
	 * @param xml the parent element
	 * @param childName the name of the child element
	 * @throws
	 * InscribeSyntaxException if the child element does not exist
	 * @return the value of the element as an Identifier
	 */
	public static Identifier asIdentifier(Element xml, String childName) throws InscribeSyntaxException
	{
		try
		{
			return new Identifier(getChild(xml, childName).getText());
		}
		catch (InvalidIdentifierException e)
		{
			throw new InscribeSyntaxException(e.getMessage());
		}
	}

	/**
	 * Gets an xml element as a String
	 * @param xml the parent element
	 * @param childName the name of the child element
	 * @throws
	 * InscribeSyntaxException if the child element does not exist
	 * @return the value of the element as a String
	 */
	public static String asString(Element xml, String childName) throws InscribeSyntaxException
	{
		return getChild(xml, childName).getText();
	}

    /**
     * Gets an xml element as a Vector3f
     * @param xml the parent element
     * @param childName the name of the child element
     * @throws
     * InscribeSyntaxException if the child element does not exist or could not be parsed as a Vector3f
     * @return the value of the element as a Vector3f
     */
	public static Vector3f asVector3f(Element xml, String childName) throws InscribeSyntaxException
	{
	    Element child = xml.getChild(childName);
	    XmlAttributes.requireAttributes(xml, "x", "y", "z");
	    return new Vector3f
        (
            XmlAttributes.asFloat(child, "x", 0.0F),
            XmlAttributes.asFloat(child, "y", 0.0F),
            XmlAttributes.asFloat(child, "z", 0.0F)
        );
	}

    /**
     * Gets an xml element as a Vector3f
     * @param xml the parent element
     * @param childName the name of the child element
     * @throws
     * InscribeSyntaxException if the child element could not be parsed as a Vector3f
     * @return the value of the element as a Vector3f or {@code fallback} if it does not exist
     */
    public static Vector3f asVector3f(Element xml, String childName, Vector3f fallback) throws InscribeSyntaxException
    {
        Element child = xml.getChild(childName);
        if (child != null)
        {
            XmlAttributes.requireAttributes(child, "x", "y", "z");
            return new Vector3f
            (
                XmlAttributes.asFloat(child, "x", fallback.getX()),
                XmlAttributes.asFloat(child, "y", fallback.getY()),
                XmlAttributes.asFloat(child, "z", fallback.getZ())
            );
        }
        else
            return fallback;
    }

	/**
	 * @throws InscribeSyntaxException
	 * Gets a child element named {@code childName}.
	 * @param xml the parent element
	 * @param childName the name of the child element
	 * @throws
	 * InscribeSyntaxException if the child element does not exist
	 * @return the child element
	 * @throws
	 */
	public static Element getChild(Element xml, String childName) throws InscribeSyntaxException
	{
		Element child = xml.getChild(childName);
		if (child == null)
			throw noElementException(xml, childName);
		return child;
	}

	private static List<String> toStringList(String s)
	{
		return XmlElements.ON_COMMA.splitToList(s);
	}

	private static InscribeSyntaxException noElementException(Element xml, String childName)
	{
		return new InscribeSyntaxException(String.format(
				"No element named '%s' found for parent element of type %s", childName, xml.getQualifiedName()));
	}
}
