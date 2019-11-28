package io.github.daomephsta.inscribe.common.util.messaging;

import java.util.ArrayDeque;
import java.util.Queue;

import io.github.daomephsta.inscribe.client.hooks.ClientPlayerJoinWorldCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

class DefaultNotifier implements Notifier
{
    private final Queue<Text> messages = new ArrayDeque<>();

    public DefaultNotifier()
    {
        ClientPlayerJoinWorldCallback.EVENT.register(player ->
        {
            while (!messages.isEmpty())
                player.sendMessage(messages.poll());
        });
    }

    @Override
    public void notify(Text message)
    {
        if (MinecraftClient.getInstance().player != null)
            MinecraftClient.getInstance().player.sendMessage(message);
        else
            messages.add(message);
    }
}