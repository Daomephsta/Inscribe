package io.github.daomephsta.inscribe.common.guide;

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

import io.github.daomephsta.inscribe.client.guide.xmlformat.Schemas;
import io.github.daomephsta.inscribe.common.Inscribe;
import io.github.daomephsta.inscribe.common.packets.Packets;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class GuideDefinitionCommonLoader implements IdentifiableResourceReloadListener
{
	public static final GuideDefinitionCommonLoader INSTANCE = new GuideDefinitionCommonLoader();
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier ID = new Identifier(Inscribe.MOD_ID, "guide_loader_common");
	private static final String FOLDER_NAME = Inscribe.MOD_ID + "_guides";
	private static final String GUIDE_DEFINITION_FILENAME = "guide_def_common.xml";

	private final Collection<GuideDefinitionCommon> guideDefinitions = new ArrayList<>();
	private MinecraftServer server;
	private boolean errored;
	
	private GuideDefinitionCommonLoader() {}
	
	@Override
	public CompletableFuture<Void> apply(Helper helper, ResourceManager resourceManager, Profiler loadProfiler, Profiler applyProfiler, Executor loadExecutor, Executor applyExecutor)
	{
		return Schemas.INSTANCE.load(loadProfiler, applyProfiler, loadExecutor, applyExecutor)
			.thenCompose(v -> load(resourceManager, loadProfiler, loadExecutor))
			.thenCompose(helper::waitForAll)
			.thenCompose(data -> apply(data, resourceManager, applyProfiler, applyExecutor));
	}
	
	public CompletableFuture<Collection<GuideDefinitionCommon>> load(ResourceManager resourceManager, Profiler profiler, Executor executor)
	{	
		return CompletableFuture.supplyAsync(() ->
		{
			SAXBuilder guideDefinitionBuilder = createBuilder(Schemas.INSTANCE.guideDefinitionCommon());
			
			Collection<Identifier> guideDefinitionPaths = resourceManager.findResources(FOLDER_NAME, path -> path.endsWith(GUIDE_DEFINITION_FILENAME));
			Collection<GuideDefinitionCommon> guideDefinitions = new ArrayList<>(guideDefinitionPaths.size());
			for (Identifier guideDefinitionPath : guideDefinitionPaths)
			{
				try
				{
					guideDefinitions.add(loadGuideDefinition(guideDefinitionBuilder, resourceManager, guideDefinitionPath));
				}
				catch (JDOMException e) 
				{
					LOGGER.error("[Inscribe] {} failed to load correctly:\n{}", guideDefinitionPath, e.getMessage());
					errored = true;
				}
				catch (IOException e)
				{
					throw new RuntimeException("An unrecoverable error occured while loading " + guideDefinitionPath, e);
				}
			}
			return guideDefinitions;
		}, executor);
	}
	
	private SAXBuilder createBuilder(Schema schema)
	{
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringBoundaryWhitespace(true);
		builder.setXMLReaderFactory(new XMLReaderSchemaFactory(schema));
		return builder;
	}

	private GuideDefinitionCommon loadGuideDefinition(SAXBuilder builder, ResourceManager resourceManager, Identifier path) throws JDOMException, IOException
	{
		Element root = builder.build(resourceManager.getResource(path).getInputStream()).getRootElement();
		return GuideDefinitionCommon.fromXml(root);
	}

	public CompletableFuture<Void> apply(Collection<GuideDefinitionCommon> guideDefinitionsIn, ResourceManager resourceManager, Profiler profiler, Executor executor)
	{
		System.out.println("THONK");
		return CompletableFuture.runAsync(() -> 
		{
			this.guideDefinitions.clear();
			this.guideDefinitions.addAll(guideDefinitionsIn);
			LOGGER.info("[Inscribe] Loaded {} common guide definitions", guideDefinitionsIn.size());
			if (this.server != null)
			{
				for (GuideDefinitionCommon guideDefinition : guideDefinitionsIn)
				{
					server.getPlayerManager().sendToAll(Packets.SEND_GUIDE_DEFINITION.createPacket(guideDefinition));
				}
				LOGGER.info("[Inscribe] Sent {} common guide definitions to clients", guideDefinitionsIn.size());
			}
		}, executor);
	}

	public void setServer(MinecraftServer server)
	{
		this.server = server;
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
