package io.github.daomephsta.inscribe.common.util;

public class Colors
{
    private Colors() {}

    public static int[] decodeRGB(int hexcode)
    {
        return new int []
        {
            hexcode >> 16 & 0xFF,
            hexcode >> 8 & 0xFF,
            hexcode & 0xFF
        };
    }
}
