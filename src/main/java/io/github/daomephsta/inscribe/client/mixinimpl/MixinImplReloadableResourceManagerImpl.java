package io.github.daomephsta.inscribe.client.mixinimpl;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.*;

public abstract class MixinImplReloadableResourceManagerImpl
{
	private static final Logger INSCRIBE_LOGGER = LogManager.getLogger();

	public static void injectGuideManagerAsListener(List<ResourceReloadListener> initialListeners, List<ResourceReloadListener> listeners)
	{
		boolean success = false;
		if (!(success = injectListenerBefore(initialListeners, GuideManager.INSTANCE, BakedModelManager.class)))
		    success = injectListenerBefore(listeners, GuideManager.INSTANCE, BakedModelManager.class);
		if (success)
			INSCRIBE_LOGGER.info("[Inscribe] Registered Guide Manager as a resource reload listener");
		else
		    INSCRIBE_LOGGER.error("[Inscribe] Failed to register Guide Manager as a resource reload listener");
	}

	private static boolean injectListenerBefore(List<ResourceReloadListener> listeners, ResourceReloadListener listener, Class<? extends ResourceReloadListener> targetClass)
	{
		int targetIndex = -1;
		for (int i = 0; i < listeners.size(); i++)
		{
			if (listeners.get(i) == listener)
				listeners.remove(i);
			else if (targetClass.isInstance(listeners.get(i)))
			{
				if (targetIndex < 0)
					targetIndex = i;
				else
					throw new IllegalStateException("[Inscribe] Did not expect multiple instances of " + targetClass.getName() + " in resource reload listener list");
			}
		}
		if (targetIndex < 0)
			return false;
		else
		{
			listeners.add(targetIndex, listener);
			return true;
		}
	}
}
