package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;

public class TextBlockWidget extends GuideWidget
{
    private final Alignment horizontalAlignment,
                            verticalAlignment;
    private int widthHint, heightHint;
    private TextNode contentHead, contentTail;
    private int nodeCount;

    public TextBlockWidget(Alignment horizontalAlignment, Alignment verticalAlignment)
    {
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
    }

    @Override
    public void renderWidget(int mouseX, int mouseY, float lastFrameDuration)
    {
        if (contentHead == null)
            return;
        float left = horizontalAlignment.offsetX(x(), this, hintWidth());
        float x = left;
        float y = verticalAlignment.offsetY(y(), this, hintHeight());
        TextNode cursor = contentHead;
        while (cursor != null)
        {
            cursor.render(x, y, mouseX, mouseY, lastFrameDuration);
            if (cursor instanceof LineBreak)
            {
                y += 9;
                x = left;
            }
            else
                x += cursor.getWidth();
            cursor = cursor.next;
        }
    }

    public void append(TextNode content)
    {
        heightHint += 1;
        Preconditions.checkNotNull(content);
        if (contentHead == null)
            contentHead = contentTail = content;
        else
        {
            contentTail.next = content;
            contentTail = content;
        }
        nodeCount += 1;
    }

    @Override
    public int hintHeight()
    {
        return heightHint;
    }

    @Override
    public int hintWidth()
    {
        return widthHint;
    }

    @Override
    public String toString()
    {
        String nodesToString = Stream.iterate(contentHead, TextNode::next)
            .limit(nodeCount)
            .map(Object::toString)
            .collect(Collectors.joining(", "));
        return String.format("TextBlockWidget[nodes={%s}, horizontalAlignment=%s, verticalAlignment=%s]", nodesToString, horizontalAlignment, verticalAlignment);
    }
}
