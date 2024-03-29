package io.github.daomephsta.inscribe.client;

import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.GuideModel;
import io.github.daomephsta.inscribe.client.input.KeyBindings;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class InscribeClientInitialiser implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        new Inscribe().onInitialise();
        registerModels();
        BlockEntityRendererRegistry.register(Inscribe.POSTER_BLOCK_ENTITY, PosterBlockEntityRenderer::new);
        KeyBindings.initialise();
    }

    public void registerModels()
    {
        ModelLoadingRegistry.INSTANCE.registerModelProvider((resourceManager, out) ->
        {
            for (Identifier modelId : GuideManager.INSTANCE.getGuideModelIds())
            {
                if (modelId instanceof ModelIdentifier)
                    out.accept(modelId);
            }
        });
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> new GuideModel.Provider());
    }
}
