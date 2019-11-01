package io.github.daomephsta.inscribe.common;

import io.github.daomephsta.inscribe.common.guide.item.GuideItem;
import net.minecraft.util.registry.Registry;

public class Inscribe
{
	public static final String MOD_ID = "inscribe";
	public static final GuideItem GUIDE_ITEM = new GuideItem();

	public void onInitialise()
	{
		Registry.register(Registry.ITEM, MOD_ID + ":guide", GUIDE_ITEM);
	}
}
