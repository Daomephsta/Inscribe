package io.github.daomephsta.inscribe.client.guide.parser.markdown;

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
import org.commonmark.node.LinkReferenceDefinition;
import org.commonmark.node.ListItem;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;

import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.LabelWidget;
import io.github.daomephsta.inscribe.client.guide.parser.FormatFlags;
import io.github.daomephsta.inscribe.client.guide.parser.markdown.ListData.ListType;

public class InscribeMarkdownVisitor extends AbstractVisitor
{
    private final InscribeBuilder builder;

    public InscribeMarkdownVisitor(GuideFlow output)
    {
        this.builder = new InscribeBuilder(output);
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
        builder.pushFormatting(FormatFlags.ITALIC);
        visitChildren(emphasis);
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis)
    {
        builder.pushFormatting(FormatFlags.BOLD);
        visitChildren(strongEmphasis);
    }

    private void visit(Ins ins)
    {
        builder.pushFormatting(FormatFlags.UNDERLINE);
        visitChildren(ins);
    }

    @Override
    public void visit(Text text)
    {
        builder.pushLiteral(text.getLiteral(), 0x000000);
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
    public void visit(LinkReferenceDefinition linkReferenceDefinition)
    {
        System.out.println("Link references not supported");
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