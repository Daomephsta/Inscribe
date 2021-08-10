package io.github.daomephsta.inscribe.client.guide.xmlformat;

import java.util.stream.Stream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathNodes;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.google.common.collect.Streams;

public class XPaths
{
    private static final XPath XPATH = XPathFactory.newInstance().newXPath();

    public static Node node(Node source, String xpath)
    {
        try
        {
            return XPATH.evaluateExpression(xpath, source, Node.class);
        }
        catch (XPathExpressionException e)
        {
            throw new IllegalArgumentException(xpath);
        }
    }

    public static XPathNodes nodes(Node source, String xpath)
    {
        try
        {
            return XPATH.evaluateExpression(xpath, source, XPathNodes.class);
        }
        catch (XPathExpressionException e)
        {
            throw new IllegalArgumentException(xpath);
        }
    }

    public static Stream<Node> streamNodes(Node source, String xpath)
    {
        return Streams.stream(nodes(source, xpath));
    }

    public static Stream<Element> streamElements(Element source, String xpath)
    {
        return streamNodes(source, xpath).map(node ->
        {
            if (node instanceof Element element)
                return element;
            else
                throw new IllegalArgumentException("Non-element child " + node);
        });
    }
}
