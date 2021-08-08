package io.github.daomephsta.inscribe.api;

import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.util.Identifier;

public class GuideFlags
{
    private final Object2BooleanMap<Identifier> flags = new Object2BooleanOpenHashMap<>();

    public boolean isTrue(Identifier flagId)
    {
        assertExists(flagId);
        return flags.getBoolean(flagId);
    }
    
    public boolean isFalse(Identifier flagId)
    {
        assertExists(flagId);
        return !flags.getBoolean(flagId);
    }

    public void set(Identifier flagId, boolean value)
    {
        assertExists(flagId);
        flags.put(flagId, value);
    }

    private void assertExists(Identifier flagId)
    {
        if (!has(flagId))
            throw new IllegalArgumentException("Unregistered flag " + flagId);
    }

    public boolean has(Identifier flagId)
    {
        return flags.containsKey(flagId);
    }
    
    public void register(Identifier key, boolean defaultValue)
    {
        if (has(key))
            throw new IllegalArgumentException("Duplicate flag " + key);
        flags.put(key, defaultValue);
    }

    public Set<Identifier> getIds()
    {
        return flags.keySet();
    }
}
