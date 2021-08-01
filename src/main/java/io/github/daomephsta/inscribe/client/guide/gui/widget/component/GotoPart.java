package io.github.daomephsta.inscribe.client.guide.gui.widget.component;

import io.github.daomephsta.inscribe.client.guide.gui.GuideGui;
import io.github.daomephsta.inscribe.client.guide.gui.InteractableElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public class GotoPart extends WidgetComponent implements InteractableElement
{
    private final Identifier partId;

    public GotoPart(Identifier partId)
    {
        this.partId = partId;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        if (currentScreen instanceof GuideGui)
        {
            ((GuideGui) currentScreen).open(partId);
            return true;
        }
        return false;
    }
}
