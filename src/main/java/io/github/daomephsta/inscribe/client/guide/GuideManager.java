package io.github.daomephsta.inscribe.client.guide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

import com.pivovarit.function.ThrowingSupplier;
import com.pivovarit.function.exception.WrappedException;

import io.github.daomephsta.inscribe.client.guide.parser.Parsers;
import io.github.daomephsta.inscribe.client.guide.parser.XmlResources;
import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideDefinition;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideItemAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.TableOfContents;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlPage;
import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.inscribe.common.util.Identifiers;
import io.github.daomephsta.inscribe.common.util.messaging.Notifier;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class GuideManager implements IdentifiableResourceReloadListener
{
    public static final GuideManager INSTANCE = new GuideManager();
    public static final String FOLDER_NAME = Inscribe.MOD_ID + "_guides";
    private static final Logger LOGGER = LogManager.getLogger("inscribe.dedicated.guide_manager");
    private static final Identifier ID = new Identifier(Inscribe.MOD_ID, "guide_manager");
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
                if (thrw instanceof WrappedException)
                    thrw = thrw.getCause();
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

    public CompletableFuture<XmlEntry> reloadEntry(Identifier guideId, Identifier entryFile, Synchronizer synchronizer, ResourceManager resourceManager, Profiler loadProfiler, Profiler applyProfiler, Executor loadExecutor, Executor applyExecutor) throws GuideLoadingException
    {
        Element root = XmlResources.readDocument(XmlResources.GENERAL, resourceManager, entryFile).getDocumentElement();
        return loadEntry(root, resourceManager, entryFile, loadExecutor)
            .exceptionally(thrw ->
            {
                 if (thrw instanceof WrappedException)
                    thrw = thrw.getCause();
                 LOGGER.error("An error occured while reloading entry '{}'", entryFile, thrw);
                 Notifier.DEFAULT.notify(new TranslatableText(Inscribe.MOD_ID + ".chat_message.reload_failure.open_entry"));
                 return createFallbackEntry(entryFile, entryFile);
            })
            .thenApply(entry ->
            {
                Guide guide = guides.get(guideId);
                guide.replaceEntry(entry.getId(), entry);
                LOGGER.info("Reloaded entry '{}'", entry.getId());
                return entry;
            });
    }

    public CompletableFuture<TableOfContents> reloadTableOfContents(Identifier guideId, Identifier file,
        Synchronizer synchronizer, ResourceManager resourceManager, Profiler loadProfiler, Profiler applyProfiler,
        Executor loadExecutor, Executor applyExecutor) throws GuideLoadingException
    {
        Supplier<TableOfContents> tocSupplier = ThrowingSupplier.unchecked(() ->
            Parsers.loadTableOfContents(resourceManager, file));
        CompletableFuture<TableOfContents> tocFuture = loadExecutor != null
            ? CompletableFuture.supplyAsync(tocSupplier, loadExecutor)
            : CompletableFuture.supplyAsync(tocSupplier);
        return tocFuture.exceptionally(thrw ->
            {
                 if (thrw instanceof WrappedException)
                    thrw = thrw.getCause();
                 LOGGER.error("An error occured while reloading ToC '{}'", file, thrw);
                 Notifier.DEFAULT.notify(new TranslatableText(Inscribe.MOD_ID + ".chat_message.reload_failure.open_toc"));
                 return getGuide(Guide.INVALID_GUIDE_ID).getMainTableOfContents();
            })
            .thenApply(toc ->
            {
                Guide guide = guides.get(guideId);
                guide.replaceTableOfContents(toc.getId(), toc);
                LOGGER.info("Reloaded ToC '{}'", toc.getId());
                return toc;
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
                if (StringUtils.countMatches(guideDefPath.getPath(), '/') != 2) //inscribe_guides/<guide ID path>/GUIDE_DEFINITION_FILENAME
                {
                    LOGGER.error("Ignored {}, guide definitions must be exactly 1 subfolders deep in {}", guideDefPath, FOLDER_NAME);
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
            if (thrw instanceof WrappedException)
                thrw = thrw.getCause();
            LOGGER.error("An unexpected error occured during the LOAD stage of guide loading", thrw);
            Notifier.DEFAULT.notify(new TranslatableText(Inscribe.MOD_ID + ".chat_message.load_failure.guide"));
            return Collections.singleton(getGuide(Guide.INVALID_GUIDE_ID));
        });
    }

    private CompletableFuture<Guide> loadGuide(ResourceManager resourceManager, Identifier guideDefPath) throws GuideLoadingException
    {
        return CompletableFuture.supplyAsync(ThrowingSupplier.unchecked(() ->
        {
            GuideDefinition guideDefinition = loadGuideDefinition(resourceManager, guideDefPath);
            Identifier rootPath = Identifiers.replaceFromEnd(guideDefPath, 0, guideDefinition.getActiveTranslation() + "/entries");
            Map<Identifier, XmlEntry> entries = new HashMap<>();
            Map<Identifier, TableOfContents> tocs = new HashMap<>();
            loadEntries(resourceManager, guideDefPath.getNamespace(), rootPath.getPath(),
                entry -> entries.put(entry.getId(), entry), toc -> tocs.put(toc.getId(), toc));
            LOGGER.info("Loaded {} entries for {}", entries.size(), guideDefinition.getGuideId());
            return new Guide(guideDefinition, entries, tocs);
        }));
    }

    private GuideDefinition loadGuideDefinition(ResourceManager resourceManager, Identifier path) throws GuideLoadingException
    {
        return Parsers.loadGuideDefinition(resourceManager, path);
    }

    private void loadEntries(ResourceManager resourceManager, String namespace, String rootPath,
        Consumer<XmlEntry> entries, Consumer<TableOfContents> tablesOfContents) throws GuideLoadingException
    {
        for(Identifier entryPath : resourceManager.findResources(rootPath, path -> path.endsWith(".xml")))
        {
            if (!entryPath.getNamespace().equals(namespace))
                continue;

            Element root = XmlResources.readDocument(XmlResources.GENERAL, resourceManager, entryPath).getDocumentElement();
            if (root.getTagName().equals("entry"))
            {
                entries.accept(loadEntry(root, resourceManager, entryPath, null)
                    .exceptionally(thrw ->
                    {
                        Throwable actual = thrw;
                        while (actual instanceof CompletionException || actual instanceof WrappedException)
                            actual = actual.getCause();
                        if (actual instanceof InscribeSyntaxException)
                            LOGGER.error("Entry at {} failed to load correctly:\n\t{}", entryPath, actual.getMessage());
                        else
                            LOGGER.error("Entry at {} failed to load correctly:", entryPath, actual);
                        Notifier.DEFAULT.notify(new TranslatableText(Inscribe.MOD_ID + ".chat_message.load_failure.entry"));
                        return createFallbackEntry(entryPath, entryPath);
                    })
                    .join());
            }
            else if (root.getTagName().equals("toc"))
            {
                tablesOfContents.accept(Parsers.loadTableOfContents(root, resourceManager, entryPath));
            }
        }
    }

    private CompletableFuture<XmlEntry> loadEntry(Element root, ResourceManager resourceManager, Identifier path, Executor loadExecutor)
    {
        Supplier<XmlEntry> entrySupplier = ThrowingSupplier.unchecked(() -> Parsers.loadEntry(root, resourceManager, path));
        return loadExecutor != null
            ? CompletableFuture.supplyAsync(entrySupplier, loadExecutor)
            : CompletableFuture.supplyAsync(entrySupplier);
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

    private XmlEntry createFallbackEntry(Identifier entryId, Identifier entryFile)
    {
        return new XmlEntry(entryId, entryFile, Collections.emptySet(), Collections.singletonList(new XmlPage(Collections.emptyList())));
    }

    @Override
    public Identifier getFabricId()
    {
        return ID;
    }
}
