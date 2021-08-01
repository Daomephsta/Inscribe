package io.github.daomephsta.inscribe.client.guide.xmlformat;

import io.github.daomephsta.inscribe.client.guide.GuideIdentifier;
import io.github.daomephsta.inscribe.client.guide.gui.GuideSession;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public interface GuidePart
{
    public Identifier getId();
    public GuideIdentifier getFilePath();
    public Screen toScreen(GuideSession session);
}
