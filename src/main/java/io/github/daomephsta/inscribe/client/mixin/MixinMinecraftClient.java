package io.github.daomephsta.inscribe.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.daomephsta.inscribe.client.hooks.ClientStartCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient
{
    @Inject(method = "<init>(Lnet/minecraft/client/RunArgs;)V", at = @At("RETURN"))
    private void onClientStart(RunArgs runArgs, CallbackInfo info)
    {
        ClientStartCallback.EVENT.invoker().invoke((MinecraftClient)(Object)this);
    }
}
