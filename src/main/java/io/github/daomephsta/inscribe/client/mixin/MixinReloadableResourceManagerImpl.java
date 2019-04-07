package io.github.daomephsta.inscribe.client.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.daomephsta.inscribe.client.guide.GuideManager;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.*;
import net.minecraft.util.Void;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class MixinReloadableResourceManagerImpl implements ReloadableResourceManager
{
	@Shadow
	private List<ResourceReloadListener> listeners;
	@Shadow
	private List<ResourceReloadListener> initialListeners;

	private static final Logger INSCRIBE_LOGGER = LogManager.getLogger();
	
	@Inject(method = "beginReloadInner(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Ljava/util/concurrent/CompletableFuture;)Lnet/minecraft/resource/ResourceReloadMonitor;", at = @At("HEAD"))
	public void inscribe_beginReloadInner(Executor loadExecutor, Executor applyExecutor, List<ResourceReloadListener> listeners, CompletableFuture<Void> future, CallbackInfoReturnable<CompletableFuture<Void>> info)
	{
		injectGuideManagerAsListener();
	}

	public void injectGuideManagerAsListener()
	{
		boolean successInitialListeners = injectListenerBefore(initialListeners, GuideManager.INSTANCE, BakedModelManager.class);
		boolean successListeners = injectListenerBefore(listeners, GuideManager.INSTANCE, BakedModelManager.class);
		if (successInitialListeners || successListeners)
			INSCRIBE_LOGGER.info("[Inscribe] Registered Guide Manager as a resource reload listener");
	}
	
	private boolean injectListenerBefore(List<ResourceReloadListener> listeners, ResourceReloadListener listener, Class<? extends ResourceReloadListener> targetClass)
	{
		int targetIndex = -1;
		for (int i = 0; i < listeners.size(); i++)
		{
			if (listeners.get(i) == listener)
				listeners.remove(i);
			else if (targetClass.isInstance(listeners.get(i)))
			{
				if (targetIndex < 0)
					targetIndex = i;
				else
					throw new IllegalStateException("[Inscribe] Did not expect multiple instances of " + targetClass.getName() + " in resource reload listener list");
			}
		}
		if (targetIndex < 0)
			return false;
		else
		{
			listeners.add(targetIndex, listener);
			return true;
		}
	}
}
