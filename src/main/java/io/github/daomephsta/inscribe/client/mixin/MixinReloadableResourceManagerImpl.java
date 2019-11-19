package io.github.daomephsta.inscribe.client.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.daomephsta.inscribe.client.mixinimpl.MixinImplReloadableResourceManagerImpl;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceType;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class MixinReloadableResourceManagerImpl implements ReloadableResourceManager
{
    @Unique
    private MixinImplReloadableResourceManagerImpl impl;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(ResourceType type, Thread thread, CallbackInfo info)
	{
	    this.impl = new MixinImplReloadableResourceManagerImpl(type);
	}

	@Inject(method = "beginReloadInner(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Ljava/util/concurrent/CompletableFuture;)Lnet/minecraft/resource/ResourceReloadMonitor;", at = @At("HEAD"))
	public void inscribe_beginReloadInner(Executor loadExecutor, Executor applyExecutor, List<ResourceReloadListener> listeners, CompletableFuture<Void> future, CallbackInfoReturnable<CompletableFuture<Void>> info)
	{
	    impl.inscribe_beginReloadInner(loadExecutor, applyExecutor, listeners, future, info);
	}
}
