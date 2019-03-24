package io.github.daomephsta.inscribe.common.packets;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;

public class Packets
{
	public static final SendGuideDefinitionPacket SEND_GUIDE_DEFINITION = new SendGuideDefinitionPacket();
	
	public static void registerPackets()
	{
		ClientSidePacketRegistry.INSTANCE.register(SEND_GUIDE_DEFINITION.getId(), SEND_GUIDE_DEFINITION);
	}
}
