package io.github.daomephsta.inscribe.client;

import java.util.Collection;
import java.util.stream.Collectors;

import io.github.daomephsta.inscribe.client.guide.*;
import io.github.daomephsta.inscribe.client.hooks.ClientPlayerJoinWorldCallback;
import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.inscribe.common.packets.Packets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;

public class InscribeClientInitialiser implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		new Inscribe().onInitialise();
		ResourceManagerHelper.get(ResourceType.ASSETS).registerReloadListener(GuideManager.INSTANCE);
		ClientPlayerJoinWorldCallback.EVENT.register(player -> 
		{
			if (GuideThemeLoader.INSTANCE.getErrored() || GuideManager.INSTANCE.getErrored())
				player.addChatMessage(new TranslatableTextComponent(Inscribe.MOD_ID + ".chat_message.load_failure"), false);
			Collection<Identifier> guideIds = GuideManager.INSTANCE.getGuides().stream()
				.map(Guide::getIdentifier)
				.collect(Collectors.toList());
			if (!guideIds.isEmpty())
				ClientSidePacketRegistry.INSTANCE.sendToServer(Packets.REQUEST_GUIDE_DEFINITION.createPacket(guideIds));
		});
	}
}
