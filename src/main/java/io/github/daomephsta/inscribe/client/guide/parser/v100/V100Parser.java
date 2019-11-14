package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.Collections;
import java.util.List;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.google.common.collect.Lists;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.LinkStyle;
import io.github.daomephsta.inscribe.client.guide.parser.Parser;
import io.github.daomephsta.inscribe.client.guide.xmlformat.ContentDeserialiser;
import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.SubtypeDeserialiser;
import io.github.daomephsta.inscribe.client.guide.xmlformat.SubtypeDeserialiser.Impl;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlElements;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlGuideGuiElement;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideDefinition;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.TableOfContents;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.client.guide.xmlformat.theme.Theme;
import io.github.daomephsta.util.Identifiers;
import io.github.daomephsta.util.XmlResources;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class V100Parser implements Parser
{
	public static final Parser INSTANCE = new V100Parser();
	private static final SubtypeDeserialiser<GuideAccessMethod> GUIDE_ACCESS_METHOD_DESERIALISER = new Impl<>(GuideAccessMethod.class)
				.registerDeserialiser(V100ElementTypes.NO_GUIDE_ACCESS_METHOD)
				.registerDeserialiser(V100ElementTypes.GUIDE_ITEM_ACCESS_METHOD);
	private static final SubtypeDeserialiser<XmlGuideGuiElement> GUIDE_GUI_ELEMENT_DESERIALISER = new Impl<>(XmlGuideGuiElement.class)
        .registerDeserialiser(V100ElementTypes.IMAGE)
	    .registerDeserialiser(V100ElementTypes.ITEMSTACK);
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
	public GuideDefinition loadGuideDefinition(Element xml, ResourceManager resourceManager, Identifier path) throws GuideLoadingException
	{
	    //Remove "inscribe_guides" from the start and "guide_definition.xml" from the end
		Identifier guideId = Identifiers.builder(path).subPath(1, -2).build();
        Identifier mainTocPath = Identifiers.builder(XmlAttributes.asIdentifier(XmlElements.getChild(xml, "main_table_of_contents"), "location"))
            .namespace(guideId.getNamespace())
            .prefixPath(guideId.getPath())
            .prefixPath(GuideManager.FOLDER_NAME)
            .build();
        try
        {
            TableOfContents mainTableOfContents = loadTableOfContents(resourceManager, mainTocPath);
            GuideAccessMethod guideAccess = GUIDE_ACCESS_METHOD_DESERIALISER.deserialise(xml.getChild("access_method"));
            Element themeXml = xml.getChild("theme");
            Theme theme = themeXml != null ? Theme.fromXml(themeXml) : Theme.DEFAULT;
            return new GuideDefinition(guideId, guideAccess, mainTableOfContents, theme);
        }
        catch (GuideLoadingException e)
        {
            GuideManager.INSTANCE.handleGuideLoadingException(e, mainTocPath.toString());
        }
        return GuideDefinition.FALLBACK;
    }

    private TableOfContents loadTableOfContents(ResourceManager resourceManager, Identifier tableOfContentsPath) throws GuideLoadingException
    {
        if (!resourceManager.containsResource(tableOfContentsPath))
            throw new InscribeSyntaxException("Could not find table of contents at " + tableOfContentsPath);
        Element tableOfContents = XmlResources.readDocument(new SAXBuilder(), resourceManager, tableOfContentsPath).getRootElement();
        LinkStyle style = XmlAttributes.asEnum(tableOfContents, "link_style", LinkStyle::fromRepresentation);

        List<Element> linkElements = tableOfContents.getChildren("link");
        List<TableOfContents.Link> links = Lists.newArrayListWithCapacity(linkElements.size());
        for (Element link : linkElements)
        {
            Element iconXml = link.getChild("icon");
            if (style.requiresIcon() && iconXml == null)
                throw new InscribeSyntaxException("Style " + style + " requires that an icon is specified");
            XmlGuideGuiElement icon = GUIDE_GUI_ELEMENT_DESERIALISER.deserialise(iconXml);
            String name = XmlAttributes.getValue(link, "name");
            Identifier destination = XmlAttributes.asIdentifier(link, "destination");
            links.add(new TableOfContents.Link(() -> RenderFormatConverter.convert(icon), name, destination, style));
        }

        return new TableOfContents(links);
    }

	@Override
	public XmlEntry loadEntry(Element root, ResourceManager resourceManager, Identifier path) throws GuideLoadingException
	{
		String title = root.getChildText("title");
	    Identifier icon = new Identifier(root.getChildText("icon")),
	    		   category = new Identifier(root.getChildText("category"));
		List<Object> content = ENTRY_DESERIALISER.deserialise(root.getContent());
		List<String> tags = XmlElements.asStringList(root, "tags", () -> Collections.emptyList());
		return new XmlEntry(title, icon, category, tags, content);
	}
}
