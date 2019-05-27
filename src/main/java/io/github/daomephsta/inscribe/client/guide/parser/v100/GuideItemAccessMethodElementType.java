package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes.Preconditions;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideItemAccessMethod;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;

final class GuideItemAccessMethodElementType extends XmlElementType<GuideItemAccessMethod>
{
	private static final String ITEM_GROUP = "item_group",
								MODEL = "model";

	GuideItemAccessMethodElementType()
	{
		super("guide_item", GuideItemAccessMethod.class);
	}

	private static final Lazy<Map<String, ItemGroup>> ID_TO_GROUP = 
			new Lazy<>(() -> Arrays.stream(ItemGroup.GROUPS).collect(Collectors.toMap(ItemGroup::getId, ig -> ig)));
	
	@Override
	protected void configurePreconditions(Preconditions attributePreconditions)
	{
		attributePreconditions.required(ITEM_GROUP, MODEL);
	}
	
	@Override
	protected GuideItemAccessMethod translate(Element xml) throws InscribeSyntaxException
	{
		String itemGroupId = xml.getAttributeValue(ITEM_GROUP);
		ItemGroup itemGroup = ID_TO_GROUP.get().get(itemGroupId);
		if (itemGroup == null)
			throw new InscribeSyntaxException(itemGroupId + " is not a valid item group id");
		Identifier modelId = xml.getAttributeValue(MODEL).contains("#")
				? XmlAttributes.asModelIdentifier(xml, MODEL)
				: XmlAttributes.asIdentifier(xml, MODEL);
		return new GuideItemAccessMethod(itemGroup, modelId);
	}
}