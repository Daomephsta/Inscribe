package io.github.daomephsta.inscribe.client;

import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.GuideModel;
import io.github.daomephsta.inscribe.client.hooks.ClientPlayerJoinWorldCallback;
import io.github.daomephsta.inscribe.client.input.KeyBindings;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class InscribeClientInitialiser implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		new Inscribe().onInitialise();
		registerEventCallbacks();
		registerModels();
		KeyBindings.initialise();
	}

	public void registerEventCallbacks()
	{
		ClientPlayerJoinWorldCallback.EVENT.register(player ->
		{
			if (GuideManager.INSTANCE.getErrored())
				player.addChatMessage(new TranslatableText(Inscribe.MOD_ID + ".chat_message.load_failure"), false);
		});
	}

	public void registerModels()
	{
		ModelLoadingRegistry.INSTANCE.registerAppender((resourceManager, out) ->
		{
			for (Identifier modelId : GuideManager.INSTANCE.getGuideModelIds())
			{
				if (modelId instanceof ModelIdentifier)
					out.accept((ModelIdentifier) modelId);
			}
		});
		ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> new GuideModel.Provider());
	}
}
