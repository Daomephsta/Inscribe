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
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import io.github.daomephsta.inscribe.client.guide.parser.Parsers;
import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeXmlParseException;
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
					GuideDefinition guideDefinition = loadGuideDefinition(builder, resourceManager, guideDefPath);
					String rootPath = Identifiers.replaceFromEnd(guideDefPath, 1, "entries").getPath();
					Collection<XmlEntry> entries = loadEntries(builder, resourceManager, rootPath);
					guides.add(new Guide(guideDefinition, entries));
				}
				catch (JDOMException | InscribeSyntaxException e)
				{
					LOGGER.error("[Inscribe] {} failed to load correctly:\n\t{}", guideDefPath, e.getMessage());
					errored = true;
					continue;
				}
				catch (IOException | InscribeXmlParseException e)
				{
					throw new RuntimeException("An unrecoverable error occured while loading " + guideDefPath, e);
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

	private GuideDefinition loadGuideDefinition(SAXBuilder builder, ResourceManager resourceManager, Identifier path) throws JDOMException, IOException, InscribeSyntaxException
	{
		Element root = builder.build(resourceManager.getResource(path).getInputStream()).getRootElement();
		return Parsers.loadGuideDefinition(root);
	}

	private Collection<XmlEntry> loadEntries(SAXBuilder builder, ResourceManager resourceManager, String rootPath)
	{
		Collection<XmlEntry> entries = new ArrayList<>();
		for(Identifier entryPath : resourceManager.findResources(rootPath, path -> path.endsWith(".xml")))
		{
			try
			{
				entries.add(loadEntry(builder, resourceManager, entryPath));
			}
			catch (JDOMException | InscribeSyntaxException e)
			{
				LOGGER.error("[Inscribe] {} failed to load correctly:\n{}", entryPath, e.getMessage());
				errored = true;
				continue;
			}
			catch (IOException e)
			{
				throw new RuntimeException("An unrecoverable error occured while loading " + entryPath, e);
			}
		}
		return entries;
	}

	private XmlEntry loadEntry(SAXBuilder builder, ResourceManager resourceManager, Identifier path) throws JDOMException, IOException, InscribeSyntaxException
	{
		Element root = builder.build(resourceManager.getResource(path).getInputStream()).getRootElement();
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
