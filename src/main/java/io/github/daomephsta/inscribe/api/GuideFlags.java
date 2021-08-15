package io.github.daomephsta.inscribe.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;

import net.minecraft.util.Identifier;

public class GuideFlags
{
    private final Map<Identifier, BooleanSupplier> flags = new HashMap<>();

    public boolean isTrue(Identifier flagId)
    {
        if (!has(flagId))
            throw new IllegalArgumentException("Unregistered flag " + flagId);
        return flags.get(flagId).getAsBoolean();
    }
    
    public boolean isFalse(Identifier flagId)
    {
        return !isTrue(flagId);
    }

    public boolean has(Identifier flagId)
    {
        return flags.containsKey(flagId);
    }
    
    public void register(Identifier key, BooleanSupplier supplier)
    {
        if (has(key))
            throw new IllegalArgumentException("Duplicate flag " + key);
        flags.put(key, supplier);
    }

    public Set<Identifier> getIds()
    {
        return flags.keySet();
    }
}
