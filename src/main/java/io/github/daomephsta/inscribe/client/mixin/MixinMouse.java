package io.github.daomephsta.inscribe.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.daomephsta.inscribe.client.mixinimpl.InputCallbackDispatcher;
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public class MixinMouse
{
    @Inject(method = "onMouseButton", at = @At("RETURN"))
    private void inscribe_onMouseButton(long windowId, int button, int action, int modifiers, CallbackInfo info)
    {
        InputCallbackDispatcher.inscribe_onMouseButton(windowId, button, action, modifiers);
    }
}
