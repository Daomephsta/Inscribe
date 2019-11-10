package io.github.daomephsta.inscribe.client.guide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException.Severity;
import io.github.daomephsta.inscribe.client.guide.parser.Parsers;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideDefinition;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideItemAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.util.Identifiers;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class GuideManager implements IdentifiableResourceReloadListener
{
	public static final GuideManager INSTANCE = new GuideManager();
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier ID = new Identifier(Inscribe.MOD_ID, "guide_manager");
	private static final String FOLDER_NAME = Inscribe.MOD_ID + "_guides";
	private static final String GUIDE_DEFINITION_FILENAME = "guide_definition.xml";

	private final SAXBuilder builder;
	private final Map<Identifier, Guide> guides = new HashMap<>();
	private final Collection<Guide> guidesImmutable = Collections.unmodifiableCollection(guides.values());
	private boolean errored;

	private GuideManager()
	{
		this.builder = new SAXBuilder();
		this.builder.setIgnoringBoundaryWhitespace(true);
	}

	public Guide getGuide(Identifier guideId)
	{
		return guides.getOrDefault(guideId, guides.get(Guide.INVALID_GUIDE_ID));
	}

	public Collection<Guide> getGuides()
	{
		return guidesImmutable;
	}

	public Stream<Identifier> streamGuideModelIds()
	{
		return getGuides().stream()
			.filter(guide -> guide.getAccessMethod() instanceof GuideItemAccessMethod)
			.map(guide -> ((GuideItemAccessMethod) guide.getAccessMethod()).getModelId());
	}

	public Collection<Identifier> getGuideModelIds()
	{
		return streamGuideModelIds()
			.collect(Collectors.toSet());
	}

	@Override
	public CompletableFuture<Void> reload(Helper helper, ResourceManager resourceManager, Profiler loadProfiler, Profiler applyProfiler, Executor loadExecutor, Executor applyExecutor)
	{
		return load(resourceManager, loadProfiler, loadExecutor)
			.thenAccept(data -> apply(data, resourceManager, applyProfiler, applyExecutor))
			.thenCompose(helper::waitForAll);
	}

	public CompletableFuture<Collection<Guide>> load(ResourceManager resourceManager, Profiler profiler, Executor executor)
	{
		return CompletableFuture.supplyAsync(() ->
		{
			Collection<Identifier> guideDefinitions = resourceManager.findResources(FOLDER_NAME, path -> path.endsWith(GUIDE_DEFINITION_FILENAME));
			Collection<Guide> guides = new ArrayList<>(guideDefinitions.size());
			for (Identifier guideDefPath : guideDefinitions)
			{
				try
				{
					GuideDefinition guideDefinition = loadGuideDefinition(resourceManager, guideDefPath);
					String rootPath = Identifiers.replaceFromEnd(guideDefPath, 1, "entries").getPath();
					Collection<XmlEntry> entries = loadEntries(resourceManager, rootPath);
					guides.add(new Guide(guideDefinition, entries));
				}
				catch (GuideLoadingException loadingException)
				{
					if (handleGuideLoadingException(loadingException, guideDefPath.toString())) continue;
				}
			}

			return guides;
		}, executor)
		.exceptionally(thrw ->
		{
			LOGGER.error("An unexpected error occured during the LOAD stage of guide loading", thrw);
			return null;
		});
	}

	private GuideDefinition loadGuideDefinition(ResourceManager resourceManager, Identifier path) throws GuideLoadingException
	{
		Element root = readDocument(resourceManager, path).getRootElement();
		return Parsers.loadGuideDefinition(root, resourceManager);
	}

	private Collection<XmlEntry> loadEntries(ResourceManager resourceManager, String rootPath)
	{
		Collection<XmlEntry> entries = new ArrayList<>();
		for(Identifier entryPath : resourceManager.findResources(rootPath, path -> path.endsWith(".xml")))
		{
			try
			{
				entries.add(loadEntry(resourceManager, entryPath));
			}
			catch (GuideLoadingException loadingException)
			{
				if (handleGuideLoadingException(loadingException, entryPath.toString())) continue;
			}
		}
		return entries;
	}

	private XmlEntry loadEntry(ResourceManager resourceManager, Identifier path) throws GuideLoadingException
	{
		Element root = readDocument(resourceManager, path).getRootElement();
		return Parsers.loadEntry(root);
	}

	public void apply(Collection<Guide> guidesIn, ResourceManager resourceManager, Profiler profiler, Executor executor)
	{
		this.guides.clear();
		for (Guide guide : guidesIn)
		{
			this.guides.put(guide.getIdentifier(), guide);
		}
		LOGGER.info("[Inscribe] Loaded {} guides", guidesIn.size());
	}

	private Document readDocument(ResourceManager resourceManager, Identifier path) throws GuideLoadingException
	{
		try
		{
			return builder.build(resourceManager.getResource(path).getInputStream());
		}
		catch (JDOMException e)
		{
			throw new GuideLoadingException(e, Severity.NON_FATAL);
		}
		catch(IOException e)
		{
			throw new GuideLoadingException(e, Severity.FATAL);
		}
	}

	private boolean handleGuideLoadingException(GuideLoadingException loadingException, String resourcePath)
	{
		if (loadingException.isFatal())
			throw new RuntimeException("An unrecoverable error occured while loading " + resourcePath, loadingException);
		else
		{
			LOGGER.error("[Inscribe] {} failed to load correctly:\n\t{}", resourcePath, loadingException.getMessage());
			errored = true;
		}
		return true;
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
