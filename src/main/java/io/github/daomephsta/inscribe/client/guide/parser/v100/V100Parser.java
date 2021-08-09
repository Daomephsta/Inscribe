package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pivovarit.function.ThrowingFunction;

import io.github.daomephsta.inscribe.api.GuideFlags;
import io.github.daomephsta.inscribe.client.guide.GuideIdentifier;
import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.LinkStyle;
import io.github.daomephsta.inscribe.client.guide.gui.RenderFormatConverter;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.FormattedTextNode;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextNode;
import io.github.daomephsta.inscribe.client.guide.parser.FormatFlags;
import io.github.daomephsta.inscribe.client.guide.parser.Parser;
import io.github.daomephsta.inscribe.client.guide.xmlformat.ContentDeserialiser;
import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.SubtypeDeserialiser;
import io.github.daomephsta.inscribe.client.guide.xmlformat.SubtypeDeserialiser.Impl;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XPaths;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlElements;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlGuideGuiElement;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideDefinition;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.TableOfContents;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.TableOfContents.Link;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlPage;
import io.github.daomephsta.inscribe.client.guide.xmlformat.theme.Theme;
import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.inscribe.common.util.Identifiers;
import io.github.daomephsta.inscribe.common.util.messaging.Notifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class V100Parser implements Parser
{
    public static final Parser INSTANCE = new V100Parser();
    private static final Logger LOGGER = Inscribe.getDedicatedLogger("parser.v100");
    private static final SubtypeDeserialiser<GuideAccessMethod> GUIDE_ACCESS_METHOD_DESERIALISER = new Impl<>(GuideAccessMethod.class)
                .registerDeserialiser(V100ElementTypes.NO_GUIDE_ACCESS_METHOD)
                .registerDeserialiser(V100ElementTypes.GUIDE_ITEM_ACCESS_METHOD);
    private static final SubtypeDeserialiser<XmlGuideGuiElement> GUIDE_GUI_ELEMENT_DESERIALISER = new Impl<>(XmlGuideGuiElement.class)
        .registerDeserialiser(V100ElementTypes.IMAGE)
        .registerDeserialiser(V100ElementTypes.ITEMSTACK)
        .registerDeserialiser(V100ElementTypes.ENTITY_DISPLAY);
    public static final ContentDeserialiser ENTRY_DESERIALISER = new ContentDeserialiser.Impl()
        .registerDeserialisers(V100ElementTypes.IMAGE, V100ElementTypes.ITEMSTACK, V100ElementTypes.ENTITY_DISPLAY,
            V100ElementTypes.RECIPE_DISPLAY, V100ElementTypes.BUTTON, V100ElementTypes.IF_ELSE)
        .registerDeserialisers(V100ElementTypes.HEADINGS);

    @Override
    public GuideDefinition loadGuideDefinition(Document doc, ResourceManager resourceManager, GuideIdentifier filePath) throws GuideLoadingException
    {
        Element root = doc.getDocumentElement();
        Identifier guideId = filePath.getGuideId();
        Identifier mainTocPath = Identifiers.working(guideId)
            .addPathSegment(XmlAttributes.getValue(
                XmlElements.getChild(root, "main_table_of_contents"), "location"))
            .editPathSegment(-1, FilenameUtils::removeExtension)
            .toIdentifier();
        try
        {
            GuideAccessMethod guideAccess = GUIDE_ACCESS_METHOD_DESERIALISER.deserialise(XmlElements.getChild(root, "access_method"));
            Element themeXml = XmlElements.getChildNullable(root, "theme");
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
    public TableOfContents loadTableOfContents(Document doc, Identifier id, GuideIdentifier filePath) throws GuideLoadingException
    {
        Element root = doc.getDocumentElement();
        String directory = Identifiers.working(id).subIdentifier(0, -2).toIdentifier().toString();
        LinkStyle style = XmlAttributes.asEnum(root, "link_style", LinkStyle::fromRepresentation);
        List<TableOfContents.Link> links = XPaths.streamElements(root, "./link")
            .map(ThrowingFunction.unchecked(e -> readLink(e, style, directory)))
            .toList();
        return new TableOfContents(id, filePath, links, XmlAttributes.asInt(root, "columns", 1));
    }

    private Link readLink(Element link, LinkStyle style, String directory) throws GuideLoadingException
    {
        String name = XmlAttributes.getValue(link, "name");
        String destination = XmlAttributes.getValue(link, "destination");
        Identifier destinationId = destination.startsWith("/")
            ? new Identifier(directory + destination)
            : new Identifier(destination);
        Consumer<GuideFlow> iconFactory = getIconFactory(style, link);
        Predicate<GuideFlags> visibilityPredicate = readVisibilityPredicate(link);
        return new TableOfContents.Link(iconFactory, name, destinationId, style, visibilityPredicate);
    }

    private Consumer<GuideFlow> getIconFactory(LinkStyle style, Element link) throws GuideLoadingException
    {
        if (!style.requiresIcon())
            return null;
        XmlGuideGuiElement icon = GUIDE_GUI_ELEMENT_DESERIALISER.deserialise(link);
        return output -> RenderFormatConverter.convert(output, icon);
    }

    private Predicate<GuideFlags> readVisibilityPredicate(Element link) throws InscribeSyntaxException
    {
        if (link.hasAttribute("if"))
        {
            Identifier flagId = XmlAttributes.asIdentifier(link, "if");
            return flags -> flags.isTrue(flagId);
        }
        else if (link.hasAttribute("if_not"))
        {
            Identifier flagId = XmlAttributes.asIdentifier(link, "if_not");
            return flags -> flags.isFalse(flagId);
        }
        else
            return flags -> true;
    }

    @Override
    public XmlEntry loadEntry(Document doc, ResourceManager resourceManager,
        Identifier id, GuideIdentifier filePath) throws GuideLoadingException
    {
        Element root = doc.getDocumentElement();
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
            pages.add(new XmlPage(ENTRY_DESERIALISER.deserialise(XPaths.nodes((Element) page, "./node()"))));
        }
        return pages;
    }

    static List<TextNode> parseContentAsText0(Element xml, List<TextNode> text, 
        Deque<FormatFlags> formatting, int colour) throws InscribeSyntaxException
    {
        for (int i = 0; i < xml.getChildNodes().getLength(); i++)
        {
            Node node = xml.getChildNodes().item(i);
            switch (node.getNodeType())
            {
            case Node.TEXT_NODE -> 
                text.add(new FormattedTextNode(node.getNodeValue(), MinecraftClient.DEFAULT_FONT_ID, 
                    colour, formatting.toArray(new FormatFlags[0])));
            case Node.ELEMENT_NODE ->
                {
                    Element element = (Element) node;
                    formatting.push(switch (element.getTagName())
                    {
                    case "b" -> FormatFlags.BOLD;
                    case "i" -> FormatFlags.ITALIC;
                    case "u" -> FormatFlags.UNDERLINE;
                    case "del" -> FormatFlags.STRIKETHROUGH;
                    default -> 
                        throw new InscribeSyntaxException("Unexpected tag " + element.getTagName());
                    });
                    parseContentAsText0(element, text, formatting, colour);
                }
            }
        }
        return text;
    }

    static List<TextNode> parseContentAsText(Element xml, int colour) throws InscribeSyntaxException
    {
        return V100Parser.parseContentAsText0(xml, new ArrayList<>(), new ArrayDeque<>(), colour);
    }
}
