package io.github.daomephsta.inscribe.client.guide.gui.widget.component;

import java.net.URI;

import io.github.daomephsta.inscribe.client.guide.gui.InteractableElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Util;

public class GotoURI extends WidgetComponent implements InteractableElement
{
    private final URI uri;

    public GotoURI(URI uri)
    {
        //TODO Move security checks from InscribeMarkdownVisitor.visit(Link)
        this.uri = uri;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (MinecraftClient.getInstance().options.chatLinksPrompt)
        {
            Screen prevScreen = MinecraftClient.getInstance().currentScreen;
            MinecraftClient.getInstance().setScreen(new ConfirmChatLinkScreen(confirmed ->
            {
                if (confirmed)
                    Util.getOperatingSystem().open(uri);
                MinecraftClient.getInstance().setScreen(prevScreen);
            }, uri.toString(), false));
        }
        else
            Util.getOperatingSystem().open(uri);
        return true;
    }
}
