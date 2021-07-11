package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;

import io.github.daomephsta.inscribe.client.guide.GuideIdentifier;
import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.LinkStyle;
import io.github.daomephsta.inscribe.client.guide.gui.RenderFormatConverter;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.parser.Parser;
import io.github.daomephsta.inscribe.client.guide.xmlformat.ContentDeserialiser;
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
import io.github.daomephsta.inscribe.common.util.Identifiers;
import io.github.daomephsta.inscribe.common.util.messaging.Notifier;
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
        .registerDeserialiser(V100ElementTypes.ITEMSTACK)
        .registerDeserialiser(V100ElementTypes.ENTITY_DISPLAY);
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
            .registerDeserialiser(V100ElementTypes.ITEMSTACK)
            .registerDeserialiser(V100ElementTypes.ENTITY_DISPLAY);

    @Override
    public GuideDefinition loadGuideDefinition(Element xml, ResourceManager resourceManager, GuideIdentifier filePath) throws GuideLoadingException
    {
        Identifier guideId = filePath.getGuideId();
        Identifier mainTocPath = Identifiers.working(guideId)
            .addPathSegment(XmlAttributes.getValue(
                XmlElements.getChild(xml, "main_table_of_contents"), "location"))
            .editPathSegment(-1, FilenameUtils::removeExtension)
            .toIdentifier();
        try
        {
            GuideAccessMethod guideAccess = GUIDE_ACCESS_METHOD_DESERIALISER.deserialise(XmlElements.getChild(xml, "access_method"));
            Element themeXml = XmlElements.getChildNullable(xml, "theme");
            Theme theme = themeXml != null ? Theme.fromXml(themeXml) : Theme.DEFAULT;
            return new GuideDefinition(guideId, guideAccess, mainTocPath, theme);
        }
        catch (GuideLoadingException loadingException)
        {
            if (loadingException.isFatal())
                throw new RuntimeException("An unrecoverable error occured while loading guide definition '" + filePath + "'", loadingException);
            else
            {
                LOGGER.error("Guide definition at {} failed to load correctly:\n\t{}", filePath, loadingException.getMessage());
                Notifier.DEFAULT.notify(new TranslatableText(Inscribe.MOD_ID + ".chat_message.load_failure.guide_definition"));
            }
        }
        return GuideDefinition.FALLBACK;
    }

    @Override
    public TableOfContents loadTableOfContents(Element root, Identifier id, GuideIdentifier filePath) throws GuideLoadingException
    {
        String directory = Identifiers.working(id).subIdentifier(0, -2).toIdentifier().toString();
        LinkStyle style = XmlAttributes.asEnum(root, "link_style", LinkStyle::fromRepresentation);        
        NodeList linkElements = root.getElementsByTagName("link");
        List<TableOfContents.Link> links = Lists.newArrayListWithCapacity(linkElements.getLength());
        for (int i = 0; i < linkElements.getLength(); i++)
        {
            Element link = (Element) linkElements.item(i);
            String name = XmlAttributes.getValue(link, "name");
            String destination = XmlAttributes.getValue(link, "destination");
            Identifier destinationId = destination.startsWith("/")
                ? new Identifier(directory + destination)
                : new Identifier(destination);
            links.add(new TableOfContents.Link(getIconFactory(style, link), name, destinationId, style));
        }
        return new TableOfContents(id, filePath, links, XmlAttributes.asInt(root, "columns", 1));
    }

    private Consumer<GuideFlow> getIconFactory(LinkStyle style, Element link) throws GuideLoadingException
    {
        if (!style.requiresIcon())
            return null;
        XmlGuideGuiElement icon = GUIDE_GUI_ELEMENT_DESERIALISER.deserialise(link);
        return output -> RenderFormatConverter.convert(output, icon);
    }

    @Override
    public XmlEntry loadEntry(Element root, ResourceManager resourceManager,
        Identifier id, GuideIdentifier filePath) throws GuideLoadingException
    {
        List<String> tags = XmlAttributes.asStringList(root, "tags", Collections::emptyList);
        List<XmlPage> pages = readPages(root, id);
        return new XmlEntry(id, filePath, new HashSet<>(tags), pages);
    }

    private List<XmlPage> readPages(Element root, Identifier entryId) throws GuideLoadingException
    {
        List<XmlPage> pages = new ArrayList<>();
        NodeList pageElements = root.getElementsByTagName("page");
        if (pageElements.getLength() == 0)
            LOGGER.warn("Entry '{}' has no pages", entryId);
        for (int i = 0; i < pageElements.getLength(); i++)
        {
            Node page = pageElements.item(i);
            pages.add(new XmlPage(ENTRY_DESERIALISER.deserialise(page.getChildNodes())));
        }
        return pages;
    }
}
