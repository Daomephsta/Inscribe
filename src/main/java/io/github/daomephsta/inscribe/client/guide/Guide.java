package io.github.daomephsta.inscribe.client.guide;

import java.util.Map;

import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideDefinition;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.TableOfContents;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.client.guide.xmlformat.theme.Theme;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.util.Identifier;

public class Guide
{
	public static final Identifier INVALID_GUIDE_ID = new Identifier(Inscribe.MOD_ID, "invalid");
	private final GuideDefinition definition;
	private final Map<Identifier, XmlEntry> entries;

	public Guide(GuideDefinition definition, Map<Identifier, XmlEntry> entries)
	{
		this.definition = definition;
		this.entries = entries;
	}

	public XmlEntry getEntry(Identifier entryId)
	{
	    return entries.get(entryId);
	}

	public Identifier getIdentifier()
	{
		return definition.getGuideId();
	}

	public String getTranslationKey()
	{
		return definition.getTranslationKey();
	}

	public GuideAccessMethod getAccessMethod()
	{
		return definition.getAccessMethod();
	}

	public TableOfContents getMainTableOfContents()
    {
        return definition.getMainTableOfContents();
    }

    public Theme getTheme()
	{
		return definition.getTheme();
	}

	public boolean isValid()
	{
		return definition.getGuideId().equals(INVALID_GUIDE_ID);
	}
}
