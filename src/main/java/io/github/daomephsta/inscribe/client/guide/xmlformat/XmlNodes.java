package io.github.daomephsta.inscribe.client.guide.xmlformat;

import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        NodeList children = node.getChildNodes();
        return IntStream.range(0, children.getLength()).mapToObj(children::item);
    }
    
    public static <T extends Node> Stream<T> streamChildren(Node node, Class<T> tClass)
    {
        NodeList children = node.getChildNodes();
        return IntStream.range(0, children.getLength())
            .mapToObj(children::item)
            .filter(tClass::isInstance)
            .map(tClass::cast);
    }
}
