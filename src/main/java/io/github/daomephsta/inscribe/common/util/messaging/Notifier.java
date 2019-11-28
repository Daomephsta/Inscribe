package io.github.daomephsta.inscribe.common.util.messaging;

import net.minecraft.text.Text;

public interface Notifier
{
    public static final Notifier DEFAULT = new DefaultNotifier();

    public void notify(Text message);
}
