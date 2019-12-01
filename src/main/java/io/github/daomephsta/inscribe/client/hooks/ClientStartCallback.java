package io.github.daomephsta.inscribe.client.hooks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;

public interface ClientStartCallback
{
    public static final Event<ClientStartCallback> EVENT =
        EventFactory.createArrayBacked(ClientStartCallback.class,
        callbacks -> client ->
        {
            for (ClientStartCallback callback : callbacks)
            {
                callback.invoke(client);
            }
        });

    public void invoke(MinecraftClient client);
}
