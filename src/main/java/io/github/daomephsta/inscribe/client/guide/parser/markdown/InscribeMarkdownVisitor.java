package io.github.daomephsta.inscribe.client.guide.parser.markdown;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.commonmark.ext.ins.Ins;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BulletList;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Document;
import org.commonmark.node.Emphasis;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;

import com.google.common.collect.Sets;

import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.LabelWidget;
import io.github.daomephsta.inscribe.client.guide.parser.FormatFlags;
import io.github.daomephsta.inscribe.client.guide.parser.markdown.ListData.ListType;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class InscribeMarkdownVisitor extends AbstractVisitor
{
    private static final Logger LOGGER = LogManager.getLogger("inscribe.dedicated.markdown");
    private final PageBuilder builder;

    public InscribeMarkdownVisitor(GuideFlow output)
    {
        this.builder = new PageBuilder(output);
    }

    @Override
    public void visit(Document document)
    {
        visitChildren(document);
    }

    @Override
    public void visit(Heading heading)
    {
        visitChildren(heading);
        builder.addLabel(Alignment.LEADING, Alignment.CENTER, (7 - heading.getLevel()) / 6.0F * LabelWidget.MAX_SCALE);
    }

    @Override
    public void visit(Paragraph paragraph)
    {
        visitChildren(paragraph);
        builder.addTextBlock(Alignment.LEADING, Alignment.CENTER, 0x000000);
    }

    @Override
    public void visit(SoftLineBreak softLineBreak)
    {
        visitChildren(softLineBreak);
    }

    @Override
    public void visit(HardLineBreak hardLineBreak)
    {
        builder.addHardLineBreak();
        visitChildren(hardLineBreak);
    }

    @Override
    public void visit(Emphasis emphasis)
    {
        visitFormatNode(emphasis, FormatFlags.ITALIC);
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis)
    {
        visitFormatNode(strongEmphasis, FormatFlags.BOLD);
    }

    private void visit(Ins ins)
    {
        visitFormatNode(ins, FormatFlags.UNDERLINE);
    }

    private void visitFormatNode(Node node, FormatFlags represented)
    {
        builder.pushFormatting(represented);
        visitChildren(node);
        builder.popFormatting();
    }

    @Override
    public void visit(Text text)
    {
        builder.pushLiteral(text.getLiteral());
        visitChildren(text);
    }

    @Override
    public void visit(BulletList bulletList)
    {
        builder.startList(ListType.UNORDERED);
        visitChildren(bulletList);
        builder.endList();
    }

    @Override
    public void visit(OrderedList orderedList)
    {
        builder.startList(ListType.ORDERED);
        visitChildren(orderedList);
        builder.endList();
    }

    @Override
    public void visit(ListItem listItem)
    {
        visitChildren(listItem);
        builder.nextListItem();
    }

    @Override
    public void visit(ThematicBreak thematicBreak)
    {
        builder.addHorizontalRule();
        super.visit(thematicBreak);
    }

    private static final Set<String> ALLOWED_SCHEMES = Sets.newHashSet("http", "https");
    @Override
    public void visit(Link link)
    {
        String destination = link.getDestination();
        try
        {
            URI uri = new URI(destination);
            String scheme = uri.getScheme();
            if (scheme == null)
            {
                builder.pushColour(0xFF0000);
                builder.pushLiteral("Check inscribe.log");
                builder.popColour();
                LOGGER.error("{} has bad scheme", destination, scheme);
                return;
            }
            else if (scheme.equals(Inscribe.MOD_ID))
            {
                String[] split = uri.getRawAuthority().split(":");
                if (split.length != 2)
                {
                    builder.pushColour(0xFF0000);
                    builder.pushLiteral("BAD ID: " + uri);
                    builder.popColour();
                    return;
                }
                try
                {
                    Identifier entryId = new Identifier(split[0], split[1] + uri.getPath());
                    String tooltipText = link.getTitle() != null ? link.getTitle() : entryId.toString();
                    builder.startEntryLink(entryId, tooltipText);
                }
                catch (InvalidIdentifierException e)
                {
                    LOGGER.error("Invalid identifier", e);
                    return;
                }
            }
            else if (ALLOWED_SCHEMES.contains(scheme))
            {
                if (MinecraftClient.getInstance().options.chatLinks)
                    builder.startWebLink(uri);
                else
                    LOGGER.info("{} disabled because web links are disabled in vanilla chat settings", link);
            }
            else
            {
                builder.pushColour(0xFF0000);
                builder.pushLiteral("ILLEGAL SCHEME: " + scheme);
                builder.popColour();
                LOGGER.error("{} has illegal scheme: {}", destination, scheme);
                return;
            }
            visitChildren(link);
            if (scheme != null)
            {
                if (scheme.equals(Inscribe.MOD_ID))
                    builder.endEntryLink();
                else if (ALLOWED_SCHEMES.contains(scheme))
                    builder.endWebLink();
            }
        }
        catch (URISyntaxException e)
        {
            builder.pushColour(0xFF0000);
            builder.pushLiteral("Check inscribe.log");
            builder.popColour();
            LOGGER.error("Link error", e);
            return;
        }
    }

    @Override
    public void visit(HtmlInline htmlInline)
    {
        System.out.println("HTML unsupported");
    }

    @Override
    public void visit(HtmlBlock htmlBlock)
    {
        System.out.println("HTML unsupported");
    }

    @Override
    public void visit(CustomNode customNode)
    {
        if (customNode instanceof Ins)
            visit((Ins) customNode);
    }
}