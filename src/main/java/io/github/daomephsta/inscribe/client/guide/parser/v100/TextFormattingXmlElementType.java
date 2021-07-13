package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.List;
import java.util.function.Function;

import org.w3c.dom.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.XmlMixedContent;

public class TextFormattingXmlElementType<T extends XmlMixedContent> extends XmlElementType<T>
{
    private final Function<List<Object>, T> constructorHandle;

    public TextFormattingXmlElementType(String elementName, Class<T> clazz, Function<List<Object>, T> constructorHandle)
    {
        super(elementName, clazz);
        this.constructorHandle = constructorHandle;
    }

    @Override
    public T fromXml(Element xml) throws GuideLoadingException
    {
        return constructorHandle.apply(V100ElementTypes.TEXT_FORMATTING.deserialise(xml.getChildNodes()));
    }
}
