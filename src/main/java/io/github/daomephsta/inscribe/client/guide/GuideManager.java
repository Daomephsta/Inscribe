package io.github.daomephsta.inscribe.client.guide;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Sets;
import com.pivovarit.function.ThrowingSupplier;
import com.pivovarit.function.exception.WrappedException;

import io.github.daomephsta.inscribe.client.guide.parser.Parsers;
import io.github.daomephsta.inscribe.client.guide.parser.XmlResources;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.GuideItemAccessMethod;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.TableOfContents;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlPage;
import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.inscribe.common.util.messaging.Notifier;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.item.ItemStack;
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
    static final String GUIDE_DEFINITION_FILENAME = "guide_definition.xml";

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
            .thenAcceptAsync(data -> apply(data, resourceManager, applyProfiler, applyExecutor), applyExecutor)
            .thenCompose(synchronizer::whenPrepared);
    }

    public CompletableFuture<Guide> reloadGuide(Identifier guideId, Synchronizer synchronizer,
        ResourceManager assets, Profiler loadProfiler, Profiler applyProfiler, Executor loadExecutor,
        Executor applyExecutor) throws GuideLoadingException
    {
        return CompletableFuture.supplyAsync(ThrowingSupplier.unchecked(() ->
            findGuideAssets(assets, FOLDER_NAME + '/' + guideId.getPath())), loadExecutor)
        .thenApplyAsync(guideAssets ->
        {
            loadGuideAssets(guideAssets, assets);
            return getGuide(guideId);
        }, applyExecutor)
        .exceptionally(thrw ->
        {
            if (thrw instanceof WrappedException)
                thrw = thrw.getCause();
            LOGGER.error("An error occured while reloading guide '{}'", guideId, thrw);
            Notifier.DEFAULT.notify(new TranslatableText(Inscribe.MOD_ID + ".chat_message.reload_failure.open_guide"));
            return getGuide(Guide.INVALID_GUIDE_ID);
        })
        .thenApplyAsync(guide ->
        {
            this.guides.put(guide.getIdentifier(), guide);
            LOGGER.info("Reloaded guide '{}'", guide.getIdentifier());
            return guide;
        }, applyExecutor)
        .thenCompose(synchronizer::whenPrepared);
    }

    public CompletableFuture<XmlEntry> reloadEntry(XmlEntry entry, Synchronizer synchronizer,
        ResourceManager resourceManager, Profiler loadProfiler, Profiler applyProfiler,
        Executor loadExecutor, Executor applyExecutor) throws GuideLoadingException
    {
        return CompletableFuture.supplyAsync(ThrowingSupplier.unchecked(() ->
        {
            return Parsers.loadEntry(XmlResources.readDocument(XmlResources.GENERAL, resourceManager,
                entry.getFilePath()), resourceManager, entry.getFilePath());
        }), loadExecutor)
        .exceptionally(thrw ->
        {
             if (thrw instanceof WrappedException)
                thrw = thrw.getCause();
             LOGGER.error("An error occured while reloading entry '{}'", entry.getId(), thrw);
             Notifier.DEFAULT.notify(new TranslatableText(Inscribe.MOD_ID + ".chat_message.reload_failure.open_entry"));
             return createFallbackEntry(entry.getId(), entry.getFilePath());
        })
        .thenApplyAsync(newEntry ->
        {
            Guide guide = guides.get(newEntry.getFilePath().getGuideId());
            guide.replaceEntry(newEntry.getId(), newEntry);
            LOGGER.info("Reloaded entry '{}'", newEntry.getId());
            return newEntry;
        }, applyExecutor);
    }

    public CompletableFuture<TableOfContents> reloadTableOfContents(TableOfContents oldToc,
        Synchronizer synchronizer, ResourceManager resourceManager, Profiler loadProfiler, Profiler applyProfiler,
        Executor loadExecutor, Executor applyExecutor) throws GuideLoadingException
    {
        return CompletableFuture.supplyAsync(ThrowingSupplier.unchecked(() ->
        {
            Document doc = XmlResources.readDocument(XmlResources.GENERAL, resourceManager, oldToc.getFilePath());
            return Parsers.loadTableOfContents(doc, resourceManager, oldToc.getFilePath());
        }), loadExecutor)
        .thenApplyAsync(toc ->
        {
            Guide guide = guides.get(oldToc.getFilePath().getGuideId());
            guide.replaceTableOfContents(toc.getId(), toc);
            LOGGER.info("Reloaded ToC '{}'", toc.getId());
            return toc;
        }, applyExecutor)
        .exceptionally(thrw ->
        {
             if (thrw instanceof WrappedException)
                thrw = thrw.getCause();
             LOGGER.error("An error occured while reloading ToC '{}'", oldToc.getId(), thrw);
             Notifier.DEFAULT.notify(new TranslatableText(Inscribe.MOD_ID + ".chat_message.reload_failure.open_toc"));
             return getGuide(Guide.INVALID_GUIDE_ID).getMainTableOfContents();
        });
    }


    public CompletableFuture<Map<GuideIdentifier, Document>> load(
        ResourceManager assets, Profiler profiler, Executor executor)
    {
        return CompletableFuture.supplyAsync(() -> findGuideAssets(assets, FOLDER_NAME), executor)
        .exceptionally(thrw ->
        {
            if (thrw instanceof WrappedException)
                thrw = thrw.getCause();
            LOGGER.error("An unexpected error occured during the LOAD stage of guide loading", thrw);
            Notifier.DEFAULT.notify(new TranslatableText(Inscribe.MOD_ID + ".chat_message.load_failure.guide"));
            return null;
        });
    }

    public Map<GuideIdentifier, Document> findGuideAssets(ResourceManager assets, String folder)
    {
        Map<GuideIdentifier, Document> guideAssets = new TreeMap<>((a, b) ->
        {
            // Guide definitions first
            if (isGuideDefinition(a) && !isGuideDefinition(b))
                return -1;
            if (!isGuideDefinition(a) && isGuideDefinition(b))
                return 1;
            // Translations of the same entry are equivalent, for the purposes of this map
            if (a.getGuideId().equals(b.getGuideId()) && a.getSectionPath().equals(b.getSectionPath()))
                return 0;
            return a.compareTo(b);
        });
        for (Identifier file : assets.findResources(folder, fileName -> fileName.endsWith(".xml")))
        {
            GuideIdentifier guideAsset = new GuideIdentifier(file);
            try
            {
                if (guideAsset.getLangCode().isEmpty() ||
                    guideAsset.getLangCode().equals(MinecraftClient.getInstance().options.language))
                {
                    // Universal or user language assets override any existing assets
                    guideAssets.put(guideAsset, XmlResources.readDocument(
                        XmlResources.GENERAL, assets, file));
                }
                else if (guideAsset.getLangCode().equals("en_us"))
                {
                    // en_us assets don't override
                    guideAssets.putIfAbsent(guideAsset, XmlResources.readDocument(
                        XmlResources.GENERAL, assets, file));
                }
            }
            catch (GuideLoadingException e)
            {
                e.printStackTrace();
            }
        }
        return guideAssets;
    }

    private boolean isGuideDefinition(GuideIdentifier filePath)
    {
        return filePath.getPath().endsWith(GUIDE_DEFINITION_FILENAME);
    }

    public void apply(Map<GuideIdentifier, Document> guideAssets,
        ResourceManager assets, Profiler profiler, Executor executor)
    {
        Set<Identifier> oldGuides = new HashSet<>(guides.keySet());
        this.guides.clear();
        loadGuideAssets(guideAssets, assets);
        editSearchCache(oldGuides);
        LOGGER.info("Loaded {} guides", guides.size());
    }

    private void loadGuideAssets(Map<GuideIdentifier, Document> guideAssets, ResourceManager assets)
    {
        for (Entry<GuideIdentifier, Document> entry : guideAssets.entrySet())
        {
            GuideIdentifier file = entry.getKey();
            Document doc = entry.getValue();
            Element root = doc.getDocumentElement();
            try
            {
                if (isGuideDefinition(file))
                    guides.put(file.getGuideId(), new Guide(Parsers.loadGuideDefinition(doc, assets, file)));
                else if (root.getTagName().equals("entry"))
                {
                    Guide guide = guides.get(file.getGuideId());
                    if (guide == null)
                    {
                        LOGGER.error("Skipped loading {} because no guide definition exists for {}",
                            file, file.getGuideId());
                        continue;
                    }
                    guide.addEntry(Parsers.loadEntry(doc, assets, file));
                }
                else if (root.getTagName().equals("toc"))
                {
                    Guide guide = guides.get(file.getGuideId());
                    if (guide == null)
                    {
                        LOGGER.error("Skipped loading {} because no guide definition exists for {}",
                            file, file.getGuideId());
                        continue;
                    }
                    guide.addTableOfContents(Parsers.loadTableOfContents(doc, assets, file));
                }
            }
            catch (GuideLoadingException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void editSearchCache(Set<Identifier> oldGuides)
    {
        SearchableContainer<ItemStack> tooltipSearchCache = MinecraftClient.getInstance()
            .getSearchableContainer(SearchManager.ITEM_TOOLTIP);
        // Clearing the cache to remove guides isn't ideal, but this case should be uncommon
        if (!Sets.difference(oldGuides, guides.keySet()).isEmpty())
            tooltipSearchCache.clear();
        for (Identifier added : Sets.difference(guides.keySet(), oldGuides))
            tooltipSearchCache.add(Inscribe.GUIDE_ITEM.forGuide(getGuide(added)));
    }

    private XmlEntry createFallbackEntry(Identifier entryId, GuideIdentifier filePath)
    {
        return new XmlEntry(entryId, filePath, Collections.emptySet(), Collections.singletonList(new XmlPage(Collections.emptyList())));
    }

    @Override
    public Identifier getFabricId()
    {
        return ID;
    }
}
