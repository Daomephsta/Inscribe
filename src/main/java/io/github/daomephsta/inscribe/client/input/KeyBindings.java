package io.github.daomephsta.inscribe.client.input;

import java.util.concurrent.CompletableFuture;

import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.gui.GuideGui;
import io.github.daomephsta.inscribe.client.guide.gui.GuideScreen;
import io.github.daomephsta.inscribe.client.input.WatchedKeyBinding.Modifier;
import io.github.daomephsta.inscribe.client.input.KeyWatcher.KeyAction;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.profiler.DummyProfiler;

public class KeyBindings
{
    private static final String KEY_CATEGORY = "category." + Inscribe.MOD_ID + ".keys";
    private static final WatchableKeyBinding BASE_RELOAD_KEY = new WatchableKeyBinding(new Identifier(Inscribe.MOD_ID, "reload_guide_base"),
        InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEYCODE.getKeyCode(), KEY_CATEGORY);

    public static void initialise()
    {
        KeyBindingRegistry.INSTANCE.addCategory(KEY_CATEGORY);
        KeyBindingRegistry.INSTANCE.register(BASE_RELOAD_KEY);
        KeyWatcher.INSTANCE.initialise();
        KeyWatcher.INSTANCE.watch(BASE_RELOAD_KEY, KeyBindings::processReloadAllGuidesBinding, Modifier.CTRL, Modifier.SHIFT);
        KeyWatcher.INSTANCE.watch(BASE_RELOAD_KEY, KeyBindings::processReloadOpenGuideBinding, Modifier.CTRL);
        KeyWatcher.INSTANCE.watch(BASE_RELOAD_KEY, KeyBindings::processReloadOpenEntryBinding);
    }

    private static void processReloadAllGuidesBinding(MinecraftClient client, KeyWatcher.KeyAction action)
    {
        if (action == KeyAction.DOWN)
            return;
        //Reload all guides
        GuideManager.INSTANCE.reload(CompletableFuture::completedFuture, client.getResourceManager(),
            DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, SystemUtil.getServerWorkerExecutor(), client)
            .thenRun(() ->
            {
                if (client.currentScreen instanceof GuideGui)
                {
                    MinecraftClient.getInstance().openScreen(new GuideScreen(
                        GuideManager.INSTANCE.getGuide(((GuideScreen) client.currentScreen).getGuideId())));
                }
            });
    }

    private static void processReloadOpenGuideBinding(MinecraftClient client, KeyWatcher.KeyAction action)
    {
        if (action == KeyAction.DOWN)
            return;
        //Reload open guide
        if (client.currentScreen instanceof GuideGui)
            ((GuideGui) client.currentScreen).reloadOpenGuide();
    }

    private static void processReloadOpenEntryBinding(MinecraftClient client, KeyWatcher.KeyAction action)
    {
        if (action == KeyAction.DOWN)
            return;
        //Reload open entry
        if (client.currentScreen instanceof GuideGui)
            ((GuideGui) client.currentScreen).reloadOpenEntry();
    }
}
