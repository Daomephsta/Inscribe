package io.github.daomephsta.inscribe.client.guide.xmlformat;

import java.util.List;
import java.util.function.Supplier;

import org.jdom2.Element;
import org.jdom2.Namespace;

import com.google.common.base.Splitter;

import net.minecraft.util.Identifier;

public class XmlElements
{
	private static final Splitter ON_COMMA = Splitter.on(',').trimResults();
	private final Namespace defaultNamespace;
	
	public XmlElements(Namespace defaultNamespace)
	{
		this.defaultNamespace = defaultNamespace;
	}

	/**
	 * Gets an xml element as a String array
	 * @param xml the parent element
	 * @param childName the name of the child element
	 * @param fallback supplies a fallback value
	 * @return the value of the element as a String array
	 */
	public List<String> asStringList(Element xml, String childName, Supplier<List<String>> fallback)
	{
		Element child = xml.getChild(childName, defaultNamespace);
		if (child == null) 
			return fallback.get();
		return toStringList(child.getText());
	}

	/**
	 * Gets an xml element as a String array
	 * @param xml the parent element
	 * @param childName the name of the child element
	 * @throws 
	 * XmlSyntaxException if the childElement does not exist
	 * @return the value of the element as a String array
	 */
	public List<String> asStringList(Element xml, String childName)
	{
		Element child = xml.getChild(childName, defaultNamespace);
		if (child == null) 
			throw noElementException(xml, childName);
		return toStringList(child.getText());
	}

	/**
	 * Gets an xml element as an Identifier
	 * @param xml the parent element
	 * @param childName the name of the child element
	 * @throws 
	 * XmlSyntaxException if the childElement does not exist
	 * @return the value of the element as an Identifier
	 */
	public Identifier asIdentifier(Element xml, String childName)
	{
		Element child = xml.getChild(childName, defaultNamespace);
		if (child == null) 
			throw noElementException(xml, childName);
		return new Identifier(child.getText());
	}

	private List<String> toStringList(String s)
	{
		return XmlElements.ON_COMMA.splitToList(s);
	}

	private XmlSyntaxException noElementException(Element xml, String childName)
	{
		return new XmlSyntaxException(String.format("No element named '%s' found for parent element of type %s", childName, xml.getQualifiedName()));
	}
}
