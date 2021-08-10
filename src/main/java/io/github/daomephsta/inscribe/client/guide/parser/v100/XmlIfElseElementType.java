package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.util.List;

import org.w3c.dom.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XPaths;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlIfElse;
import net.minecraft.util.Identifier;

public class XmlIfElseElementType extends XmlElementType<XmlIfElse>
{   
    protected XmlIfElseElementType()
    {
        super("if");
    }

    @Override
    public XmlIfElse fromXml(Element xml) throws GuideLoadingException
    {
        Identifier condition = XmlAttributes.asIdentifier(xml, "condition");
        List<Object> trueBranch = V100Parser.ENTRY_DESERIALISER.deserialise(
            XPaths.nodes(xml, "./node()[not(self::else)]"));
        List<Object> falseBranch = V100Parser.ENTRY_DESERIALISER.deserialise(
            XPaths.nodes(xml, "./else/node()"));
        return new XmlIfElse(condition, trueBranch, falseBranch);
    }
}
