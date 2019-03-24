package io.github.daomephsta.inscribe.client.guide.xmlformat.entry;

import java.util.*;

import org.jdom2.Element;

import com.google.common.collect.ImmutableSet;

import io.github.daomephsta.inscribe.client.guide.xmlformat.DeserialisationManager;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.XmlMixedContent;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.*;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.util.Identifier;

public class XmlEntry extends XmlMixedContent
{
	public static final DeserialisationManager DESERIALISATION_MANAGER = new DeserialisationManager(XmlParagraph.XML_TYPE, XmlItalics.XML_TYPE, XmlEmphasis.XML_TYPE, XmlBold.XML_TYPE, XmlStrong.XML_TYPE, XmlDel.XML_TYPE, XmlLineBreak.XML_TYPE, XmlWebLink.XML_TYPE, XmlEntryLink.XML_TYPE, XmlAnchor.XML_TYPE, XmlImage.XML_TYPE);
	
	private final String title;
    private final Identifier icon,
    						 category;
    private final Set<String> tags;
    
	private XmlEntry(String title, Identifier icon, Identifier category, Iterable<String> tags, List<Object> content)
	{
		super(content);
		this.title = title;
		this.icon = icon;
		this.category = category;
		this.tags = ImmutableSet.copyOf(tags);
	}
	
	public static XmlEntry fromXml(Element xml)
	{
		String title = xml.getChildText("title", Inscribe.XML_NAMESPACE);
	    Identifier icon = new Identifier(xml.getChildText("icon", Inscribe.XML_NAMESPACE)),
	    		   category = new Identifier(xml.getChildText("icon", Inscribe.XML_NAMESPACE));
		List<Object> content = DESERIALISATION_MANAGER.deserialiseContent(xml.getContent());
		List<String> tags = Inscribe.ELEMENT_HELPER.asStringList(xml, "tags", () -> Collections.emptyList());
		
		return new XmlEntry(title, icon, category, tags, content);
	}

	@Override
	public String toString()
	{
		return String.format("XmlEntry [title=%s, icon=%s, category=%s, tags=%s]", title, icon, category, tags);
	}

	public String getTitle()
	{
		return title;
	}

	public Identifier getIcon()
	{
		return icon;
	}

	public Identifier getCategory()
	{
		return category;
	}
	
	public Set<String> getTags()
	{
		return tags;
	}
}
