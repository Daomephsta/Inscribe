package io.github.daomephsta.util.bytebuf;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.util.PacketByteBuf;

public class ByteBufConverterManager implements ByteBufToObjectConverter, ObjectToByteBufConverter
{
	private final Map<Class<?>, ByteBufConverter<?>> exactMatchConverters = new HashMap<>(),
													 subTypeMatchingCache = new HashMap<>(),
													 subTypeMatchingConverters = new HashMap<>();
	@Override
	public <T> T readFromByteBuffer(PacketByteBuf buffer, Class<T> target)
	{
		return getConverter(target).read(buffer, target, this); 
	}
	
	@Override
	public <T> void writeToByteBuffer(PacketByteBuf buffer, T object, Class<T> clazz)
	{
		getConverter(clazz).write(buffer, object, this);
	}

	@SuppressWarnings("unchecked")
	private <T> ByteBufConverter<T> getConverter(Class<T> target)
	{
		ByteBufConverter<?> converter = exactMatchConverters.get(target);
		if (converter == null)
			converter = subTypeMatchingCache.get(target);
		if (converter == null)
			converter = subTypeMatchingConverters.get(target);
		if (converter == null)
		{
			for (Entry<Class<?>, ByteBufConverter<?>> entry : subTypeMatchingConverters.entrySet())
			{
				if (entry.getKey().isAssignableFrom(target))
				{
					converter = entry.getValue();
					subTypeMatchingCache.put(target, converter);
				}
			}
		}
		if (converter == null)
			throw new IllegalArgumentException("Could not find a converter for " + target);
		
		return (ByteBufConverter<T>) converter;
	}
	
	public <T> ByteBufConverterManager registerExactMatchConverter(Class<T> target, ByteBufConverter<T> converter)
	{
		if (exactMatchConverters.put(target, converter) != null)
			throw new IllegalArgumentException("Converter already registered for " + target);
		return this;
	}
	
	public <T> ByteBufConverterManager registerSubtypeMatchingConverter(Class<T> targetBase, ByteBufConverter<T> converter)
	{
		if (subTypeMatchingConverters.put(targetBase, converter) != null)
			throw new IllegalArgumentException("Converter already registered for " + targetBase);
		for (Class<?> clazz : subTypeMatchingConverters.keySet())
		{
			if (clazz.isAssignableFrom(targetBase) && !clazz.equals(targetBase))
				throw new IllegalArgumentException("Converter already registered for " + clazz + ", an ancestor of " + targetBase);
		}
		return this;
	}
}
