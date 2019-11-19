package io.github.daomephsta.inscribe.client.mixinimpl;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.daomephsta.inscribe.client.guide.GuideManager;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.ActionResult;

public class MixinImplReloadableResourceManagerImpl
{
	private static final Logger INSCRIBE_LOGGER = LogManager.getLogger();
    private final ResourceType type;

	public MixinImplReloadableResourceManagerImpl(ResourceType type)
    {
        this.type = type;
    }

    public void inscribe_beginReloadInner(Executor loadExecutor, Executor applyExecutor, List<ResourceReloadListener> listeners, CompletableFuture<Void> future, CallbackInfoReturnable<CompletableFuture<Void>> info)
    {
        if (type == ResourceType.CLIENT_RESOURCES)
        {
            ActionResult injectionResult = injectListenerBefore(listeners, GuideManager.INSTANCE, BakedModelManager.class);
            if (injectionResult == ActionResult.SUCCESS)
                INSCRIBE_LOGGER.info("[Inscribe] Registered Guide Manager as a resource reload listener");
            else if (injectionResult == ActionResult.FAIL)
                INSCRIBE_LOGGER.error("[Inscribe] Failed to register Guide Manager as a resource reload listener. Listeners: {}", listeners);
        }
    }

    private ActionResult injectListenerBefore(List<ResourceReloadListener> listeners, ResourceReloadListener listener, Class<? extends ResourceReloadListener> targetClass)
	{
		int targetIndex = -1;
		int i = 0;
		Iterator<ResourceReloadListener> iter = listeners.iterator();
		while(iter.hasNext())
		{
		    ResourceReloadListener current = iter.next();
			if (current == listener)
				return ActionResult.PASS;
			else if (targetClass.isInstance(current))
			{
				if (targetIndex < 0)
					targetIndex = i;
				else
					throw new IllegalStateException("[Inscribe] Did not expect multiple instances of " + targetClass.getName() + " in resource reload listener list");
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
