package io.github.daomephsta.inscribe.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.daomephsta.inscribe.client.mixinimpl.InputCallbackDispatcher;
import net.minecraft.client.Keyboard;

@Mixin(Keyboard.class)
public class MixinKeyboard
{
    @Inject(method = "onKey", at = @At("RETURN"))
    private void inscribe_onKey(long windowId, int key, int scancode, int action, int modifiers, CallbackInfo info)
    {
        InputCallbackDispatcher.inscribe_onKey(windowId, key, scancode, action, modifiers);
    }
}
