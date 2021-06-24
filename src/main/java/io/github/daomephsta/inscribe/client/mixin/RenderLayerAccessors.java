package io.github.daomephsta.inscribe.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;

@Mixin(RenderLayer.class)
public interface RenderLayerAccessors
{
    @Invoker("of")
    public static RenderLayer.MultiPhase create(String name, VertexFormat format, VertexFormat.DrawMode drawMode, 
        int expectedBufferSize, RenderLayer.MultiPhaseParameters phaseData)
    {
        throw new IllegalStateException("Invoker stub invoked!");
    }
}
