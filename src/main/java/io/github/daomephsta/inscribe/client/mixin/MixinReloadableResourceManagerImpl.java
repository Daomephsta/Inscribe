package io.github.daomephsta.inscribe.client.mixin;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.daomephsta.inscribe.client.guide.GuideManager;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Unit;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class MixinReloadableResourceManagerImpl implements ReloadableResourceManager
{
    @Shadow
    private @Final ResourceType type;
    @Shadow
    private @Final List<ResourceReloader> reloaders;
    @Unique
    private static final Logger LOGGER = LogManager.getLogger();

    @Inject(at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;isDebugEnabled()Z", remap = false), method = "reload")
    public void inscribe_reload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs, CallbackInfoReturnable<ResourceReload> info)
    {
        if (type == ResourceType.CLIENT_RESOURCES)
        {
            ActionResult injectionResult = injectListenerBefore(
                reloaders, GuideManager.INSTANCE, BakedModelManager.class);
            if (injectionResult == ActionResult.SUCCESS)
                LOGGER.info("[Inscribe] Registered Guide Manager as a resource reload listener");
            else if (injectionResult == ActionResult.FAIL)
            {
                LOGGER.error("[Inscribe] Failed to register Guide Manager as a resource reload listener. "
                    + "Listeners: {}", reloaders);
            }
        }
    }

    @Unique
    private ActionResult injectListenerBefore(List<ResourceReloader> listeners, 
        ResourceReloader listener, Class<? extends ResourceReloader> targetClass)
    {
        int targetIndex = -1;
        int i = 0;
        Iterator<ResourceReloader> iter = listeners.iterator();
        while(iter.hasNext())
        {
            ResourceReloader current = iter.next();
            if (current == listener)
                return ActionResult.PASS;
            else if (targetClass.isInstance(current))
            {
                if (targetIndex < 0)
                    targetIndex = i;
                else
                {
                    throw new IllegalStateException("[Inscribe] Did not expect multiple instances of " + 
                        targetClass.getName() + " in resource reload listener list");
                }
            }
            i++;
        }
        if (targetIndex < 0)
            return ActionResult.FAIL;
        else
        {
            listeners.add(targetIndex, listener);
            return ActionResult.SUCCESS;
        }
    }
}
