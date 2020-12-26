package io.github.daomephsta.inscribe.client.guide;

import io.github.daomephsta.inscribe.common.util.Identifiers;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class GuideIdentifier extends Identifier
{
    private final Identifier guideId;
    private final String sectionPath, langCode;

    public GuideIdentifier(Identifier identifier)
    {
        super(identifier.getNamespace(), identifier.getPath());
        Identifiers.Working working = Identifiers.working(identifier);
        int entriesIndex = working.indexOf("entries");
        if (entriesIndex != -1)
        {
            int langCodeIndex = entriesIndex - 1;
            this.langCode = working.getSegment(langCodeIndex);
            this.guideId = Identifiers.working(identifier).subIdentifier(1, langCodeIndex).toIdentifier();
            this.sectionPath = Identifiers.working(identifier).subIdentifier(entriesIndex + 1, -1).toPath();
        }
        else if (getPath().endsWith(GuideManager.GUIDE_DEFINITION_FILENAME))
        {
            this.langCode = ""; //Universal. Same convention as Locale.ROOT
            this.sectionPath = GuideManager.GUIDE_DEFINITION_FILENAME;
            this.guideId = working.subIdentifier(1, -2).toIdentifier();
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