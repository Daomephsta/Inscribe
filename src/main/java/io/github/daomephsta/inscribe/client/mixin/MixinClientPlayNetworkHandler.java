package io.github.daomephsta.inscribe.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.daomephsta.inscribe.client.hooks.ClientPlayerJoinWorldCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.GameJoinS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler
{
    @Inject(method = "onGameJoin(Lnet/minecraft/client/network/packet/GameJoinS2CPacket;)V", at = @At("RETURN"))
    private void onPlayerJoinWorld(GameJoinS2CPacket joinPacket, CallbackInfo info)
    {
        ClientPlayerJoinWorldCallback.EVENT.invoker().invoke(MinecraftClient.getInstance().player);
    }
}
