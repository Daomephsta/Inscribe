package io.github.daomephsta.inscribe.client.guide;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;

import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideItemAccessMethod;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class GuideModel
{
    public static class Provider implements ModelResourceProvider
    {
        private static final Identifier GUIDE_MODEL_ID = new Identifier(Inscribe.MOD_ID, "item/guide");

        @Override
        public UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context)
            throws ModelProviderException
        {
            return resourceId.equals(GUIDE_MODEL_ID) ? new GuideModel.Unbaked() : null;
        }
    }

    private static class Unbaked implements UnbakedModel
    {
        @Override
        public Collection<Identifier> getModelDependencies()
        {
            return GuideManager.INSTANCE.getGuideModelIds();
        }

        @Override
        public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> modelGetter, Set<Pair<String, String>> errors)
        {
            return GuideManager.INSTANCE.streamGuideModelIds()
                .flatMap(model -> modelGetter.apply(model).getTextureDependencies(modelGetter, errors).stream())
                .collect(toSet());
        }

        @Override
        public BakedModel bake(ModelLoader modelLoader, Function<SpriteIdentifier, Sprite> spriteGetter, ModelBakeSettings bakeSettings, Identifier var4)
        {
            Map<Identifier, BakedModel> modelMap = GuideManager.INSTANCE.streamGuideModelIds()
                .collect(toMap(id -> id, id -> modelLoader.bake(id, bakeSettings)));
            ModelOverrideList overrides = new Overrides(modelLoader, modelMap);
            return new Baked(overrides);
        }
    }

    private static class Overrides extends ModelOverrideList
    {
        private final Map<Identifier, BakedModel> modelMap;
        private final BakedModel missingModel;

        public Overrides(ModelLoader modelLoader, Map<Identifier, BakedModel> modelMap)
        {
            super(modelLoader, null, id -> null, Collections.emptyList());
            this.modelMap = modelMap;
            this.missingModel = modelLoader.bake(ModelLoader.MISSING_ID, ModelRotation.X0_Y0);
        }
        
        

        @Override
        public BakedModel apply(BakedModel baseModel, ItemStack stack, 
            ClientWorld world, LivingEntity entity, int seed)
        {
            Guide guide = Inscribe.GUIDE_ITEM.getGuide(stack);
            if (!guide.isValid())
                return missingModel;

            GuideAccessMethod accessMethod = guide.getAccessMethod();
            if (accessMethod instanceof GuideItemAccessMethod)
                return modelMap.get(((GuideItemAccessMethod) accessMethod).getModelId());
            else
                return missingModel;
        }
    }

    public static class Baked implements BakedModel
    {
        private final ModelOverrideList overrides;

        public Baked(ModelOverrideList overrides)
        {
            this.overrides = overrides;
        }

        //Only this method matters, since it redirects to the correct model
        @Override
        public ModelOverrideList getOverrides()
        {
            return overrides;
        }

        @Override
        public List<BakedQuad> getQuads(BlockState var1, Direction var2, Random var3)
        {
            return Collections.emptyList();
        }

        @Override
        public boolean useAmbientOcclusion()
        {
            return false;
        }

        @Override
        public boolean hasDepth()
        {
            return false;
        }

        @Override
        public boolean isBuiltin()
        {
            return false;
        }

        @Override
        public Sprite getSprite()
        {
            return null;
        }

        @Override
        public ModelTransformation getTransformation()
        {
            return ModelTransformation.NONE;
        }

        @Override
        public boolean isSideLit()
        {
            return false;
        }
    }
}
