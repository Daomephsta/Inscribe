package io.github.daomephsta.inscribe.client.guide.parser.markdown;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Stream;

import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.FormattedTextNode;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.LabelWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.LineBreak;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextBlockWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextNode;
import io.github.daomephsta.inscribe.client.guide.parser.FormatFlags;

public class InscribeBuilder
{
    private final GuideFlow output;
    private final Deque<FormatFlags> formatFlags = new ArrayDeque<>();
    private final Deque<TextNode> textNodes = new ArrayDeque<>();

    public InscribeBuilder(GuideFlow output)
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
        TextBlockWidget textBlock = new TextBlockWidget(horizontalAlignment, verticalAlignment);
        while (!textNodes.isEmpty())
            textBlock.append(textNodes.poll());
        output.add(textBlock);
    }
}
