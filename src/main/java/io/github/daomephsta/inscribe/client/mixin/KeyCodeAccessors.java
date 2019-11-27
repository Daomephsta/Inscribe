package io.github.daomephsta.inscribe.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.KeyCode;

@Mixin(KeyCode.class)
public interface KeyCodeAccessors
{
    @Accessor
    public InputUtil.Type getType();
}
