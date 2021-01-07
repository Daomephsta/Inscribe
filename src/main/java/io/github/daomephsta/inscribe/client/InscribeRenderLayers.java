package io.github.daomephsta.inscribe.client;

import org.lwjgl.opengl.GL11;

import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class InscribeRenderLayers extends RenderLayer
{
    private InscribeRenderLayers(String a, VertexFormat b, int c, int d, boolean e, boolean f, Runnable g, Runnable h)
    {
        super(a, b, c, d, e, f, g, h);
    }

    public static final RenderLayer COLOUR_QUADS;
    static
    {
        MultiPhaseParameters multiPhaseParameters = MultiPhaseParameters.builder().cull(RenderPhase.DISABLE_CULLING).build(false);
        COLOUR_QUADS = RenderLayer.of(Inscribe.MOD_ID + ":colour_quads", VertexFormats.POSITION_COLOR,
            GL11.GL_QUADS, 256, multiPhaseParameters);
    }

    public static RenderLayer textureQuads(Identifier texture)
    {
        MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
            .texture(new RenderPhase.Texture(texture, false, false))
            .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
            .build(false);
        return RenderLayer.of(Inscribe.MOD_ID + ":texture_quads", VertexFormats.POSITION_TEXTURE, GL11.GL_QUADS, 256, multiPhaseParameters);
    }
}
