package io.github.daomephsta.inscribe.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.daomephsta.inscribe.client.hooks.ClientStartCallback;
import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient
{
    @Inject(method = "init()V", at = @At("RETURN"))
    private void onClientStart(CallbackInfo info)
    {
        ClientStartCallback.EVENT.invoker().invoke((MinecraftClient)(Object)this);
    }
}
