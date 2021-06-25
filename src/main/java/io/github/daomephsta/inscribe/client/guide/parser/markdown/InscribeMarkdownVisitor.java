package io.github.daomephsta.inscribe.client.guide.parser.markdown;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.commonmark.ext.ins.Ins;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Document;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
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

import io.github.daomephsta.inscribe.client.guide.gui.widget.component.Tooltip;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.LabelWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextBlockWidget;
import io.github.daomephsta.inscribe.client.guide.parser.FormatFlags;
import io.github.daomephsta.inscribe.client.guide.parser.markdown.ListData.ListType;
import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.inscribe.common.util.Identifiers;
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
        builder.addTextBlock(Alignment.LEADING, Alignment.LEADING);
    }

    @Override
    public void visit(BlockQuote blockQuote)
    {
        builder.startBlockQuote();
        visitChildren(blockQuote);
        builder.endBlockQuote();
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
                error(link, "%s has bad scheme", destination);
                return;
            }
            else if (scheme.equals(Inscribe.MOD_ID))
            {
                String[] split = uri.getRawAuthority().split(":");
                if (split.length != 2)
                {
                    error(link, "Missing/excess ':' in identifier %s", uri.getRawAuthority());
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
                    error(link, "Invalid identifier: %s", e.getMessage());
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
                error(link, "%s has illegal scheme: %s Allowed: %s", uri, scheme, ALLOWED_SCHEMES);
                return;
            }
            visitChildren(link);
            if (scheme.equals(Inscribe.MOD_ID))
                builder.endEntryLink();
            else if (ALLOWED_SCHEMES.contains(scheme))
                builder.endWebLink();
        }
        catch (URISyntaxException e)
        {
            error(link, "Invalid destination: %s", e.getMessage());
            return;
        }
    }

    @Override
    public void visit(Image image)
    {
        try
        {
            Identifier location = Identifiers.working(image.getDestination())
                .addPathSegment(0, "textures").toIdentifier();
            if (MinecraftClient.getInstance().getResourceManager().containsResource(location))
                builder.addInlineImage(location, image.getTitle());
            else
                error(image, "Image %s not found", location);
        }
        catch (InvalidIdentifierException e)
        {
            error(image, "Invalid image ID: %s", e.getMessage());
        }
    }

    private void error(Node node, String format, Object... args)
    {
        LOGGER.error(format.replaceAll("%\\w+", "{}"), args);
        if (args[args.length - 1] instanceof Throwable t)
        {
            StringWriter stringWriter = new StringWriter();
            t.printStackTrace(new PrintWriter(stringWriter));
            args[args.length - 1] = stringWriter.toString();
        }
        builder.pushColour(0xFF00FF);
        builder.pushRenderable(new Tooltip(tooltip ->
            tooltip.accept(String.format(format, args))));
        visitChildren(node);
        builder.popRenderable();
        builder.popColour();
    }

    @Override
    public void visit(Code code)
    {
        visitCodeBlock(code, code.getLiteral());
    }

    @Override
    public void visit(FencedCodeBlock fencedCodeBlock)
    {
        visitCodeBlock(fencedCodeBlock, fencedCodeBlock.getLiteral());
    }

    @Override
    public void visit(IndentedCodeBlock indentedCodeBlock)
    {
        visitCodeBlock(indentedCodeBlock, indentedCodeBlock.getLiteral());
    }

    private void visitCodeBlock(Node codeBlock, String literal)
    {
        builder.pushFont(TextBlockWidget.MONO_FONT);
        String[] lines = literal.split("\n");
        for (int i = 0; i < lines.length; i++)
        {
            if (i > 0)
                builder.addHardLineBreak();
            builder.pushLiteral(lines[i]);
        }
        visitChildren(codeBlock);
        builder.addTextBlock(Alignment.LEADING, Alignment.LEADING);
        builder.popFont();
    }

    @Override
    public void visit(HtmlInline htmlInline)
    {
        LOGGER.error("{}: Inline html is not supported", htmlInline.getLiteral());
    }

    @Override
    public void visit(HtmlBlock htmlBlock)
    {
        LOGGER.error("{}: Block html is not supported", htmlBlock.getLiteral());
    }

    @Override
    public void visit(CustomNode customNode)
    {
        if (customNode instanceof Ins)
            visit((Ins) customNode);
    }
}