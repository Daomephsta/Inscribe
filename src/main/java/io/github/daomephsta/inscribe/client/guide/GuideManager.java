package io.github.daomephsta.inscribe.client.guide;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.xml.validation.Schema;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSchemaFactory;

import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeXmlParseException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.Schemas;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideDefinition;
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
	
	private final Map<Identifier, Guide> guides = new HashMap<>();
	private final Collection<Guide> guidesImmutable = Collections.unmodifiableCollection(guides.values());
	private boolean errored;
	
	private GuideManager() {}
	
	public Guide getGuide(Identifier guideId)
	{
		return guides.get(guideId);
	}
	
	public Collection<Guide> getGuides()
	{
		return guidesImmutable;
	}
	
	@Override
	public CompletableFuture<Void> apply(Helper helper, ResourceManager resourceManager, Profiler loadProfiler, Profiler applyProfiler, Executor loadExecutor, Executor applyExecutor)
	{
		return Schemas.INSTANCE.load(loadProfiler, applyProfiler, loadExecutor, applyExecutor)
			.thenCompose(helper::waitForAll)
			.thenCompose(v -> load(resourceManager, loadProfiler, loadExecutor))
			.thenAccept(data -> apply(data, resourceManager, applyProfiler, applyExecutor));
	}
	
	public CompletableFuture<Collection<Guide>> load(ResourceManager resourceManager, Profiler profiler, Executor executor)
	{	
		return CompletableFuture.supplyAsync(() ->
		{
			SAXBuilder guideDefinitionBuilder = createBuilder(Schemas.INSTANCE.guideDefinition());
			SAXBuilder entryBuilder = createBuilder(Schemas.INSTANCE.entry());
			
			Collection<Identifier> guideDefinitions = resourceManager.findResources(FOLDER_NAME, path -> path.endsWith(GUIDE_DEFINITION_FILENAME));
			Collection<Guide> guides = new ArrayList<>(guideDefinitions.size());
			for (Identifier guideDefPath : guideDefinitions)
			{
				try
				{
					GuideDefinition guideDefinition = loadGuideDefinition(guideDefinitionBuilder, resourceManager, guideDefPath);
					String rootPath = Identifiers.replaceFromEnd(guideDefPath, 1, "entries").getPath();
					Collection<XmlEntry> entries = loadEntries(entryBuilder, resourceManager, rootPath);
					guides.add(new Guide(guideDefinition, entries));
				}
				catch (JDOMException | InscribeXmlParseException e) 
				{
					LOGGER.error("[Inscribe] {} failed to load correctly:\n{}", guideDefPath, e.getMessage());
					errored = true;
					continue;
				}
				catch (IOException e)
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
	
	private SAXBuilder createBuilder(Schema schema)
	{
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringBoundaryWhitespace(true);
		builder.setXMLReaderFactory(new XMLReaderSchemaFactory(schema));
		return builder;
	}

	private GuideDefinition loadGuideDefinition(SAXBuilder builder, ResourceManager resourceManager, Identifier path) throws JDOMException, IOException
	{
		Element root = builder.build(resourceManager.getResource(path).getInputStream()).getRootElement();
		return GuideDefinition.fromXml(root);
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
			catch (JDOMException | InscribeXmlParseException e)
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
	
	private XmlEntry loadEntry(SAXBuilder builder, ResourceManager resourceManager, Identifier path) throws JDOMException, IOException
	{
		Element root = builder.build(resourceManager.getResource(path).getInputStream()).getRootElement();
		return XmlEntry.fromXml(root);
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
