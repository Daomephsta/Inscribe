package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlGuideGuiElement;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class XmlEntityDisplay implements XmlGuideGuiElement
{
    public final Identifier entityId;
    public final CompoundTag nbt;
    public final Transform transform;
    public final Animation animation;
    public final boolean lighting;

    public XmlEntityDisplay(Identifier entityId, CompoundTag nbt, Transform transform, Animation animation, boolean lighting)
    {
        this.entityId = entityId;
        this.nbt = nbt;
        this.transform = transform;
        this.animation = animation;
        this.lighting = lighting;
    }

    public static class Transform
    {
        public static final Transform NONE = new Transform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1.0F);
        public final Vector3f translation,
                              rotation;
        public final float scale;

        public Transform(Vector3f translation, Vector3f rotation, float scale)
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
