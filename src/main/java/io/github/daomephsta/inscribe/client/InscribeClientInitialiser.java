package io.github.daomephsta.inscribe.client;

import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.GuideThemeLoader;
import io.github.daomephsta.inscribe.client.hooks.ClientPlayerJoinWorldCallback;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.TranslatableTextComponent;

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
		});
	}
}
