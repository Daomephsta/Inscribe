package io.github.daomephsta.inscribe.client.guide.xmlformat;

import java.io.StringWriter;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

import com.pivovarit.function.ThrowingBinaryOperator;

public class XmlNodes
{
    public static ProcessingInstruction getProcessingInstruction(Node node, String target) throws InscribeSyntaxException
    {
        return streamChildren(node, ProcessingInstruction.class)
            .filter(insn -> insn.getTarget().equals(target))
            .reduce(ThrowingBinaryOperator.unchecked((a, b) -> 
            {
                throw new InscribeSyntaxException(node + " contains more than one processing instruction " + target);
            }))
            .orElseThrow(() -> new InscribeSyntaxException(node + " is missing processing instruction " + target));
    }

    public static Iterable<Node> iterate(NodeList nodes)
    {
        return stream(nodes)::iterator;
    }

    public static <T extends Node> Iterable<T> iterate(NodeList nodes, Class<T> tClass)
    {
        return stream(nodes, tClass)::iterator;
    }
    
    public static Iterable<Node> iterateChildren(Node node)
    {
        return streamChildren(node)::iterator;
    }
    
    public static <T extends Node> Iterable<T> iterateChildren(Node node, Class<T> tClass)
    {
        return streamChildren(node, tClass)::iterator;
    }

    public static Stream<Node> streamChildren(Node node)
    {
        return stream(node.getChildNodes());
    }
    
    public static <T extends Node> Stream<T> streamChildren(Node node, Class<T> tClass)
    {
        return stream(node.getChildNodes(), tClass);
    }

    public static Stream<Node> stream(NodeList nodes)
    {
        return IntStream.range(0, nodes.getLength())
            .mapToObj(nodes::item);
    }

    public static <T extends Node> Stream<T> stream(NodeList nodes, Class<T> tClass)
    {
        return stream(nodes)
            .filter(tClass::isInstance)
            .map(tClass::cast);
    }

    public static void replaceChild(Node child, Iterable<Node> replacements)
    {
        for (Node substitute : replacements)
        {
            child.getOwnerDocument().adoptNode(substitute);
            child.getParentNode().insertBefore(substitute, child);
        }
        child.getParentNode().removeChild(child);
    }

    public static String toString(Node node)
    {
        try
        {
            Transformer out = TransformerFactory.newInstance().newTransformer();
            out.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter s = new StringWriter();
            out.transform(new DOMSource(node), new StreamResult(s));
            return s.toString();
        }
        catch (TransformerException | TransformerFactoryConfigurationError e)
        {
            throw new RuntimeException(e);
        }
    }
}
