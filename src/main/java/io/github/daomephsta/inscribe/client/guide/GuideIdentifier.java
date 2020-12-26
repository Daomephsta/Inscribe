package io.github.daomephsta.inscribe.client.guide;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class GuideIdentifier extends Identifier
{
    private final Identifier guideId;
    private final String sectionPath, langCode;

    public GuideIdentifier(Identifier identifier)
    {
        super(identifier.getNamespace(), identifier.getPath());
        int langCodeEnd = getPath().indexOf("/entries");
        if (langCodeEnd != -1)
        {
            int langCodeStart = getPath().lastIndexOf('/', langCodeEnd - 1);
            this.langCode = getPath().substring(langCodeStart + 1, langCodeEnd);
            this.guideId = new Identifier(identifier.getNamespace(),
                getPath().substring("inscribe_guides/".length(), langCodeStart));
            this.sectionPath = getPath().substring(langCodeEnd + "/entries/".length());
        }
        else if (getPath().endsWith(GuideManager.GUIDE_DEFINITION_FILENAME))
        {
            this.langCode = ""; //Universal. Same convention as Locale.ROOT
            this.sectionPath = GuideManager.GUIDE_DEFINITION_FILENAME;
            this.guideId = new Identifier(identifier.getNamespace(), getPath().substring("inscribe_guides/".length(),
                getPath().length() - GuideManager.GUIDE_DEFINITION_FILENAME.length() - 1));
        }
        else
            throw new InvalidIdentifierException(identifier + " isn't an entry or guide definition identifier");
    }

    public Identifier getGuideId()
    {
        return guideId;
    }

    public String getSectionPath()
    {
        return sectionPath;
    }

    public String getLangCode()
    {
        return langCode;
    }
}