package io.github.daomephsta.inscribe.server;

import io.github.daomephsta.inscribe.common.Inscribe;
import net.fabricmc.api.DedicatedServerModInitializer;

public class InscribeServerInitialiser implements DedicatedServerModInitializer
{
	@Override
	public void onInitializeServer()
	{
		new Inscribe().onInitialise();
	}
}