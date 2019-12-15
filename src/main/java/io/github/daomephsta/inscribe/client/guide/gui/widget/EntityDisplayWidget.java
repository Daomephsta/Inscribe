package io.github.daomephsta.inscribe.client.guide.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay.Animation;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay.Revolve;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay.Transform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Quaternion;

public class EntityDisplayWidget extends GuideWidget
{
    //Block and sky light of 15 packed into a single int
    private static final int MAX_LIGHT = 0xF000F0;
    private final Entity entity;
    private final XmlEntityDisplay.Transform transform;
    private final XmlEntityDisplay.Animation animation;

    public EntityDisplayWidget(Entity entity, Transform transform, Animation animation)
    {
        this.entity = entity;
        this.transform = transform;
        this.animation = animation;
    }

    @Override
    public void renderWidget(int mouseX, int mouseY, float lastFrameDuration)
    {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(left() + width() / 2, bottom() - (entity.getHeight() * 30 / 2), 1050.0F);
        MatrixStack matrixStack = new MatrixStack();
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        Quaternion rotation = transform.rotation.copy();
        applyTransform(matrixStack, rotation, transform);
        rotation.conjugate();
        matrixStack.multiply(rotation);
        DiffuseLighting.enableForLevel(matrixStack.peek().getModel());
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
        entityRenderDispatcher.setRotation(rotation);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate entityVertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        matrixStack.translate(0, -entity.getHeight() / 2, 0);
        entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, entityVertexConsumers, MAX_LIGHT);
        entityVertexConsumers.draw();
        entityRenderDispatcher.setRenderShadows(true);
        RenderSystem.popMatrix();
    }

    private void applyTransform(MatrixStack matrixStack, Quaternion rotation, Transform transform)
    {
        matrixStack.translate(transform.translation.getX(), transform.translation.getY(), transform.translation.getZ() + 1000.0D);
        matrixStack.scale(30 * transform.scale, -30 * transform.scale, 30 * transform.scale);
        if (animation instanceof Revolve)
        {
            Revolve revolve = (Revolve) animation;
            float angle = (System.currentTimeMillis() % (int) Math.ceil(5000 / revolve.speed)) / (5000.0F / revolve.speed) * 360.0F;
            rotation.hamiltonProduct(revolve.axis.getDegreesQuaternion(angle));
        }
    }

    @Override
    public int hintHeight()
    {
        return (int) Math.ceil(32 * entity.getHeight() * transform.scale);
    }

    @Override
    public int hintWidth()
    {
        return (int) Math.ceil(32 * entity.getWidth() * transform.scale);
    }
}
