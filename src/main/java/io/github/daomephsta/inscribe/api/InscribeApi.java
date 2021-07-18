package io.github.daomephsta.inscribe.api;

import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class InscribeApi
{
    public static ItemStack stackOfGuide(Identifier guide)
    {
        return Inscribe.GUIDE_ITEM.forGuide(guide);
    }
}
