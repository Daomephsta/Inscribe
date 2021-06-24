package io.github.daomephsta.inscribe.client;

import io.github.daomephsta.inscribe.client.mixin.RenderLayerAccessors;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class InscribeRenderLayers extends RenderLayer
{
    private InscribeRenderLayers(String a, VertexFormat b, DrawMode c, int d, boolean e, boolean f, Runnable g, Runnable h)
    {
        super(a, b, c, d, e, f, g, h);
    }

    public static final RenderLayer COLOUR_QUADS;
    static
    {
        MultiPhaseParameters multiPhaseParameters = MultiPhaseParameters.builder()
            .shader(RenderPhase.COLOR_SHADER)
            .cull(RenderPhase.DISABLE_CULLING)
            .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
            .build(false);
        COLOUR_QUADS = RenderLayerAccessors.create(Inscribe.MOD_ID + ":colour_quads", 
            VertexFormats.POSITION_COLOR, DrawMode.QUADS, 256, multiPhaseParameters);
    }

    public static RenderLayer textureQuads(Identifier texture)
    {
        MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
            .shader(RenderPhase.POSITION_TEXTURE_SHADER)
            .texture(new RenderPhase.Texture(texture, false, false))
            .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
            .build(false);
        return RenderLayerAccessors.create(Inscribe.MOD_ID + ":texture_quads", 
            VertexFormats.POSITION_TEXTURE, DrawMode.QUADS, 256, multiPhaseParameters);
    }
}
