package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import org.apache.logging.log4j.Logger;

import io.github.daomephsta.inscribe.client.guide.gui.widget.EntityDisplayWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlGuideGuiElement;
import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.mosaic.EdgeSpacing;
import io.github.daomephsta.mosaic.Size;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

public record XmlEntityDisplay(
    Identifier entityId,
    NbtCompound nbt,    
    Transform transform,
    Animation animation,
    EdgeSpacing padding,
    EdgeSpacing margin, 
    Size size          
)
implements XmlGuideGuiElement
{
    private static final Logger LOGGER = Inscribe.getDedicatedLogger();
    
    public record Transform(Vec3f translation, Quaternion rotation, float scale)
    {
        public static final Transform NONE = new Transform(Vec3f.ZERO, Quaternion.IDENTITY, 1.0F);
    }

    public static interface Animation
    {
        public static final Animation NONE = new Animation() {};
    }

    public record Revolve(Vec3f axis, float speed) implements Animation {}

    @Override
    public void acceptPage(GuideFlow output)
    {
         Entity entity;
         try
         {
             entity = Registry.ENTITY_TYPE.get(entityId).create(MinecraftClient.getInstance().world);
             entity.readNbt(nbt);
         }
         catch (Exception e)
         {
             LOGGER.error("Error creating entity {}{} {}", entityId, nbt, e);
             return;
         }
         EntityDisplayWidget widget = new EntityDisplayWidget(entity, transform, animation);
         widget.setPadding(padding);
         widget.setMargin(margin);
         addWidget(output, widget, size);
    }
    
    public Entity createEntity()
    {
        try
        {
            Entity entity = Registry.ENTITY_TYPE.get(entityId).create(MinecraftClient.getInstance().world);
            entity.readNbt(nbt);
            return entity;
        }
        catch (Exception e)
        {
            LOGGER.error("Error creating entity {}{} {}", entityId, nbt, e);
            return null;
        }
    }
}
