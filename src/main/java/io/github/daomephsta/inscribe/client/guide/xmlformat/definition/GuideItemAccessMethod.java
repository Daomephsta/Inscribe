package io.github.daomephsta.inscribe.client.guide.xmlformat.definition;

import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

public class GuideItemAccessMethod extends GuideAccessMethod
{
    private final ItemGroup itemGroup;
    private final Identifier modelId;

    public GuideItemAccessMethod(ItemGroup itemGroup, Identifier modelId)
    {
        this.itemGroup = itemGroup;
        this.modelId = modelId;
    }

    public ItemGroup getItemGroup()
    {
        return itemGroup;
    }

    public Identifier getModelId()
    {
        return modelId;
    }
}