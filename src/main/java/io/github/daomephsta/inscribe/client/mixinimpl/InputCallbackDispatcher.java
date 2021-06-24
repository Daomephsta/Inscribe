package io.github.daomephsta.inscribe.client.mixinimpl;

import io.github.daomephsta.inscribe.client.hooks.InputCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

public class InputCallbackDispatcher
{
    public static void inscribe_onKey(long windowId, int key, int scancode, int action, int modifiers)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        if (windowId == client.getWindow().getHandle())
        {
            Key keyCode = key != InputUtil.UNKNOWN_KEY.getCode()
                ? InputUtil.Type.KEYSYM.createFromCode(key)
                : InputUtil.Type.SCANCODE.createFromCode(scancode);
            InputCallback.EVENT.invoker().processInput(client, keyCode, action, modifiers);
        }
    }

    public static void inscribe_onMouseButton(long windowId, int button, int action, int modifiers)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        if (windowId == client.getWindow().getHandle())
        {
            Key mouseCode = InputUtil.Type.MOUSE.createFromCode(button);
            InputCallback.EVENT.invoker().processInput(client, mouseCode, action, modifiers);
        }
    }
}
