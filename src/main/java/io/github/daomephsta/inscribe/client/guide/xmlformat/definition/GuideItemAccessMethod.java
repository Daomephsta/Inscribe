package io.github.daomephsta.inscribe.client.guide.xmlformat.definition;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.*;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Lazy;

public class GuideItemAccessMethod extends GuideAccessMethod implements IXmlRepresentation
{
	public static final XmlElementType<GuideItemAccessMethod> XML_TYPE = new XmlType();
	private final ItemGroup itemGroup;
	
	private GuideItemAccessMethod(ItemGroup itemGroup)
	{
		this.itemGroup = itemGroup;
	}
	
	public ItemGroup getItemGroup()
	{
		return itemGroup;
	}

	private static class XmlType extends XmlElementType<GuideItemAccessMethod>
	{
		private static final Lazy<Map<String, ItemGroup>> ID_TO_GROUP = 
			new Lazy<>(() -> Arrays.stream(ItemGroup.GROUPS).collect(Collectors.toMap(ItemGroup::getId, g -> g)));
		
		private XmlType()
		{
			super("guide_item", GuideItemAccessMethod.class);
		}

		@Override
		public GuideItemAccessMethod fromXml(Element xml, DeserialisationManager manager)
		{
			String itemGroupId = xml.getAttributeValue("item_group");
			ItemGroup itemGroup = ID_TO_GROUP.get().get(itemGroupId);
			if (itemGroup == null)
				throw new XmlSyntaxException(itemGroupId + " is not a valid item group id");
			
			return new GuideItemAccessMethod(itemGroup);
		}
	}
}