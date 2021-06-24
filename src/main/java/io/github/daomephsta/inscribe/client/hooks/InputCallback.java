package io.github.daomephsta.inscribe.client.hooks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil.Key;

public interface InputCallback
{
    public static final Event<InputCallback> EVENT =
        EventFactory.createArrayBacked(InputCallback.class,
        callbacks -> (client, key, action, modifiers) ->
        {
            for (InputCallback callback : callbacks)
            {
                callback.processInput(client, key, action, modifiers);
            }
        });

    public void processInput(MinecraftClient client, Key key, int action, int modifiers);
}
