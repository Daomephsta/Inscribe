package io.github.daomephsta.inscribe.client;

import java.util.Optional;

import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.gui.RenderFormatConverter;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.xmlformat.theme.Theme;
import io.github.daomephsta.inscribe.common.guide.poster.PosterBlockEntity;
import io.github.daomephsta.mosaic.flow.Flow.Direction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

public class PosterBlockEntityRenderer extends BlockEntityRenderer<PosterBlockEntity>
{
    private final Framebuffer framebuffer;

    public PosterBlockEntityRenderer(BlockEntityRenderDispatcher renderDispatcher)
    {
        super(renderDispatcher);
        Window window = MinecraftClient.getInstance().getWindow();
        this.framebuffer = new Framebuffer(window.getFramebufferWidth(),
            window.getFramebufferHeight(), true, MinecraftClient.IS_SYSTEM_MAC);
        this.framebuffer.setClearColor(0F, 0F, 0F, 0F);
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

        Optional.ofNullable(GuideManager.INSTANCE.getGuide(new Identifier("test:bestiary")))
            .map(guide -> guide.getEntry(new Identifier("test:bestiary/passive/chicken")))
            .filter(entry -> entry.getPages().size() >= 1)
            .ifPresent(entry ->
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
                VertexConsumer buffer = vertices.getBuffer(RenderLayer.getText(Theme.DEFAULT.getGuiTexture()));
                buffer.vertex(model, minX, minY + height, 0)
                    .color(255, 255, 255, 255).texture(0, maxV).light(0xF000F0).next();
                buffer.vertex(model, minX + width, minY + height, 0)
                    .color(255, 255, 255, 255).texture(maxU, maxV).light(0xF000F0).next();
                buffer.vertex(model, minX + width, minY, 0)
                    .color(255, 255, 255, 255).texture(maxU, 0).light(0xF000F0).next();
                buffer.vertex(model, minX, minY, 0)
                    .color(255, 255, 255, 255).texture(0, 0).light(0xF000F0).next();
                matrices.translate(0, 0, -0.1);
                page.render(vertices, matrices, 0, 0, 0, false);
                matrices.pop();
            });
    }

    @Override
    public boolean rendersOutsideBoundingBox(PosterBlockEntity blockEntity)
    {
        return true;
    }
}
