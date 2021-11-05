package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.w3c.dom.Element;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XPaths;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlRecipeDisplay;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideItemAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.NoGuideAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlButton;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlIfElse;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlImage;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlItemStack;
import io.github.daomephsta.mosaic.EdgeSpacing;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class V100ElementTypes
{
    static final XmlElementType<XmlImage> IMAGE = V100ElementTypes::image;
    static final Map<String, XmlHeadingElementType> HEADINGS = IntStream.rangeClosed(1, 6).collect(
        HashMap::new, (map, level) -> map.put("h" + level, new XmlHeadingElementType(level)), Map::putAll);
    static final XmlElementType<XmlItemStack> ITEMSTACK = V100ElementTypes::itemstack;
    static final XmlElementType<XmlEntityDisplay> ENTITY_DISPLAY = new XmlEntityDisplayElementType();
    static final XmlElementType<XmlRecipeDisplay> RECIPE_DISPLAY = V100ElementTypes::recipe;
    static final XmlElementType<XmlButton> BUTTON = new XmlButtonElementType();
    static final XmlElementType<XmlIfElse> IF_ELSE = V100ElementTypes::ifElse;

    static final XmlElementType<GuideItemAccessMethod> GUIDE_ITEM_ACCESS_METHOD = new GuideItemAccessMethodElementType();
    static final XmlElementType<NoGuideAccessMethod> NO_GUIDE_ACCESS_METHOD = xml -> new NoGuideAccessMethod();

    private static XmlImage image(Element xml) throws GuideLoadingException
    {
        XmlAttributes.requireAttributes(xml, "src", "alt_text", "width", "height");
        Identifier src = XmlAttributes.asIdentifier(xml, "src");
        String alt_text = XmlAttributes.getValue(xml, "alt_text");
        int width = XmlAttributes.asInt(xml, "width"),
            height = XmlAttributes.asInt(xml, "height");
        EdgeSpacing padding = LayoutParameters.readPadding(xml);
        EdgeSpacing margin = LayoutParameters.readMargin(xml);
        return new XmlImage(src, alt_text, width, height, padding, margin, LayoutParameters.readSize(xml));
    }

    private static XmlItemStack itemstack(Element xml) throws GuideLoadingException
    {
        try
        {
            String itemId = XmlAttributes.getValue(xml, "item");
            String itemString = itemId + XmlAttributes.getValue(xml, "tag", "");
            ItemStringReader itemReader = new ItemStringReader(new StringReader(itemString), false).consume();
            int amount = XmlAttributes.asInt(xml, "amount", 1);
            ItemStack stack = new ItemStack(itemReader.getItem(), amount);
            stack.setNbt(itemReader.getNbt());
            EdgeSpacing padding = LayoutParameters.readPadding(xml);
            EdgeSpacing margin = LayoutParameters.readMargin(xml);
            return new XmlItemStack(stack, padding, margin, LayoutParameters.readSize(xml));
        }
        catch (CommandSyntaxException e)
        {
            throw new InscribeSyntaxException("Failed to parse itemstack", e);
        }
    }

    private static XmlRecipeDisplay recipe(Element xml) throws GuideLoadingException
    {
        return new XmlRecipeDisplay(XmlAttributes.asIdentifier(xml, "id"));
    }

    private static XmlIfElse ifElse(Element xml) throws GuideLoadingException
    {
        Identifier condition = XmlAttributes.asIdentifier(xml, "condition");
        List<Object> trueBranch = V100Parser.ENTRY_DESERIALISER.deserialise(
            XPaths.nodes(xml, "./node()[not(self::else)]"));
        List<Object> falseBranch = V100Parser.ENTRY_DESERIALISER.deserialise(
            XPaths.nodes(xml, "./else/node()"));
        return new XmlIfElse(condition, trueBranch, falseBranch);
    }
}
