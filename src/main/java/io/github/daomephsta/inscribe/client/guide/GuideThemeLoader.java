package io.github.daomephsta.inscribe.client.guide;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSchemaFactory;

import io.github.daomephsta.inscribe.client.guide.xmlformat.Schemas;
import io.github.daomephsta.inscribe.client.guide.xmlformat.theme.Theme;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class GuideThemeLoader implements SimpleResourceReloadListener<Collection<Theme>>
{
	public static final GuideThemeLoader INSTANCE = new GuideThemeLoader();
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier ID = new Identifier(Inscribe.MOD_ID, "guide_theme_loader");
	private static final String FOLDER_NAME = Inscribe.MOD_ID + "_themes";
	private static final String THEME_FILE_EXT = ".xml";
	
	private final Map<Identifier, Theme> themes = new HashMap<>();
	private boolean errored;
	
	private GuideThemeLoader() {}
	
	@Override
	public CompletableFuture<Collection<Theme>> load(ResourceManager resourceManager, Profiler profiler, Executor executor)
	{
		return CompletableFuture.supplyAsync(() -> 
		{
			SAXBuilder builder = new SAXBuilder();
			builder.setIgnoringBoundaryWhitespace(true);
			builder.setXMLReaderFactory(new XMLReaderSchemaFactory(Schemas.INSTANCE.theme()));
			
			Collection<Identifier> themePaths = resourceManager.findResources(FOLDER_NAME, path -> path.endsWith(THEME_FILE_EXT));
			Collection<Theme> themes = new ArrayList<>(themePaths.size());
			for (Identifier themePath : themePaths)
			{
				try
				{
					themes.add(loadTheme(builder, resourceManager, themePath));
				}
				catch (JDOMException e) 
				{
					LOGGER.error("[Inscribe] {} failed to load correctly:\n{}", themePath, e.getMessage());
					errored = true;
				}
				catch (IOException e)
				{
					throw new RuntimeException("An unrecoverable error occured while loading " + themePath, e);
				}
			}
			return themes;
		}, executor)
		.exceptionally(thrw -> 
		{
			LOGGER.error("An unexpected error occured during the LOAD stage of guide theme loading", thrw);
			return null;
		});
	}

	private Theme loadTheme(SAXBuilder builder, ResourceManager resourceManager, Identifier themePath) throws JDOMException, IOException
	{
		Element xml = builder.build(resourceManager.getResource(themePath).getInputStream()).getRootElement();
		return Theme.fromXml(xml);
	}

	@Override
	public CompletableFuture<Void> apply(Collection<Theme> themes, ResourceManager resourceManager, Profiler profiler, Executor executor)
	{
		return CompletableFuture.runAsync(() -> 
		{
			for(Theme theme : themes)
			{
				this.themes.put(theme.getIdentifier(), theme);
			}
			LOGGER.info("[Inscribe] Loaded {} themes", themes.size());
		}, executor)
		.exceptionally(thrw -> 
		{
			LOGGER.error("An unexpected error occured during the APPLY stage of guide theme loading", thrw);
			return null;
		}); 
	}

	public boolean getErrored()
	{
		return errored;
	}

	@Override
	public Identifier getFabricId()
	{
		return ID;
	}
}
