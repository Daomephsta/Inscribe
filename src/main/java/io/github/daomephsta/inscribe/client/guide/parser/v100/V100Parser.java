package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.google.common.collect.Lists;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.LinkStyle;
import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
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
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlPage;
import io.github.daomephsta.inscribe.client.guide.xmlformat.theme.Theme;
import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.inscribe.common.util.messaging.Notifier;
import io.github.daomephsta.util.Identifiers;
import io.github.daomephsta.util.XmlResources;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class V100Parser implements Parser
{
	public static final Parser INSTANCE = new V100Parser();
    private static final Logger LOGGER = LogManager.getLogger("inscribe.dedicated.parserV100");
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
			.registerDeserialiser(V100ElementTypes.IMAGE)
	        .registerDeserialiser(V100ElementTypes.ITEMSTACK);

	private V100Parser() {}

	@Override
	public GuideDefinition loadGuideDefinition(Element xml, ResourceManager resourceManager, Identifier path) throws GuideLoadingException
	{
	    //Remove "inscribe_guides" from the start and "guide_definition.xml" from the end
		Identifier guideId = Identifiers.builder(path).subPath(1, -2).build();
        Identifier mainTocPath = Identifiers.builder(XmlAttributes.asIdentifier(XmlElements.getChild(xml, "main_table_of_contents"), "location"))
            .namespace(guideId.getNamespace())
            .prependPathSegments(guideId.getPath())
            .prependPathSegments(GuideManager.FOLDER_NAME)
            .build();
        try
        {
            TableOfContents mainTableOfContents = loadTableOfContents(resourceManager, mainTocPath);
            GuideAccessMethod guideAccess = GUIDE_ACCESS_METHOD_DESERIALISER.deserialise(xml.getChild("access_method"));
            Element themeXml = xml.getChild("theme");
            Theme theme = themeXml != null ? Theme.fromXml(themeXml) : Theme.DEFAULT;
            return new GuideDefinition(guideId, guideAccess, mainTableOfContents, theme);
        }
        catch (GuideLoadingException loadingException)
        {
            if (loadingException.isFatal())
                throw new RuntimeException("An unrecoverable error occured while loading guide definition '" + path + "'", loadingException);
            else
            {
                LOGGER.error("Guide definition at {} failed to load correctly:\n\t{}", path, loadingException.getMessage());
                Notifier.DEFAULT.notify(new TranslatableText(Inscribe.MOD_ID + ".chat_message.load_failure.guide_definition"));
            }
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
            if (style.requiresIcon() && iconXml != null)
                throw new InscribeSyntaxException("Style " + style + " requires that an icon is specified");
            String name = XmlAttributes.getValue(link, "name");
            Identifier destination = XmlAttributes.asIdentifier(link, "destination");
            Supplier<GuideWidget> iconFactory = null;
            if (iconXml != null)
            {
                XmlGuideGuiElement icon = GUIDE_GUI_ELEMENT_DESERIALISER.deserialise(iconXml);
                iconFactory = () -> RenderFormatConverter.convert(icon );
            }
            links.add(new TableOfContents.Link(iconFactory, name, destination, style));
        }

        return new TableOfContents(links);
    }

	@Override
	public XmlEntry loadEntry(Element root, ResourceManager resourceManager, Identifier id) throws GuideLoadingException
	{
        List<String> tags = XmlElements.asStringList(root, "tags", () -> Collections.emptyList());
        List<XmlPage> pages = readPages(root);
		return new XmlEntry(id, tags, pages);
	}

    private List<XmlPage> readPages(Element root) throws GuideLoadingException
    {
        List<XmlPage> pages = new ArrayList<>();
        for (Element page : root.getChildren("page"))
            pages.add(new XmlPage(ENTRY_DESERIALISER.deserialise(page.getContent())));
        return pages;
    }
}
