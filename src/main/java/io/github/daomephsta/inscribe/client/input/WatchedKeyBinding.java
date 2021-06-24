package io.github.daomephsta.inscribe.client.input;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;

import io.github.daomephsta.inscribe.client.input.KeyWatcher.KeyAction;
import io.github.daomephsta.inscribe.client.input.KeyWatcher.KeyEventCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil.Key;

class WatchedKeyBinding
{
    private final WatchedKeyBindingIndex parent;
    private final WatchableKeyBinding delegate;
    private final Modifier[] modifiers;
    private final int modifierBits;
    private final KeyEventCallback callback;

    private WatchedKeyBinding(WatchedKeyBindingIndex parent, WatchableKeyBinding delegate, Modifier[] modifiers, KeyEventCallback callback)
    {
        this.parent = parent;
        this.delegate = delegate;
        this.modifiers = modifiers;
        this.modifierBits = Arrays.stream(modifiers)
            .mapToInt(Modifier::getGlBitmask)
            .reduce((result, element) -> result | element)
            .orElse(0);
        this.callback = callback;
    }

    static WatchedKeyBinding from(WatchedKeyBindingIndex parent, WatchableKeyBinding delegate, Modifier[] modifiers, KeyEventCallback callback)
    {
        return new WatchedKeyBinding(parent, delegate, modifiers, callback);
    }

    public boolean matches(Key keyCode, int modifierFlags)
    {
       if (!delegate.getBoundKey().equals(keyCode))
           return false;
       int uncheckedModifierCount = Integer.bitCount(modifierFlags);
       for (Modifier modifier : modifiers)
       {
           if (!modifier.isSet(modifierFlags))
               return false;
           else
               uncheckedModifierCount--;
       }
       return uncheckedModifierCount == 0;
    }

    Key getBoundKey()
    {
        return delegate.getBoundKey();
    }

    int getModifierBits()
    {
        return modifierBits;
    }

    void markDelegateDirty()
    {
        parent.updateBindingKey(this, delegate.getBoundKey());
    }

    public static enum Modifier
    {
        SHIFT(GLFW.GLFW_MOD_SHIFT),
        CTRL(GLFW.GLFW_MOD_CONTROL),
        ALT(GLFW.GLFW_MOD_ALT),
        SUPER(GLFW.GLFW_MOD_SUPER);

        private final int glBitmask;

        private Modifier(int glBitmask)
        {
            this.glBitmask = glBitmask;
        }

        int getGlBitmask()
        {
            return glBitmask;
        }

        public boolean isSet(int modifierFlags)
        {
            return (modifierFlags & this.glBitmask) == this.glBitmask;
        }
    }

    public void processEvent(MinecraftClient client, KeyAction action)
    {
        callback.processEvent(client, action);
    }
}
