package io.github.daomephsta.inscribe.client.guide.gui;

import net.minecraft.util.Identifier;

public interface GuideGui
{
    public void openEntry(Identifier entryId);

    public void reloadOpenGuide();

    public void reloadOpenEntry();
}
