package io.github.daomephsta.inscribe.client.guide.parser.markdown;

import static java.util.stream.Collectors.toList;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Stream;

import io.github.daomephsta.inscribe.client.guide.gui.widget.HorizontalRuleWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.FormattedTextNode;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.LabelWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.LineBreak;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextBlockWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextNode;
import io.github.daomephsta.inscribe.client.guide.parser.FormatFlags;
import io.github.daomephsta.inscribe.client.guide.parser.markdown.ListData.ListType;

public class PageBuilder
{
    private final GuideFlow output;
    private final Deque<FormatFlags> formatFlags = new ArrayDeque<>();
    private final Deque<TextNode> textNodes = new ArrayDeque<>();
    private final Deque<ListData> listData = new ArrayDeque<>();
    private int indentLevel = 0;

    public PageBuilder(GuideFlow output)
    {
        this.output = output;
    }

    public void pushFormatting(FormatFlags formatFlag)
    {
        formatFlags.push(formatFlag);
    }

    public void pushLiteral(String literal, int color)
    {
        FormatFlags[] formatFlags = Stream.generate(this.formatFlags::poll)
            .limit(this.formatFlags.size())
            .toArray(FormatFlags[]::new);
        textNodes.add(new FormattedTextNode(literal, color, formatFlags));
    }

    public void addHardLineBreak()
    {
        textNodes.add(new LineBreak());
    }

    public void addLabel(Alignment horizontalAlignment, Alignment verticalAlignment, float scale)
    {
        output.add(new LabelWidget(textNodes.poll(), horizontalAlignment, verticalAlignment, scale));
    }

    public void addTextBlock(Alignment horizontalAlignment, Alignment verticalAlignment, int color)
    {
        ListData listDatum = listData.peek();
        if (listDatum != null)
        {
            listDatum.addListMarker(textNodes, indentLevel);
        }
        List<TextNode> nodes = Stream.generate(textNodes::poll)
            .limit(textNodes.size())
            .collect(toList());
        TextBlockWidget textBlock = new TextBlockWidget(horizontalAlignment, verticalAlignment, nodes);
        output.add(textBlock);
    }

    public void startList(ListType type)
    {
        listData.push(new ListData(type));
        indentLevel += 1;
    }

    public void endList()
    {
        listData.pop();
        indentLevel -= 1;
    }

    public void nextListItem()
    {
        listData.peek().nextItem();
    }

    public void addHorizontalRule()
    {
        output.add(new HorizontalRuleWidget());
    }
}
