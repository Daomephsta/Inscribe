package io.github.daomephsta.inscribe.common;

import io.github.daomephsta.inscribe.common.guide.item.GuideItem;
import io.github.daomephsta.inscribe.server.DelegatingArgumentType;
import io.github.daomephsta.inscribe.server.InscribeCommand;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.util.registry.Registry;

public class Inscribe
{
	public static final String MOD_ID = "inscribe";
	public static final GuideItem GUIDE_ITEM = new GuideItem();

	public void onInitialise()
	{
		Registry.register(Registry.ITEM, MOD_ID + ":guide", GUIDE_ITEM);
        ServerStartCallback.EVENT.register(server ->
        {
            ArgumentTypes.register(Inscribe.MOD_ID + ":delegating", DelegatingArgumentType.class, new DelegatingArgumentType.Serialiser());
            InscribeCommand.register(server.getCommandManager().getDispatcher());
        });
	}
}
