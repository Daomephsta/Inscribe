package io.github.daomephsta.inscribe.common.guide.xmlformat;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeXmlParseException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.util.XmlItemStack;
import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.util.bytebuf.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CommonGuideDefinition
{
	private final Identifier id;
	private final ItemSpecification itemSpecification;

	public CommonGuideDefinition(Identifier id, ItemSpecification itemSpecification)
	{
		this.id = id;
		this.itemSpecification = itemSpecification;
	}

	public static CommonGuideDefinition fromXml(Element xml)
	{
		Identifier guideId = Inscribe.ELEMENT_HELPER.asIdentifier(xml, "id");
		ItemSpecification itemSpecification = getItemSpecification(xml.getChild("item", Inscribe.XML_NAMESPACE));
		CommonGuideDefinition guideDefinitionCommon = new CommonGuideDefinition(guideId, itemSpecification);
		return guideDefinitionCommon;
	}
	
	private static ItemSpecification getItemSpecification(Element xml)
	{
		Element element;
		if ((element = xml.getChild("standard", Inscribe.XML_NAMESPACE)) != null)
		{
			return new ItemSpecification.Standard(element.getAttributeValue("item_group"));
		}
		else if ((element = xml.getChild("custom", Inscribe.XML_NAMESPACE)) != null)
		{
			ItemStack stack = XmlItemStack.fromXml(element).getStack();
			return new ItemSpecification.Custom(stack );
		}
		else if (xml.getChild("none", Inscribe.XML_NAMESPACE) != null)
		{
			return new ItemSpecification.None();
		}
		else
			throw new InscribeXmlParseException("Unknown item specification " + xml.getChildren()); 
	}

	public Identifier getGuideId()
	{
		return id;
	}

	public ItemSpecification getItemSpecification()
	{
		return itemSpecification;
	}
	
	@Override
	public String toString()
	{
		return String.format("GuideDefinitionCommon [id=%s, itemSpecifier=%s]", id, itemSpecification);
	}
	
	public static class Converter implements ByteBufConverter<CommonGuideDefinition>
	{
		@Override
		public CommonGuideDefinition read(PacketByteBuf buffer, Class<CommonGuideDefinition> target, ByteBufToObjectConverter converter)
		{
			Identifier id = buffer.readIdentifier();
			ItemSpecification itemSpecification = converter.readFromByteBuffer(buffer, ItemSpecification.class);
			return new CommonGuideDefinition(id, itemSpecification);
		}

		@Override
		public void write(PacketByteBuf buffer, CommonGuideDefinition definition, ObjectToByteBufConverter converter)
		{
			buffer.writeIdentifier(definition.getGuideId());
			converter.writeToByteBuffer(buffer, definition.itemSpecification, ItemSpecification.class);
		}
	}
}
