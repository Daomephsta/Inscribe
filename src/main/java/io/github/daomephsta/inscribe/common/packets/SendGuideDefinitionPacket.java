package io.github.daomephsta.inscribe.common.packets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.inscribe.common.guide.GuideDefinitionCommon;
import io.github.daomephsta.inscribe.common.guide.ItemSpecification;
import io.github.daomephsta.util.bytebuf.ByteBufConverterManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.*;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class SendGuideDefinitionPacket implements PacketConsumer
{
	private static final ByteBufConverterManager BYTE_BUF_CONVERTER = new ByteBufConverterManager()
		.registerSubtypeMatchingConverter(ItemSpecification.class, new ItemSpecification.Converter())
		.registerExactMatchConverter(GuideDefinitionCommon.class, new GuideDefinitionCommon.Converter());
	private static final Logger GUIDE_LOGGER = LogManager.getLogger(Inscribe.MOD_ID + ":guide_logger");
	private final Identifier ID = new Identifier(Inscribe.MOD_ID, "send_guide_definition"); 
	
	public Packet<?> createPacket(GuideDefinitionCommon definition)
	{
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		BYTE_BUF_CONVERTER.writeToByteBuffer(data, definition, GuideDefinitionCommon.class);
		return ServerSidePacketRegistry.INSTANCE.toPacket(getId(), data);
	}
	
	@Override
	public void accept(PacketContext context, PacketByteBuf data)
	{
		GuideDefinitionCommon definition = BYTE_BUF_CONVERTER.readFromByteBuffer(data, GuideDefinitionCommon.class); 
		
		context.getTaskQueue().execute(() -> 
		{
			Guide guide = GuideManager.INSTANCE.getGuide(definition.getGuideId());
			if (guide != null)
				guide.loadCommonDefinition(definition);
			else
				GUIDE_LOGGER.error("Missing client-side guide with ID {}", definition.getGuideId());
		});
	}
	
	public Identifier getId()
	{
		return ID;
	}
}
