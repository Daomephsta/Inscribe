package io.github.daomephsta.inscribe.client.guide.parser.v100;

import org.w3c.dom.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlRecipeDisplay;

public class XmlRecipeDisplayElementType extends XmlElementType<XmlRecipeDisplay>
{
    protected XmlRecipeDisplayElementType()
    {
        super("recipe", XmlRecipeDisplay.class);
    }

    @Override
    public XmlRecipeDisplay fromXml(Element xml) throws GuideLoadingException
    {
        return new XmlRecipeDisplay(XmlAttributes.asIdentifier(xml, "id"));
    }
}
