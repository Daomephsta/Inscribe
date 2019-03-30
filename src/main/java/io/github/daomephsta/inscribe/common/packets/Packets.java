package io.github.daomephsta.inscribe.common.packets;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

public class Packets
{
	public static final SendGuideDefinitionPacket SEND_GUIDE_DEFINITION = new SendGuideDefinitionPacket();
	public static final RequestGuideDefinitionPacket REQUEST_GUIDE_DEFINITION = new RequestGuideDefinitionPacket();
	
	public static void registerPackets()
	{
		ClientSidePacketRegistry.INSTANCE.register(SEND_GUIDE_DEFINITION.getId(), SEND_GUIDE_DEFINITION);
		ServerSidePacketRegistry.INSTANCE.register(REQUEST_GUIDE_DEFINITION.getId(), REQUEST_GUIDE_DEFINITION);
	}
}
