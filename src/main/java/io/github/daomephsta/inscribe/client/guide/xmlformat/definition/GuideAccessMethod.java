package io.github.daomephsta.inscribe.client.guide.xmlformat.definition;

import org.jdom2.Element;

import io.github.daomephsta.inscribe.client.guide.xmlformat.*;

public abstract class GuideAccessMethod
{
	private static final DeserialisationManager DESERIALISATION_MANAGER = new DeserialisationManager
		(
			GuideItemAccessMethod.XML_TYPE,
			NoAccessMethod.XML_TYPE
		);
	
	public static GuideAccessMethod deserialiseGuideAccess(Element element)
	{
		return DESERIALISATION_MANAGER.deserialise(element, GuideAccessMethod.class, false);
	}
}
