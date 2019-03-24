package io.github.daomephsta.inscribe.common.guide;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeXmlParseException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.util.XmlItemStack;
import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.util.bytebuf.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class GuideDefinitionCommon
{
	private final Identifier id;
	private final ItemSpecification itemSpecification;

	public GuideDefinitionCommon(Identifier id, ItemSpecification itemSpecification)
	{
		this.id = id;
		this.itemSpecification = itemSpecification;
	}

	public static GuideDefinitionCommon fromXml(Element xml)
	{
		Identifier guideId = Inscribe.ELEMENT_HELPER.asIdentifier(xml, "id");
		ItemSpecification itemSpecification = getItemSpecification(xml); 
		return new GuideDefinitionCommon(guideId, itemSpecification);
	}
	
	private static ItemSpecification getItemSpecification(Element xml)
	{
		Element element;
		if ((element = xml.getChild("standard")) != null)
		{
			return new ItemSpecification.Standard(element.getAttributeValue("item_group", Inscribe.XML_NAMESPACE));
		}
		else if ((element = xml.getChild("custom")) != null)
		{
			ItemStack stack = XmlItemStack.fromXml(element).getStack();
			return new ItemSpecification.Custom(stack );
		}
		else if (xml.getChild("none") != null)
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

	public ItemSpecification getItemSpecifier()
	{
		return itemSpecification;
	}
	
	@Override
	public String toString()
	{
		return String.format("GuideDefinitionCommon [id=%s, itemSpecifier=%s]", id, itemSpecification);
	}
	
	public static class Converter implements ByteBufConverter<GuideDefinitionCommon>
	{
		@Override
		public GuideDefinitionCommon read(PacketByteBuf buffer, Class<GuideDefinitionCommon> target, ByteBufToObjectConverter converter)
		{
			Identifier id = buffer.readIdentifier();
			ItemSpecification itemSpecification = converter.readFromByteBuffer(buffer, ItemSpecification.class);
			return new GuideDefinitionCommon(id, itemSpecification);
		}

		@Override
		public void write(PacketByteBuf buffer, GuideDefinitionCommon definition, ObjectToByteBufConverter converter)
		{
			buffer.writeIdentifier(definition.getGuideId());
			converter.writeToByteBuffer(buffer, definition.itemSpecification, ItemSpecification.class);
		}
	}
}
