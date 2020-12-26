package io.github.daomephsta.inscribe.client.guide.xmlformat.definition;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.xmlformat.theme.Theme;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.util.Identifier;

public class GuideDefinition
{
    public static final GuideDefinition FALLBACK = new GuideDefinition(Guide.INVALID_GUIDE_ID,
        new NoGuideAccessMethod(), new Identifier(Inscribe.MOD_ID, "invalid/toc"), Theme.DEFAULT, "en_us");

    private final Identifier guideId;
    private final String translationKey;
    private final GuideAccessMethod guideAccess;
    private final Identifier mainTableOfContents;
    private final Theme theme;
    private final String activeTranslation;

    public GuideDefinition(Identifier guideId, GuideAccessMethod guideAccess, Identifier mainTableOfContents, Theme theme, String activeTranslation)
    {
        this.guideId = guideId;
        this.translationKey = guideId.getNamespace() + ".guide." + guideId.getPath() + ".name";
        this.guideAccess = guideAccess;
        this.mainTableOfContents = mainTableOfContents;
        this.theme = theme;
        this.activeTranslation = activeTranslation;
    }

    public Identifier getGuideId()
    {
        return guideId;
    }

    public String getTranslationKey()
    {
        return translationKey;
    }

    public GuideAccessMethod getAccessMethod()
    {
        return guideAccess;
    }

    public Identifier getMainTableOfContents()
    {
        return mainTableOfContents;
    }

    public Theme getTheme()
    {
        return theme;
    }

    public String getActiveTranslation()
    {
        return activeTranslation;
    }

    @Override
    public String toString()
    {
        return String.format("GuideDefinitionClient [guideId=%s, theme=%s]", guideId, theme);
    }
}
