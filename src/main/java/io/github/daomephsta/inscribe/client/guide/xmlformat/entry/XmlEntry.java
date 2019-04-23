package io.github.daomephsta.inscribe.client.guide.xmlformat.entry;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import io.github.daomephsta.inscribe.client.guide.xmlformat.base.XmlMixedContent;
import net.minecraft.util.Identifier;

public class XmlEntry extends XmlMixedContent
{	
	private final String title;
    private final Identifier icon,
    						 category;
    private final Set<String> tags;
    
	public XmlEntry(String title, Identifier icon, Identifier category, Iterable<String> tags, List<Object> content)
	{
		super(content);
		this.title = title;
		this.icon = icon;
		this.category = category;
		this.tags = ImmutableSet.copyOf(tags);
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
