package io.github.daomephsta.inscribe.client.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.daomephsta.inscribe.client.mixinimpl.MixinImplReloadableResourceManagerImpl;
import net.minecraft.resource.*;
import net.minecraft.util.Void;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class MixinReloadableResourceManagerImpl implements ReloadableResourceManager
{
	@Shadow
	private List<ResourceReloadListener> listeners;
	@Shadow
	private List<ResourceReloadListener> initialListeners;

	@Inject(method = "beginReloadInner(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Ljava/util/concurrent/CompletableFuture;)Lnet/minecraft/resource/ResourceReloadMonitor;", at = @At("HEAD"))
	public void inscribe_beginReloadInner(Executor loadExecutor, Executor applyExecutor, List<ResourceReloadListener> listeners, CompletableFuture<Void> future, CallbackInfoReturnable<CompletableFuture<Void>> info)
	{
	    MixinImplReloadableResourceManagerImpl.injectGuideManagerAsListener(initialListeners, listeners);
	}
}
