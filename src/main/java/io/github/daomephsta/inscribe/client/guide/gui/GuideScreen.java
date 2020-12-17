package io.github.daomephsta.inscribe.client.guide.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.common.util.Identifiers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public abstract class GuideScreen extends Screen implements GuideGui
{
    private static final Logger LOGGER = LogManager.getLogger();
    protected final Guide guide;

    public GuideScreen(Guide guide)
    {
        super(new TranslatableText(guide.getTranslationKey()));
        this.guide = guide;
    }

    @Override
    public void openEntry(Identifier entryId)
    {
        Identifier guideId = Identifiers.builder(entryId).subPath(0, 1).build();
        Guide owningGuide = GuideManager.INSTANCE.getGuide(guideId);
        XmlEntry entry = owningGuide.getEntry(entryId);
        if (entry != null)
            MinecraftClient.getInstance().openScreen(new OpenEntryScreen(owningGuide, entry));
        else
            LOGGER.error("Could not open unknown entry {}", entryId);
    }

    @Override
    public Identifier getOpenGuideId()
    {
        return guide.getIdentifier();
    }
}
