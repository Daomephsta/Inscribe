package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideItemAccessMethod;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

final class GuideItemAccessMethodElementType extends XmlElementType<GuideItemAccessMethod>
{
    private static final String ITEM_GROUP = "item_group",
                                MODEL = "model";
    private static final Map<String, ItemGroup> ID_TO_GROUP = new HashMap<>();

    GuideItemAccessMethodElementType()
    {
        super("guide_item");
    }

    @Override
    public GuideItemAccessMethod fromXml(Element xml) throws GuideLoadingException
    {
        XmlAttributes.requireAttributes(xml, ITEM_GROUP, MODEL);
        String itemGroupId = xml.getAttribute(ITEM_GROUP);
        ItemGroup itemGroup = ID_TO_GROUP.computeIfAbsent(itemGroupId, id ->
        {
            for (ItemGroup group : ItemGroup.GROUPS)
            {
                if (group.getName().equals(itemGroupId))
                    return group;
            }
            return null;
        });
        if (itemGroup == null)
            throw new InscribeSyntaxException(itemGroupId + " is not a valid item group id");
        Identifier modelId = xml.getAttribute(MODEL).contains("#")
                ? XmlAttributes.asModelIdentifier(xml, MODEL)
                : XmlAttributes.asIdentifier(xml, MODEL);
        return new GuideItemAccessMethod(itemGroup, modelId);
    }
}