package io.github.daomephsta.inscribe.common.packets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.inscribe.common.guide.xmlformat.CommonGuideDefinition;
import io.github.daomephsta.inscribe.common.guide.xmlformat.CommonGuideDefinitionManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.*;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class RequestGuideDefinitionPacket implements PacketConsumer
{
	private static final Logger LOGGER = LogManager.getLogger();
	private final Identifier ID = new Identifier(Inscribe.MOD_ID, "request_guide_definition"); 
	
	public Packet<?> createPacket(Collection<Identifier> guideIds)
	{
		PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		data.writeInt(guideIds.size());
		for(Identifier guideId : guideIds)
		{
			data.writeIdentifier(guideId);
		}
			
		return ClientSidePacketRegistry.INSTANCE.toPacket(getId(), data);
	}
	
	@Override
	public void accept(PacketContext context, PacketByteBuf data)
	{
		int guideIdCount = data.readInt();
		//Collect, because otherwise the work won't get done offthread, since Streams are lazy
		Collection<Identifier> guideIds = IntStream.range(0, guideIdCount)
			.mapToObj(i -> data.readIdentifier())
			.collect(Collectors.toList());
		
		context.getTaskQueue().execute(() -> 
		{
			Collection<CommonGuideDefinition> guideDefinitions = new ArrayList<>();
			for(Identifier guideId : guideIds)
			{
				CommonGuideDefinition definition = CommonGuideDefinitionManager.INSTANCE.get(guideId);
				if (definition != null)
					guideDefinitions.add(definition);
				else
					LOGGER.error("[Inscribe] Missing common guide definition with ID {}", guideId);
			}
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(context.getPlayer(), 
				Packets.SEND_GUIDE_DEFINITION.createPacket(guideDefinitions));
			LOGGER.info("[Inscribe] Sent {} common guide definitions to {}", 
				guideDefinitions.size(), context.getPlayer().getName().getFormattedText());
		});
	}
	
	public Identifier getId()
	{
		return ID;
	}
}
