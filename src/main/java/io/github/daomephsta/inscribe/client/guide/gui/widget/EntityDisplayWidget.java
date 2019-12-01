package io.github.daomephsta.inscribe.client.guide.gui.widget;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay.Animation;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay.Revolve;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay.Transform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;

public class EntityDisplayWidget extends GuideWidget
{
    private final Entity entity;
    private final XmlEntityDisplay.Transform transform;
    private final XmlEntityDisplay.Animation animation;
    private final boolean lighting;

    public EntityDisplayWidget(Entity entity, Transform transform, Animation animation, boolean lighting)
    {
        this.entity = entity;
        this.transform = transform;
        this.animation = animation;
        this.lighting = lighting;
    }

    @Override
    public void renderWidget(int mouseX, int mouseY, float lastFrameDuration)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(left() + width() / 2F, bottom(), 100.0F);
        transform();
        animate();
        if (lighting) GuiLighting.enable();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderManager();
        entityRenderDispatcher.method_3945(180.0F);
        entityRenderDispatcher.setRenderShadows(false);
        //true = Don't render bounding boxes
        entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.5F, true);
        entityRenderDispatcher.setRenderShadows(true);
        GlStateManager.popMatrix();
        if (lighting) GuiLighting.disable();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }

    private void transform()
    {
        GlStateManager.scalef(-30 * transform.scale, 30 * transform.scale, 30 * transform.scale);
        GlStateManager.translatef(transform.translation.getX(), transform.translation.getY(), transform.translation.getZ());
        GlStateManager.rotatef(transform.rotation.getX(), 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef(transform.rotation.getY(), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(180.0F + transform.rotation.getZ(), 0.0F, 0.0F, 1.0F);
    }

    private void animate()
    {
        if (animation instanceof Revolve)
        {
            Revolve revolve = (Revolve) animation;
            GlStateManager.translatef(0.0F, entity.getHeight() / 2, 0.0F);
            float angle = (System.currentTimeMillis() % (int) Math.ceil(5000 / revolve.speed)) / (5000.0F / revolve.speed) * 360.0F;
            GlStateManager.rotatef(angle, revolve.axis.getX(), revolve.axis.getY(), revolve.axis.getZ());
            GlStateManager.translatef(0.0F, -entity.getHeight() / 2, 0.0F);
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
