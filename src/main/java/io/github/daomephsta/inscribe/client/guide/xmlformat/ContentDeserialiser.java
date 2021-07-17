package io.github.daomephsta.inscribe.client.guide.xmlformat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.commonmark.ext.ins.InsExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.inscribe.common.util.Unindenter;

/**
 * Deserialises collections of XML nodes.
 * Primarily used to deserialise child nodes.
 * @author Daomephsta
 */
public interface ContentDeserialiser
{
    /**
     * Deserialises a list of nodes. Tag names not registered with this deserialiser
     * are ignored.
     * @param list the nodes to be deserialised
     * @return a list of the nodes in deserialised form, in encounter order
     * @throws GuideLoadingException if any error occurs during deserialisation
     */
    public List<Object> deserialise(NodeList list) throws GuideLoadingException;

    public static class Impl implements ContentDeserialiser
    {
        private static final Parser MARKDOWN_PARSER = Parser.builder()
            .extensions(Lists.newArrayList(InsExtension.create()))
            .build();
        private static final Logger LOGGER = Inscribe.getDedicatedLogger("content_deserialiser.default");
        private static final Unindenter UNINDENTER = new Unindenter();
        private final Map<String, XmlElementType<?>> deserialisers = new HashMap<>();

        public Impl registerDeserialisers(XmlElementType<?>... elementTypes)
        {
            for (var type : elementTypes)
                registerDeserialiser(type);
            return this;
        }
        
        public Impl registerDeserialiser(XmlElementType<?> elementType)
        {
            deserialisers.put(elementType.getElementName(), elementType);
            return this;
        }

        /**@inheritDoc*/
        @Override
        public List<Object> deserialise(NodeList list) throws GuideLoadingException
        {
            List<Object> result = new ArrayList<>();
            for (int i = 0; i < list.getLength(); i++)
            {
                org.w3c.dom.Node node = list.item(i);
                switch (node.getNodeType()) 
                {
                    case org.w3c.dom.Node.ELEMENT_NODE ->
                    {
                        Element element = (Element) node;
                        XmlElementType<?> deserialiser = deserialisers.get(element.getTagName());
                        if (deserialiser == null) 
                        {
                            LOGGER.debug("Ignored unknown element {}", element);
                            continue;
                        } 
                        else
                            result.add(deserialiser.fromXml(element));
                    }
                    case org.w3c.dom.Node.TEXT_NODE ->
                            result.add(parseMarkDown(node.getTextContent()));
                    default ->
                            LOGGER.debug("Ignored {} as it is not text or an element", node);
                }
            }
            return result;
        }

        private static Node parseMarkDown(String markDown)
        {
            return MARKDOWN_PARSER.parse(UNINDENTER.unindent(markDown));
        }
    }
}
