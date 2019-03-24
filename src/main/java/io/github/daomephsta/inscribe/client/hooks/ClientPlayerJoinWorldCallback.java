package io.github.daomephsta.inscribe.client.hooks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface ClientPlayerJoinWorldCallback
{
	public static final Event<ClientPlayerJoinWorldCallback> EVENT = 
		EventFactory.createArrayBacked(ClientPlayerJoinWorldCallback.class, 
		callbacks -> player -> 
		{
			for (ClientPlayerJoinWorldCallback callback : callbacks)
			{
				callback.invoke(player);
			}
		});
	
	public void invoke(PlayerEntity player);
}
