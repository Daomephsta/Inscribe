package io.github.daomephsta.inscribe.common.guide;

import java.util.function.Function;

import io.github.daomephsta.util.bytebuf.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.PacketByteBuf;

public interface ItemSpecification extends Function<GuideDefinitionCommon, ItemStack>
{
	@Override
	default ItemStack apply(GuideDefinitionCommon definition)
	{
		return createGuideItemStack(definition);
	}
	
	public ItemStack createGuideItemStack(GuideDefinitionCommon definition);
	
	public void registerGuideItemStack();
	
	public static class Standard implements ItemSpecification
	{
		private final String itemGroupId;
		
		public Standard(String itemGroupId)
		{
			this.itemGroupId = itemGroupId;
		}

		@Override
		public ItemStack createGuideItemStack(GuideDefinitionCommon definition) {return ItemStack.EMPTY;}

		@Override
		public void registerGuideItemStack() {}

		@Override
		public String toString()
		{
			return String.format("Standard [itemGroup=%s]", itemGroupId);
		}	
	}
	
	public static class Custom implements ItemSpecification
	{
		private final ItemStack stack;

		public Custom(ItemStack stack)
		{
			this.stack = stack;
		}

		@Override
		public ItemStack createGuideItemStack(GuideDefinitionCommon definition) 
		{
			return stack;
		}

		@Override
		public void registerGuideItemStack() {}

		@Override
		public String toString()
		{
			return String.format("Custom [stack=%s]", stack);
		}	
	}
	
	public static class None implements ItemSpecification
	{
		@Override
		public ItemStack createGuideItemStack(GuideDefinitionCommon definition) 
		{
			return ItemStack.EMPTY;
		}

		@Override
		public void registerGuideItemStack() {}

		@Override
		public String toString()
		{
			return String.format("None []");
		}	
	}
	
	public static class Converter implements ByteBufConverter<ItemSpecification>
	{
		private static final int STANDARD = 0,
								 CUSTOM = 1,
								 NONE = 2;
		@Override
		public ItemSpecification read(PacketByteBuf buffer, Class<ItemSpecification> target, ByteBufToObjectConverter converter)
		{
			int ordinal = buffer.readByte();
			switch (ordinal)
			{
			case STANDARD:
				String itemGroupId = buffer.readString(Short.MAX_VALUE);
				return new Standard(itemGroupId);
				
			case CUSTOM:
				return new Custom(buffer.readItemStack());
				
			case NONE:
				return new None();

			default:
				throw new IllegalStateException("Unexpected ordinal " + ordinal);
			}
		}

		@Override
		public void write(PacketByteBuf buffer, ItemSpecification specification, ObjectToByteBufConverter converter)
		{
			if (specification instanceof Standard)
			{
				buffer.writeByte(STANDARD);
				buffer.writeString(((Standard) specification).itemGroupId, Short.MAX_VALUE);
			}
			else if (specification instanceof Custom)
			{
				buffer.writeByte(CUSTOM);
				buffer.writeItemStack(((Custom) specification).stack);
			}
			else if (specification instanceof None)
			{
				buffer.writeByte(NONE);
			}
		}
	}
}