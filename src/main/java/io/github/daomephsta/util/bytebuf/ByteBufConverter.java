package io.github.daomephsta.util.bytebuf;

import net.minecraft.util.PacketByteBuf;

public interface ByteBufConverter<T>
{
	public T read(PacketByteBuf buffer, Class<T> target, ByteBufToObjectConverter converter);
	
	public void write(PacketByteBuf buffer, T object, ObjectToByteBufConverter converter);
}
