package io.github.daomephsta.inscribe.client.hooks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil.KeyCode;

public interface InputCallback
{
    public static final Event<InputCallback> EVENT =
        EventFactory.createArrayBacked(InputCallback.class,
        callbacks -> (client, keyCode, action, modifiers) ->
        {
            for (InputCallback callback : callbacks)
            {
                callback.processInput(client, keyCode, action, modifiers);
            }
        });

    public void processInput(MinecraftClient client, KeyCode keyCode, int action, int modifiers);
}
