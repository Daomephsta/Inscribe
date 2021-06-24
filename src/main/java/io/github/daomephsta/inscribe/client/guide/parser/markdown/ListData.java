package io.github.daomephsta.inscribe.client.guide.parser.markdown;

import java.util.Deque;

import io.github.daomephsta.inscribe.client.guide.gui.widget.text.FormattedTextNode;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.Indent;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextNode;
import net.minecraft.client.MinecraftClient;

class ListData
{
    private final ListData.ListType type;
    private int currentItemIndex = 0;

    ListData(ListData.ListType type)
    {
        this.type = type;
    }

    public void addListMarker(Deque<TextNode> textNodes, int indentLevel)
    {
        type.addListMarker(textNodes, indentLevel, currentItemIndex);
    }

    @Override
    public String toString()
    {
        return String.format("ListData(type: %s, currentItemIndex: %d)", type, currentItemIndex);
    }

    public void nextItem()
    {
        currentItemIndex += 1;
    }

    public static enum ListType
    {
        ORDERED
        {
            @Override
            public void addListMarker(Deque<TextNode> textNodes, int indentLevel, int currentItemIndex)
            {
                textNodes.push(new FormattedTextNode(Integer.toString(currentItemIndex + 1) + ". ",
                    MinecraftClient.DEFAULT_FONT_ID, 0x000000));
                textNodes.push(new Indent(indentLevel));
            }
        },
        UNORDERED
        {
            @Override
            public void addListMarker(Deque<TextNode> textNodes, int indentLevel, int currentItemIndex)
            {
                textNodes.push(new FormattedTextNode("\u2022 ",
                    MinecraftClient.DEFAULT_FONT_ID, 0x000000));
                textNodes.push(new Indent(indentLevel));
            }
        };

        public abstract void addListMarker(Deque<TextNode> textNodes, int indentLevel, int currentItemIndex);
    }
}