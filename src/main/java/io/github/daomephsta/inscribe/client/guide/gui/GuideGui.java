package io.github.daomephsta.inscribe.client.guide.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

/**
 * Implemented by an {@link Screen} to designate it as a guide GUI.
 * Any mod implementing their own GUI for their Inscribe guide must implement this interface.
 * @author Daomephsta
 *
 */
public interface GuideGui
{
    public void openEntry(Identifier entryId);

    public void reloadOpenGuide();

    public void reloadOpenEntry();

    public Identifier getOpenGuideId();
}
