package io.github.daomephsta.inscribe.client.guide.xmlformat;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import net.minecraft.util.profiler.Profiler;

enum SchemaId
{
	GUIDE_DEFINITION("schemas/guide_definition/schema.xsd"),
	THEME("schemas/theme/schema.xsd"),
	ENTRY("schemas/entry/schema.xsd");

	private final String path;

	private SchemaId(String path)
	{
		this.path = path;
	}

	public String getPath()
	{
		return path;
	}
}


public class Schemas
{
	public static final Schemas INSTANCE = new Schemas();
	private static final Logger LOGGER = LogManager.getLogger();

	private final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	private Schema guideDefinitionSchema;
	private Schema entrySchema;
	private boolean loaded = false;

	private Schemas() {}

	public CompletableFuture<Void> load(Profiler loadProfiler, Profiler applyProfiler, Executor loadExecutor, Executor applyExecutor)
	{
		return loaded 
			? CompletableFuture.completedFuture(null)
			: loadInternal(loadProfiler, applyProfiler, loadExecutor, applyExecutor);
	}
	
	public CompletableFuture<Void> loadInternal(Profiler loadProfiler, Profiler applyProfiler, Executor loadExecutor, Executor applyExecutor)
	{
		return CompletableFuture.supplyAsync(() ->
		{
			Map<SchemaId, Schema> schemas = new EnumMap<>(SchemaId.class);
			for(SchemaId schemaId : SchemaId.values())
			{
				URL url = ClassLoader.getSystemClassLoader().getResource(schemaId.getPath());
				try
				{
					schemas.put(schemaId, schemaFactory.newSchema(url));
				}
				catch (SAXException e)
				{
					throw new RuntimeException("An unrecoverable error occured while loading a schema", e);
				}
			}
			return schemas;
		}, loadExecutor)
		.thenAccept(schemas -> 
		{
			this.guideDefinitionSchema = schemas.get(SchemaId.GUIDE_DEFINITION);
			this.entrySchema = schemas.get(SchemaId.ENTRY);
			LOGGER.info("[Inscribe] Loaded schemas");
			loaded = true;
		}).exceptionally(thrw -> 
		{
			LOGGER.error("An unexpected error occured while loading schemas", thrw);
			return null;
		});
	}

	public Schema guideDefinition()
	{
		return guideDefinitionSchema;
	}

	public Schema entry()
	{
		return entrySchema;
	}
}
