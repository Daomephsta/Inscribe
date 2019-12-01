package io.github.daomephsta.inscribe.client.guide.xmlformat;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.jdom2.*;
import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.util.Unindenter;

public interface ContentDeserialiser
{
    public List<Object> deserialise(List<Content> list) throws GuideLoadingException;

    public static class Impl implements ContentDeserialiser
    {
        private static final Parser MARKDOWN_PARSER = Parser.builder().build();
        private static final Logger LOGGER = LogManager.getLogger("inscribe.dedicated.content_deserialiser.default");
        private static final Unindenter UNINDENTER = new Unindenter();
        private final Map<String, XmlElementType<?>> deserialisers = new HashMap<>();

        public Impl registerDeserialiser(XmlElementType<?> elementType)
        {
            deserialisers.put(elementType.getElementName(), elementType);
            return this;
        }

        @Override
        public List<Object> deserialise(List<Content> list) throws GuideLoadingException
        {
            List<Object> result = new ArrayList<>();
            for (Content content : list)
            {
                switch (content.getCType())
                {
                case Element:
                    Element element = (Element) content;
                    XmlElementType<?> deserialiser = deserialisers.get(element.getName());
                    if (deserialiser == null)
                    {
                        LOGGER.debug("Ignored unknown element {}", element);
                        continue;
                    }
                    else
                        result.add(deserialiser.fromXml(element));
                    break;
                case Text:
                    result.add(parseMarkDown(content.getValue()));
                    break;
                default:
                    LOGGER.debug("Ignored {} as it is not text or an element", content);
                    break;
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
