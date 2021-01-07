package io.github.daomephsta.inscribe.client;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.gui.RenderFormatConverter;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.common.guide.poster.PosterBlockEntity;
import io.github.daomephsta.mosaic.flow.Flow.Direction;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

public class PosterBlockEntityRenderer extends BlockEntityRenderer<PosterBlockEntity>
{
    private RenderLayer renderLayer;

    public PosterBlockEntityRenderer(BlockEntityRenderDispatcher renderDispatcher)
    {
        super(renderDispatcher);
    }

    @Override
    public void render(PosterBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
        VertexConsumerProvider vertices, int light, int overlay)
    {
        if (!blockEntity.isRenderOrigin())
            return;
        net.minecraft.util.math.Direction facing = blockEntity.getCachedState().get(Properties.HORIZONTAL_FACING);
        float maxU = 381F / 440F, maxV = 232F / 256F,
              width = 381F, height = 232F,
              minX = (400F - width) / 2F,
              minY = (300F - height) / 2F;

        XmlEntry entry = getGuide().getEntry(new Identifier("test:bestiary/passive/chicken"));
        if (entry != null && entry.getPages().size() >= 1)
        {
            GuideFlow page = new GuideFlow(Direction.VERTICAL);
            page.padding().setAll(4);
            for (Object element : entry.getPages().get(0).getContent())
                RenderFormatConverter.convert(page, element);
            page.setLayoutParameters(25, 34, 176, 232);
            page.layoutChildren();

            matrices.push();
            matrices.translate(0.5, 0, 0.5);
            matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(facing.asRotation() + 180));
            matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
            matrices.translate(-0.5, -3, 6.85F / 16F);
            matrices.scale(0.01F, 0.01F, 0.01F);
            Matrix4f model = matrices.peek().getModel();
            VertexConsumer buffer = vertices.getBuffer(getRenderLayer());
            buffer.vertex(model, minX, minY + height, 0).texture(0, maxV).next();
            buffer.vertex(model, minX + width, minY + height, 0).texture(maxU, maxV).next();
            buffer.vertex(model, minX + width, minY, 0).texture(maxU, 0).next();
            buffer.vertex(model, minX, minY, 0).texture(0, 0).next();
            matrices.translate(0, 0, -0.1);
            page.render(vertices, matrices, 0, 0, 0, false);
            matrices.pop();
        }
    }

    private Guide getGuide()
    {
        return GuideManager.INSTANCE.getGuide(new Identifier("test:bestiary"));
    }

    private RenderLayer getRenderLayer()
    {
        if (renderLayer == null)
            renderLayer = InscribeRenderLayers.textureQuads(getGuide().getTheme().getGuiTexture());
        return renderLayer;
    }

    @Override
    public boolean rendersOutsideBoundingBox(PosterBlockEntity blockEntity)
    {
        return true;
    }
}
