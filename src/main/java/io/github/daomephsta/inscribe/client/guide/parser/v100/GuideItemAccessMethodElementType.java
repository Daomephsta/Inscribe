package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideItemAccessMethod;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;

final class GuideItemAccessMethodElementType extends XmlElementType<GuideItemAccessMethod>
{
	GuideItemAccessMethodElementType()
	{
		super("guide_item", GuideItemAccessMethod.class);
	}

	private static final Lazy<Map<String, ItemGroup>> ID_TO_GROUP = 
			new Lazy<>(() -> Arrays.stream(ItemGroup.GROUPS).collect(Collectors.toMap(ItemGroup::getId, ig -> ig)));
	
	@Override
	public GuideItemAccessMethod fromXml(Element xml)
	{
		String itemGroupId = xml.getAttributeValue("item_group");
		ItemGroup itemGroup = ID_TO_GROUP.get().get(itemGroupId);
		if (itemGroup == null)
			throw new InscribeSyntaxException(itemGroupId + " is not a valid item group id");
		String modelIdString = xml.getAttributeValue("model");
		Identifier modelId = modelIdString.contains("#") ? new ModelIdentifier(modelIdString) : new Identifier(modelIdString);
		return new GuideItemAccessMethod(itemGroup, modelId);
	}
}