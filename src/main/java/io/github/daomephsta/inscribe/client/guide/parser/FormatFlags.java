package io.github.daomephsta.inscribe.client.guide.parser;

import net.minecraft.util.Formatting;

public enum FormatFlags
{
    BOLD(Formatting.BOLD),
    ITALIC(Formatting.ITALIC),
    UNDERLINE(Formatting.UNDERLINE),
    STRIKETHROUGH(Formatting.STRIKETHROUGH);

    private final Formatting delegate;

    private FormatFlags(Formatting delegate)
    {
        this.delegate = delegate;
    }

    public String getMCFormatCode()
    {
        return delegate.toString();
    }
}
