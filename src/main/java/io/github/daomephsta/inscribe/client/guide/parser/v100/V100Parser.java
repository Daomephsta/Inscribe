package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.Collections;
import java.util.List;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.Parser;
import io.github.daomephsta.inscribe.client.guide.xmlformat.*;
import io.github.daomephsta.inscribe.client.guide.xmlformat.SubtypeDeserialiser.Impl;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideDefinition;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.client.guide.xmlformat.theme.Theme;
import net.minecraft.util.Identifier;

public class V100Parser implements Parser
{
	public static final Parser INSTANCE = new V100Parser();
	private static final SubtypeDeserialiser<GuideAccessMethod> GUIDE_ACCESS_METHOD_DESERIALISER = new Impl<>(GuideAccessMethod.class)
				.registerDeserialiser(V100ElementTypes.NO_GUIDE_ACCESS_METHOD)
				.registerDeserialiser(V100ElementTypes.GUIDE_ITEM_ACCESS_METHOD);
	private static final ContentDeserialiser ENTRY_DESERIALISER = new ContentDeserialiser.Impl()
			.registerDeserialiser(V100ElementTypes.PARAGRAPH)
			.registerDeserialiser(V100ElementTypes.ITALICS)
			.registerDeserialiser(V100ElementTypes.EMPHASIS)
			.registerDeserialiser(V100ElementTypes.BOLD)
			.registerDeserialiser(V100ElementTypes.STRONG)
			.registerDeserialiser(V100ElementTypes.DEL)
			.registerDeserialiser(V100ElementTypes.LINE_BREAK)
			.registerDeserialiser(V100ElementTypes.WEB_LINK)
			.registerDeserialiser(V100ElementTypes.ENTRY_LINK)
			.registerDeserialiser(V100ElementTypes.ANCHOR)
			.registerDeserialiser(V100ElementTypes.IMAGE);

	private V100Parser() {}

	@Override
	public GuideDefinition loadGuideDefinition(Element xml) throws GuideLoadingException
	{
		Identifier guideId = XmlElements.asIdentifier(xml, "id");
		GuideAccessMethod guideAccess = GUIDE_ACCESS_METHOD_DESERIALISER.deserialise(xml.getChild("access_method"));
		Element themeXml = xml.getChild("theme");
		Theme theme = themeXml != null ? Theme.fromXml(themeXml) : Theme.DEFAULT;
		return new GuideDefinition(guideId, guideAccess, theme);
	}

	@Override
	public XmlEntry loadEntry(Element root) throws GuideLoadingException
	{
		String title = root.getChildText("title");
	    Identifier icon = new Identifier(root.getChildText("icon")),
	    		   category = new Identifier(root.getChildText("category"));
		List<Object> content = ENTRY_DESERIALISER.deserialise(root.getContent());
		List<String> tags = XmlElements.asStringList(root, "tags", () -> Collections.emptyList());

		return new XmlEntry(title, icon, category, tags, content);
	}
}
