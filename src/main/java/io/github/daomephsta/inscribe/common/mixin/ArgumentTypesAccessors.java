package io.github.daomephsta.inscribe.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;

import net.minecraft.command.arguments.ArgumentTypes;

@Mixin(ArgumentTypes.class)
public class ArgumentTypesAccessors
{
    @Invoker
    public static <T extends ArgumentType<?>> void invokeToJson(JsonObject json, T argumentType)
    {
        throw new IllegalStateException("Dummy mixin body invoked");
    }
}
