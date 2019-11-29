package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

public class LineBreak extends TextNode
{
    @Override
    int getWidth()
    {
        return 0;
    }

    @Override
    public String toString()
    {
        return "\\n";
    }
}