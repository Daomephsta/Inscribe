package io.github.daomephsta.inscribe.client.guide.parser;

import java.util.function.UnaryOperator;

import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

public enum FormatFlags
{
    BOLD(Formatting.BOLD, style -> style.withBold(true)),
    ITALIC(Formatting.ITALIC, style -> style.withItalic(true)),
    UNDERLINE(Formatting.UNDERLINE, style -> style.withUnderline(true)),
    STRIKETHROUGH(Formatting.STRIKETHROUGH, style -> style.withStrikethrough(true));

    private final Formatting delegate;
    private final UnaryOperator<Style> styler;

    private FormatFlags(Formatting delegate, UnaryOperator<Style> styler)
    {
        this.delegate = delegate;
        this.styler = styler;
    }

    public String getMCFormatCode()
    {
        return delegate.toString();
    }
    
    public Style apply(Style style)
    {
        return styler.apply(style);
    }
}
