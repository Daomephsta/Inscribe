package io.github.daomephsta.inscribe.client.guide.xmlformat.definition;

import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;

public class NoAccessMethod extends GuideAccessMethod implements IXmlRepresentation
{
	public static final XmlElementType<NoAccessMethod> XML_TYPE = 
		new SimpleXmlRepresentationType<>("none", NoAccessMethod.class, NoAccessMethod::new);
	
	private NoAccessMethod() {}
}