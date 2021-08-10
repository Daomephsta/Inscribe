package io.github.daomephsta.inscribe.client.guide.template;

import java.util.Map;

public record Arguments(Map<String, String> arguments)
{
    public String getString(Object key)
    {
        return arguments.get(key);
    }

    public String putString(String key, String value)
    {
        return arguments.put(key, value);
    }
}