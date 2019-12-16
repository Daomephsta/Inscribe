package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlGuideGuiElement;
import io.github.daomephsta.mosaic.EdgeSpacing;
import io.github.daomephsta.mosaic.Size;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

public class XmlEntityDisplay implements XmlGuideGuiElement
{
    public final Identifier entityId;
    public final CompoundTag nbt;
    public final Transform transform;
    public final Animation animation;
    public final EdgeSpacing padding,
                              margin;
    public final Size size;

    public XmlEntityDisplay(Identifier entityId, CompoundTag nbt, Transform transform, Animation animation, EdgeSpacing padding, EdgeSpacing margin, Size size)
    {
        this.entityId = entityId;
        this.nbt = nbt;
        this.transform = transform;
        this.animation = animation;
        this.padding = padding;
        this.margin = margin;
        this.size = size;
    }

    public static class Transform
    {
        public static final Transform NONE = new Transform(new Vector3f(), Quaternion.IDENTITY, 1.0F);
        public final Vector3f translation;
        public final Quaternion rotation;
        public final float scale;

        public Transform(Vector3f translation, Quaternion rotation, float scale)
        {
            this.translation = translation;
            this.rotation = rotation;
            this.scale = scale;
        }
    }

    public static interface Animation
    {
        public static final Animation NONE = new Animation() {};
    }

    public static class Revolve implements Animation
    {
        public final Vector3f axis;
        public final float speed;

        public Revolve(Vector3f axis, float speed)
        {
            this.axis = axis;
            this.speed = speed;
        }
    }
}
