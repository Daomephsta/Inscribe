package io.github.daomephsta.inscribe.client.guide;

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

import com.pivovarit.function.ThrowingSupplier;

import io.github.daomephsta.inscribe.client.guide.parser.Parsers;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideDefinition;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideItemAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlPage;
import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.inscribe.common.util.messaging.Notifier;
import io.github.daomephsta.util.Identifiers;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class GuideManager implements IdentifiableResourceReloadListener
{
	public static final GuideManager INSTANCE = new GuideManager();
	private static final Logger LOGGER = LogManager.getLogger("inscribe.dedicated.guide_manager");
	private static final Identifier ID = new Identifier(Inscribe.MOD_ID, "guide_manager");
	public static final String FOLDER_NAME = Inscribe.MOD_ID + "_guides";
	private static final String GUIDE_DEFINITION_FILENAME = "guide_definition.xml";

	private final Map<Identifier, Guide> guides = new HashMap<>();
	private final Collection<Guide> guidesImmutable = Collections.unmodifiableCollection(guides.values());

	public Guide getGuide(Identifier guideId)
	{
		return guides.getOrDefault(guideId, guides.get(Guide.INVALID_GUIDE_ID));
	}

	public boolean hasGuide(Identifier guideId)
    {
        return guides.containsKey(guideId);
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
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager resourceManager, Profiler loadProfiler, Profiler applyProfiler, Executor loadExecutor, Executor applyExecutor)
	{
		return load(resourceManager, loadProfiler, loadExecutor)
			.thenAccept(data -> apply(data, resourceManager, applyProfiler, applyExecutor))
			.thenCompose(synchronizer::whenPrepared);
	}

	public CompletableFuture<Guide> reloadGuide(Identifier guideId, Synchronizer synchronizer, ResourceManager resourceManager, Profiler loadProfiler, Profiler applyProfiler, Executor loadExecutor, Executor applyExecutor) throws GuideLoadingException
	{
	    Identifier guideDefPath = new Identifier(guideId.getNamespace(), FOLDER_NAME + "/" + guideId.getPath()+ "/" + GUIDE_DEFINITION_FILENAME);
	    return loadGuide(resourceManager, guideDefPath)
	        .exceptionally(thrw ->
            {
                LOGGER.error("An error occured while reloading guide '{}'", guideId, thrw);
                Notifier.DEFAULT.notify(new TranslatableText(Inscribe.MOD_ID + ".chat_message.reload_failure.open_guide"));
                return getGuide(Guide.INVALID_GUIDE_ID);
            })
	        .thenApply(guide ->
	        {
	            this.guides.put(guide.getIdentifier(), guide);
	            LOGGER.info("Reloaded guide '{}'", guide.getIdentifier());
	            return guide;
	        })
	        .thenCompose(synchronizer::whenPrepared);
	}

	public CompletableFuture<XmlEntry> reloadEntry(Identifier guideId, Identifier entryId, Synchronizer synchronizer, ResourceManager resourceManager, Profiler loadProfiler, Profiler applyProfiler, Executor loadExecutor, Executor applyExecutor) throws GuideLoadingException
    {
	    String guideFolder = entryId.getPath().substring(0, guideId.getPath().length());
	    String entryPath = entryId.getPath().substring(guideId.getPath().length() + 1, entryId.getPath().length());
	    Identifier entryLocation = Identifiers.builder(entryId.getNamespace())
	        .appendPathSegments(FOLDER_NAME, guideFolder, "entries", entryPath)
	        .suffixPath(".xml")
	        .build();
	    return loadEntry(resourceManager, entryLocation, loadExecutor)
            .exceptionally(thrw ->
            {
                    LOGGER.error("An error occured while reloading entry '{}'", entryId, thrw);
                    Notifier.DEFAULT.notify(new TranslatableText(Inscribe.MOD_ID + ".chat_message.reload_failure.open_entry"));
                    return new XmlEntry(entryId, Collections.emptySet(), Collections.singletonList(new XmlPage(Collections.emptyList())));
            })
	        .thenApply(entry ->
	        {
	            Guide guide = guides.get(guideId);
	            guide.replaceEntry(entry.getId(), entry);
	            LOGGER.info("Reloaded entry '{}'", entry.getId());
	            return entry;
	        });
    }

	public CompletableFuture<Collection<Guide>> load(ResourceManager resourceManager, Profiler profiler, Executor executor)
	{
		return CompletableFuture.supplyAsync(() ->
		{
			Collection<Identifier> guideDefinitions = resourceManager.findResources(FOLDER_NAME, path -> path.endsWith(GUIDE_DEFINITION_FILENAME));
			Collection<Guide> guides = new ArrayList<>(guideDefinitions.size());
			for (Identifier guideDefPath : guideDefinitions)
			{
			    if (guideDefPath.getPath().equals(FOLDER_NAME + "/" + GUIDE_DEFINITION_FILENAME))
			    {
			        LOGGER.error("Ignored {}, it must be in a subfolder of the {} folder", guideDefPath, FOLDER_NAME);
			        continue;
			    }
				try
				{
					guides.add(loadGuide(resourceManager, guideDefPath).join());
				}
				catch (GuideLoadingException loadingException)
				{
				    Identifier guidePath = Identifiers.subPath(guideDefPath, 0, -1);
                    if (loadingException.isFatal())
			            throw new RuntimeException("An unrecoverable error occured while loading a guide from " + guidePath , loadingException);
			        else
			        {
			            LOGGER.error("Guide at {} failed to load correctly:\n\t{}", guidePath, loadingException.getMessage());
			            Notifier.DEFAULT.notify(new TranslatableText(Inscribe.MOD_ID + ".chat_message.load_failure.guide"));
			        }
				}
			}

			return guides;
		}, executor)
		.exceptionally(thrw ->
		{
			LOGGER.error("An unexpected error occured during the LOAD stage of guide loading", thrw);
			return Collections.singleton(getGuide(Guide.INVALID_GUIDE_ID));
		});
	}

    private CompletableFuture<Guide> loadGuide(ResourceManager resourceManager, Identifier guideDefPath) throws GuideLoadingException
    {
        return CompletableFuture.supplyAsync(ThrowingSupplier.unchecked(() ->
        {
            GuideDefinition guideDefinition = loadGuideDefinition(resourceManager, guideDefPath);
            String rootPath = Identifiers.replaceFromEnd(guideDefPath, 0, "entries").getPath();
            Map<Identifier, XmlEntry> entries = loadEntries(resourceManager, guideDefPath.getNamespace(), rootPath).join();
            LOGGER.info("Loaded {} entries for {}", entries.size(), guideDefinition.getGuideId());
            return new Guide(guideDefinition, entries);
        }));
    }

	private GuideDefinition loadGuideDefinition(ResourceManager resourceManager, Identifier path) throws GuideLoadingException
	{
		return Parsers.loadGuideDefinition(resourceManager, path);
	}

	private CompletableFuture<Map<Identifier, XmlEntry>> loadEntries(ResourceManager resourceManager, String namespace, String rootPath)
	{
		return CompletableFuture.supplyAsync(() ->
		{
		    Map<Identifier, XmlEntry> entries = new HashMap<>();
	        for(Identifier entryPath : resourceManager.findResources(rootPath, path -> path.endsWith(".xml")))
	        {
	            if (!entryPath.getNamespace().equals(namespace))
	                continue;
	            try
	            {
	                XmlEntry entry = loadEntry(resourceManager, entryPath, null).join();
	                entries.put(entry.getId(), entry);
	            }
	            catch (GuideLoadingException loadingException)
	            {
	                if (loadingException.isFatal())
	                    throw new RuntimeException("An unrecoverable error occured while loading an entry from " + entryPath, loadingException);
	                else
	                {
	                    LOGGER.error("Entry at {} failed to load correctly:\n\t{}", entryPath, loadingException.getMessage());
	                    Notifier.DEFAULT.notify(new TranslatableText(Inscribe.MOD_ID + ".chat_message.load_failure.entry"));
	                }
	            }
	        }
	        return entries;
		});
	}

	private CompletableFuture<XmlEntry> loadEntry(ResourceManager resourceManager, Identifier path, Executor loadExecutor) throws GuideLoadingException
	{
	    return loadExecutor != null
	        ? CompletableFuture.supplyAsync(ThrowingSupplier.unchecked(() -> Parsers.loadEntry(resourceManager, path)), loadExecutor)
	        : CompletableFuture.supplyAsync(ThrowingSupplier.unchecked(() -> Parsers.loadEntry(resourceManager, path)));
	}

	public void apply(Collection<Guide> guidesIn, ResourceManager resourceManager, Profiler profiler, Executor executor)
	{
		this.guides.clear();
		for (Guide guide : guidesIn)
		{
			this.guides.put(guide.getIdentifier(), guide);
		}
		LOGGER.info("Loaded {} guides", guidesIn.size());
	}

	@Override
	public Identifier getFabricId()
	{
		return ID;
	}
}
