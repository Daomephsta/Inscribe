package io.github.daomephsta.inscribe.common.packets;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.inscribe.common.guide.xmlformat.CommonGuideDefinition;
import io.github.daomephsta.inscribe.common.guide.xmlformat.ItemSpecification;
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
		.registerExactMatchConverter(CommonGuideDefinition.class, new CommonGuideDefinition.Converter());
	private static final Logger GUIDE_LOGGER = LogManager.getLogger(Inscribe.MOD_ID + ":guide_logger");
	private final Identifier ID = new Identifier(Inscribe.MOD_ID, "send_guide_definition"); 
	
	public Packet<?> createPacket(Collection<CommonGuideDefinition> guideDefinitions)
	{
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		data.writeInt(guideDefinitions.size());
		for(CommonGuideDefinition definition : guideDefinitions)
		{
			BYTE_BUF_CONVERTER.writeToByteBuffer(data, definition, CommonGuideDefinition.class);
		}
		return ServerSidePacketRegistry.INSTANCE.toPacket(getId(), data);
	}
	
	@Override
	public void accept(PacketContext context, PacketByteBuf data)
	{
		int definitionCount = data.readInt();
		Collection<CommonGuideDefinition> guideDefinitions = IntStream.range(0, definitionCount)
			.mapToObj(i -> BYTE_BUF_CONVERTER.readFromByteBuffer(data, CommonGuideDefinition.class))
			.collect(Collectors.toList()); 
		
		context.getTaskQueue().execute(() -> 
		{
			int loadCount = guideDefinitions.size();
			for(CommonGuideDefinition definition : guideDefinitions)
			{
				Guide guide = GuideManager.INSTANCE.getGuide(definition.getGuideId());
				if (guide != null)
					guide.loadCommonDefinition(definition);
				else
				{
					GUIDE_LOGGER.error("[Inscribe] Missing client-side guide with ID {}", definition.getGuideId());
					loadCount -= 1;
				}
				GUIDE_LOGGER.info("[Inscribe] Recieved {} common guide definitions. Loaded {}.", guideDefinitions.size(), loadCount);
			}
		});
	}
	
	public Identifier getId()
	{
		return ID;
	}
}
