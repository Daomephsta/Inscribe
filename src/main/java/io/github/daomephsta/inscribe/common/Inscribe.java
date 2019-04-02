package io.github.daomephsta.inscribe.common;

import org.jdom2.Namespace;

import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlElements;
import io.github.daomephsta.inscribe.common.guide.item.GuideItem;
import net.minecraft.util.registry.Registry;

public class Inscribe
{
	public static final Namespace XML_NAMESPACE = Namespace.getNamespace("https://github.com/Daomephsta/inscribe");
	public static final XmlElements ELEMENT_HELPER = new XmlElements(XML_NAMESPACE);
	public static final XmlAttributes ATTRIBUTE_HELPER = new XmlAttributes(XML_NAMESPACE);
	public static final String MOD_ID = "inscribe";
	public static final GuideItem GUIDE_ITEM = new GuideItem();
	
	public void onInitialise()
	{		
		Registry.register(Registry.ITEM, MOD_ID + ":guide", GUIDE_ITEM);
	}
}
