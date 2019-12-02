package io.github.daomephsta.inscribe.client.guide.parser.v100;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlElements;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay.Animation;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay.Transform;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

final class XmlEntityDisplayElementType extends XmlElementType<XmlEntityDisplay>
{
    XmlEntityDisplayElementType()
    {
        super("entity_display", XmlEntityDisplay.class);
    }

    @Override
    public XmlEntityDisplay fromXml(Element xml) throws GuideLoadingException
    {
        try
        {
            Identifier entityId = XmlAttributes.asIdentifier(xml, "entity");
            if (!Registry.ENTITY_TYPE.containsId(entityId))
                throw new InscribeSyntaxException("Unknown entity id " + entityId);
            Attr tagAttr = xml.getAttributeNode("tag");
            CompoundTag nbt = tagAttr != null
                ? StringNbtReader.parse(tagAttr.getValue())
                : new CompoundTag();
            Transform transform = readTransform(XmlElements.getChildNullable(xml, "transform"));
            Animation animation = readAnimation(XmlElements.getChildNullable(xml, "animation"));
            boolean lighting = XmlAttributes.asBoolean(xml, "lighting", true);
            return new XmlEntityDisplay(entityId, nbt, transform, animation, lighting);
        }
        catch (CommandSyntaxException e)
        {
            throw new InscribeSyntaxException("Failed to parse entity tag", e);
        }
    }

    private Transform readTransform(Element xml) throws InscribeSyntaxException
    {
        if (xml == null)
            return Transform.NONE;
        Vector3f translation = XmlElements.asVector3f(xml, "translate", Transform.NONE.translation);
        Vector3f rotation = XmlElements.asVector3f(xml, "rotate", Transform.NONE.rotation);
        float scale = XmlAttributes.asFloat(XmlElements.getChild(xml, "scale"), "s", 1.0F);
        return new Transform(translation, rotation, scale);
    }

    private Animation readAnimation(Element xml) throws InscribeSyntaxException
    {
        if (xml == null)
            return Animation.NONE;
        Element element = XmlElements.getChildNullable(xml, "rotate_around");
        if (element != null)
        {
            XmlAttributes.requireAttributes(element, "axis", "speed");
            String axis = element.getAttribute("axis");
            Vector3f axisVector;
            if (axis.equals("x"))
                axisVector = new Vector3f(1.0F, 0.0F, 0.0F);
            else if (axis.equals("y"))
                axisVector = new Vector3f(0.0F, 1.0F, 0.0F);
            else if (axis.equals("z"))
                axisVector = new Vector3f(0.0F, 0.0F, 1.0F);
            else
                throw new InscribeSyntaxException("Axis must be x, y, or z");
            return new XmlEntityDisplay.Revolve(axisVector, XmlAttributes.asFloat(element, "speed"));
        }
        return Animation.NONE;
    }
}