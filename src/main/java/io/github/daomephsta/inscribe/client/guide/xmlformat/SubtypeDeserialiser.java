package io.github.daomephsta.inscribe.client.guide.xmlformat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;

public interface SubtypeDeserialiser<T extends IXmlRepresentation>
{
    public T deserialise(Element root) throws GuideLoadingException;

    public class Impl<T extends IXmlRepresentation> implements SubtypeDeserialiser<T>
    {
        private final Class<T> parentType;
        private final Map<String, XmlElementType<? extends T>> deserialisers = new HashMap<>();
        private static final Logger LOGGER = LogManager.getLogger("inscribe.dedicated.subtype_deserialiser.default");

        public Impl(Class<T> parentType)
        {
            this.parentType = parentType;
        }

        public Impl<T> registerDeserialiser(XmlElementType<? extends T> elementType)
        {
            deserialisers.put(elementType.getElementName(), elementType);
            return this;
        }

        @Override
        public T deserialise(Element root) throws GuideLoadingException
        {
            Object subtype = null;
            for (Entry<String, XmlElementType<? extends T>> deserialiser : deserialisers.entrySet())
            {
                Element element = XmlElements.getChildNullable(root, deserialiser.getKey());
                if (element == null)
                    continue;
                T deserialised = deserialiser.getValue().fromXml(element);
                if (parentType.isInstance(deserialised) && subtype == null)
                    subtype = deserialised;
                else
                {
                    throw new InscribeSyntaxException(
                            String.format("Cannot parse %s as an instance of %s", element, parentType.getName()));
                }
            }
            if (subtype == null)
            {
                throw new InscribeSyntaxException(String.format("Expected %s to have a child element %s",
                                root,
                                deserialisers.keySet().stream()
                                    .map(s -> "'" + s + "'")
                                    .collect(Collectors.joining(" or "))));
            }
            return parentType.cast(subtype);
        }
    }
}
