package io.github.daomephsta.inscribe.client.input;

import java.util.HashSet;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

import io.github.daomephsta.inscribe.client.hooks.InputCallback;
import io.github.daomephsta.inscribe.client.input.WatchedKeyBinding.Modifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil.Key;

public enum KeyWatcher implements InputCallback
{
    INSTANCE;

    private final WatchedKeyBindingIndex watchedKeys = new WatchedKeyBindingIndex();
    private final Set<WatchedKeyBinding> keysDown = new HashSet<>();

    public void watch(WatchableKeyBinding key, KeyEventCallback callback, Modifier... modifiers)
    {
        WatchedKeyBinding watched = WatchedKeyBinding.from(watchedKeys, key, modifiers, callback);
        key.addChild(watched);
        watchedKeys.add(watched);
    }

    public void initialise()
    {
        InputCallback.EVENT.register(this);
    }

    @Override
    public void processInput(MinecraftClient client, Key key, int action, int modifiers)
    {
        WatchedKeyBinding watchedKey = watchedKeys.get(key, modifiers);
        if (watchedKey != null)
        {
            KeyAction keyAction = KeyAction.fromGlConstant(action);
            if (keyAction == KeyAction.DOWN)
            {
                keysDown.add(watchedKey);
                watchedKey.processEvent(client, keyAction);
            }
            else if (keyAction == KeyAction.UP)
            {
                keysDown.remove(watchedKey);
                watchedKey.processEvent(client, KeyAction.fromGlConstant(action));
            }
        }
    }

    public static enum KeyAction
    {
        UP(GLFW.GLFW_RELEASE),
        DOWN(GLFW.GLFW_PRESS),
        REPEAT(GLFW.GLFW_REPEAT);

        private final int glConstant;

        private KeyAction(int glConstant)
        {
            this.glConstant = glConstant;
        }

        private static KeyAction fromGlConstant(int glConstant)
        {
            for (KeyAction action : values())
            {
                if (action.glConstant == glConstant)
                    return action;
            }
            throw new IllegalArgumentException("Unknown key action " + glConstant);
        }
    }

    public interface KeyEventCallback
    {
        public void processEvent(MinecraftClient client, KeyAction action);
    }
}
