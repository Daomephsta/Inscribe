package io.github.daomephsta.inscribe.common;

import org.jdom2.Namespace;

import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlElements;
import io.github.daomephsta.inscribe.common.guide.GuideDefinitionCommonLoader;
import io.github.daomephsta.inscribe.common.packets.Packets;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class Inscribe
{
	public static final Namespace XML_NAMESPACE = Namespace.getNamespace("https://github.com/Daomephsta/inscribe");
	public static final XmlElements ELEMENT_HELPER = new XmlElements(XML_NAMESPACE);
	public static final XmlAttributes ATTRIBUTE_HELPER = new XmlAttributes(XML_NAMESPACE);
	public static final String MOD_ID = "inscribe";
	
	public void onInitialise()
	{
		ResourceManagerHelper.get(ResourceType.DATA).registerReloadListener(GuideDefinitionCommonLoader.INSTANCE);
		ServerStartCallback.EVENT.register(GuideDefinitionCommonLoader.INSTANCE::setServer);
		Packets.registerPackets();
	}
}
