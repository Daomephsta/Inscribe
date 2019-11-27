package io.github.daomephsta.inscribe.client.input;

import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import net.minecraft.client.util.InputUtil.KeyCode;

class WatchedKeyBindingIndex
{
    private final Int2ObjectSortedMap<WatchedKeyBinding> watchedKeyBindings = new Int2ObjectAVLTreeMap<>();

    void add(WatchedKeyBinding watched)
    {
        watchedKeyBindings.put(getMapKey(watched), watched);
    }

    WatchedKeyBinding get(KeyCode keyCode, int modifiers)
    {
        return watchedKeyBindings.get(getMapKey(keyCode, modifiers));
    }

    void updateBindingKeyCode(WatchedKeyBinding watched, KeyCode keyCode)
    {
        watchedKeyBindings.put(getMapKey(keyCode, watched.getModifierBits()), watched);
    }

    private int getMapKey(WatchedKeyBinding watched)
    {
        return getMapKey(watched.getBoundKey(), watched.getModifierBits());
    }

    private int getMapKey(KeyCode keyCode, int modifiers)
    {
        //Modifiers in last 6 bits, keycode in the rest
        return (modifiers << 26) | keyCode.getKeyCode();
    }
}
