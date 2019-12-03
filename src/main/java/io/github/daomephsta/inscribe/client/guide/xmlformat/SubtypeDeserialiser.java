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

/**
 * Deserialises a single child node of a node into a subtype of {@code T}.
 * @author Daomephsta
 *
 * @param <T> the common supertype all the registered deserialisers deserialise to
 */
public interface SubtypeDeserialiser<T extends IXmlRepresentation>
{
    /**
     * @param root a node with child nodes. The child nodes will be used
     * to determine the subtype that is returned.
     * @return a subtype of {@code parentType}, deserialised from the first matching child node
     * of {@code root}
     * @throws GuideLoadingException if any error occurs in the deserialiser,
     * a matching deserialiser
     */
    public T deserialise(Element root) throws GuideLoadingException;

    public static class Impl<T extends IXmlRepresentation> implements SubtypeDeserialiser<T>
    {
        private static final Logger LOGGER = LogManager.getLogger("inscribe.dedicated.subtype_deserialiser.default");
        private final Class<T> parentType;
        private final Map<String, XmlElementType<? extends T>> deserialisers = new HashMap<>();

        public Impl(Class<T> parentType)
        {
            this.parentType = parentType;
        }

        public Impl<T> registerDeserialiser(XmlElementType<? extends T> elementType)
        {
            deserialisers.put(elementType.getElementName(), elementType);
            return this;
        }

        /**{@inheritDoc}*/
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
