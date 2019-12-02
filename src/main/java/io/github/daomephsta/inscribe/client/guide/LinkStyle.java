package io.github.daomephsta.inscribe.client.guide;

import static java.util.Arrays.stream;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.util.StringIdentifiable;

public enum LinkStyle implements StringIdentifiable
{
    ICON_WITH_TOOLTIP("icon_with_tooltip", true),
    ICON_WITH_TEXT("icon_with_text", true),
    TEXT("text", false);

    private static final Map<String, LinkStyle> REPRESENTATION_TO_STYLE;
    static
    {
        REPRESENTATION_TO_STYLE = stream(LinkStyle.values()).collect(ImmutableMap.toImmutableMap(LinkStyle::asString, s -> s));
    }
    private final String stringRepresentation;
    private final boolean requiresIcon;

    private LinkStyle(String stringRepresentation, boolean requiresIcon)
    {
        this.stringRepresentation = stringRepresentation;
        this.requiresIcon = requiresIcon;
    }

    public static LinkStyle fromRepresentation(String representation)
    {
        return REPRESENTATION_TO_STYLE.get(representation);
    }

    @Override
    public String asString()
    {
        return stringRepresentation;
    }

    public boolean requiresIcon()
    {
        return requiresIcon;
    }
}