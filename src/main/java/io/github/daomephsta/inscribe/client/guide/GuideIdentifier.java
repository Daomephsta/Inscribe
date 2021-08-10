package io.github.daomephsta.inscribe.client.guide;

import io.github.daomephsta.inscribe.common.util.Identifiers;
import net.minecraft.util.Identifier;

public class GuideIdentifier extends Identifier
{
    /**Index of the path segment that defines the type of asset**/
    private static final int ASSET_TYPE_INDEX = 3;
    private final Identifier guideId;
    private final String sectionPath, langCode;

    public GuideIdentifier(Identifier identifier)
    {
        super(identifier.getNamespace(), identifier.getPath());
        Identifiers.Working working = Identifiers.working(identifier);
        if (getPath().endsWith(GuideManager.GUIDE_DEFINITION_FILENAME))
        {
            this.langCode = ""; //Universal. Same convention as Locale.ROOT
            this.sectionPath = GuideManager.GUIDE_DEFINITION_FILENAME;
            this.guideId = working.subIdentifier(1, -2).toIdentifier();
        }
        else
        {
            int langCodeIndex = ASSET_TYPE_INDEX - 1;
            this.langCode = working.getSegment(langCodeIndex);
            this.guideId = Identifiers.working(identifier).subIdentifier(1, ASSET_TYPE_INDEX - 1).toIdentifier();
            this.sectionPath = Identifiers.working(identifier).subIdentifier(ASSET_TYPE_INDEX + 1, -1).toPath();
        }
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