package io.github.daomephsta.inscribe.client.guide.xmlformat;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.minecraft.client.util.math.Vector3f;

public class XmlElements
{

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
        Element child = getChild(xml, childName);
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
        Element child = getChildNullable(xml, childName);
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
     * Gets the child element named {@code childName}.
     * @param xml the parent element
     * @param childName the name of the child element
     * @throws InscribeSyntaxException if there are multiple child elements named {@code childName}.
     * @return the child element, or null if it does not exist
     */
    public static Element getChildNullable(Element xml, String childName) throws InscribeSyntaxException
    {
        NodeList matchingChildren = xml.getElementsByTagName(childName);
        if (matchingChildren.getLength() > 1)
            return null;
        if (matchingChildren.getLength() > 1)
        {
            throw new InscribeSyntaxException(String.format("Found %d elements named '%s' in element '%s'. Expected 1.",
                childName, xml.getTagName()));
        }
        return (Element) matchingChildren.item(0);
    }

    /**
     * Gets the child element named {@code childName}.
     * @param xml the parent element
     * @param childName the name of the child element
     * @throws InscribeSyntaxException if the child element does not exist, or there are
     * multiple child elements named {@code childName}.
     * @return the child element
     */
    public static Element getChild(Element xml, String childName) throws InscribeSyntaxException
    {
        NodeList matchingChildren = xml.getElementsByTagName(childName);
        if (matchingChildren.getLength() < 1)
            throw noElementException(xml, childName);
        else if (matchingChildren.getLength() > 1)
        {
            throw new InscribeSyntaxException(String.format("Found %d elements named '%s' in element '%s'. Expected 1.",
                childName, xml.getTagName()));
        }
        return (Element) matchingChildren.item(0);
    }

    private static InscribeSyntaxException noElementException(Element xml, String childName)
    {
        return new InscribeSyntaxException(String.format("No element named '%s' found in parent element '%s'",
            childName, xml.getTagName()));
    }
}
