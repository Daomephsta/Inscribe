package io.github.daomephsta.inscribe.client;

import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.GuideModel;
import io.github.daomephsta.inscribe.client.input.KeyBindings;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class InscribeClientInitialiser implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        new Inscribe().onInitialise();
        registerModels();
        KeyBindings.initialise();
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
