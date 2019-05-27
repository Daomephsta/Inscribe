package io.github.daomephsta.inscribe.client.guide.xmlformat;

import java.util.List;
import java.util.function.Supplier;

import org.jdom2.Element;

import com.google.common.base.Splitter;

import net.minecraft.util.Identifier;

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

	public static Identifier asIdentifier(Element xml, String childName, Identifier fallback)
	{
		Element child = xml.getChild(childName);
		return child != null ? new Identifier(child.getText()) : fallback;
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
		return new Identifier(getChild(xml, childName).getText());
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
