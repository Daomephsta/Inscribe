package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.List;
import java.util.function.Function;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.ContentDeserialiser;
import io.github.daomephsta.inscribe.client.guide.xmlformat.ContentDeserialiser.Impl;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.XmlMixedContent;
import net.minecraft.util.Lazy;

public class TextFormattingXmlElementType<T extends XmlMixedContent> extends XmlElementType<T>
{
	private final Function<List<Object>, T> constructorHandle;
	private final Lazy<ContentDeserialiser> contentDeserialiser;
	
	public TextFormattingXmlElementType(String elementName, Class<T> clazz, Function<List<Object>, T> constructorHandle)
	{
		super(elementName, clazz);
		this.constructorHandle = constructorHandle;
		this.contentDeserialiser = new Lazy<>
		(() ->
			new Impl()
				.registerDeserialiser(V100ElementTypes.BOLD)
				.registerDeserialiser(V100ElementTypes.STRONG)
				.registerDeserialiser(V100ElementTypes.EMPHASIS)
				.registerDeserialiser(V100ElementTypes.ITALICS)
				.registerDeserialiser(V100ElementTypes.DEL)
		);
	}
	
	@Override
	public T fromXml(Element xml)
	{
		return constructorHandle.apply(contentDeserialiser.get().deserialise(xml.getContent()));
	}
}
