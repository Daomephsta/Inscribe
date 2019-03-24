package io.github.daomephsta.util.bytebuf;

import net.minecraft.util.PacketByteBuf;

public interface ObjectToByteBufConverter
{
	public <T> void writeToByteBuffer(PacketByteBuf buffer, T object, Class<T> clazz);
}
