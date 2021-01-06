package io.github.daomephsta.inscribe.client.guide.gui.widget;

import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay.Animation;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay.Revolve;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay.Transform;
import io.github.daomephsta.inscribe.common.util.Lighting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Quaternion;

public class EntityDisplayWidget extends GuideWidget
{
    private static final int MAX_LIGHT = Lighting.MAX;
    private static final Matrix3f IDENTITY_3F;
    static
    {
        IDENTITY_3F = new Matrix3f();
        IDENTITY_3F.loadIdentity();
    }
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
    public void renderWidget(VertexConsumerProvider vertices, MatrixStack matrices, int mouseX, int mouseY, float lastFrameDuration)
    {
        // Nasty hack to deal with mysterious Z axis inversion. Rendering is suffering...
        double zOffset = isOnGui(matrices) ? 100 : -1;

        matrices.push();
        matrices.translate(left() + width() / 2, bottom() - (entity.getHeight() * 30 / 2), zOffset);
        matrices.scale(1, 1, 0.001F);
        Quaternion rotation = transform.rotation.copy();
        applyTransform(matrices, rotation, transform);
        rotation.conjugate();
        matrices.multiply(rotation);
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
        entityRenderDispatcher.setRotation(rotation);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate entityVertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        matrices.translate(0, -entity.getHeight() / 2, 0);
        entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrices, entityVertexConsumers, MAX_LIGHT);
        entityVertexConsumers.draw();
        entityRenderDispatcher.setRenderShadows(true);
        matrices.pop();
    }

    private boolean isOnGui(MatrixStack matrices)
    {
        return matrices.peek().getNormal().equals(IDENTITY_3F);
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

    @Override
    public void dispose()
    {
        //Kill the entity, just in case
        entity.remove();
    }
}
