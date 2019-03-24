package io.github.daomephsta.util.bytebuf;

import net.minecraft.util.PacketByteBuf;

public interface ByteBufToObjectConverter
{
	public <T> T readFromByteBuffer(PacketByteBuf buffer, Class<T> target);
}
