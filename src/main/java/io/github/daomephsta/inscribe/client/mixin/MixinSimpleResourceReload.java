package io.github.daomephsta.inscribe.client.mixin;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.daomephsta.inscribe.client.guide.GuideManager;
import net.fabricmc.fabric.impl.resource.loader.FabricLifecycledResourceManager;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Unit;

@Mixin(SimpleResourceReload.class)
public abstract class MixinSimpleResourceReload implements ResourceReload
{
    @Unique
    private static final Logger INSCRIBE_LOGGER = LogManager.getLogger("Inscribe");

    @Inject(method = "start", at = @At("HEAD"))
    private static void inscribe_insertGuideManagerListener(
        ResourceManager manager, List<ResourceReloader> reloaders,
        Executor a, Executor b, CompletableFuture<Unit> c, boolean d, CallbackInfoReturnable<ResourceReload> info)
    {
        if (manager instanceof FabricLifecycledResourceManager flrm &&
            flrm.fabric_getResourceType() == ResourceType.CLIENT_RESOURCES)
        {
            ActionResult injectionResult = insertListenerBefore(
                reloaders, GuideManager.INSTANCE, BakedModelManager.class);
            if (injectionResult == ActionResult.SUCCESS)
                INSCRIBE_LOGGER.info("Registered Guide Manager as a resource reload listener");
            else if (injectionResult == ActionResult.FAIL)
            {
                INSCRIBE_LOGGER.error("Failed to register Guide Manager as a resource reload listener. "
                    + "Listeners: {}", reloaders);
            }
        }
    }

    @Unique
    private static ActionResult insertListenerBefore(List<ResourceReloader> listeners,
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
