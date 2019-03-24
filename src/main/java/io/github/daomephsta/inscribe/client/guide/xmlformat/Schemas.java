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
	GUIDE_DEFINITION_COMMON("schemas/guide_definition/common/schema.xsd"),
	GUIDE_DEFINITION_CLIENT("schemas/guide_definition/client/schema.xsd"),
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
	private Schema guideDefinitionCommonSchema;
	private Schema guideDefinitionClientSchema;
	private Schema entrySchema;
	private Schema themeSchema;
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
			this.guideDefinitionCommonSchema = schemas.get(SchemaId.GUIDE_DEFINITION_COMMON);
			this.guideDefinitionClientSchema = schemas.get(SchemaId.GUIDE_DEFINITION_CLIENT);
			this.entrySchema = schemas.get(SchemaId.ENTRY);
			this.themeSchema = schemas.get(SchemaId.THEME);
			LOGGER.info("[Inscribe] Loaded schemas");
			loaded = true;
		});
	}

	public Schema guideDefinitionCommon()
	{
		return guideDefinitionCommonSchema;
	}

	public Schema guideDefinitionClient()
	{
		return guideDefinitionClientSchema;
	}

	public Schema entry()
	{
		return entrySchema;
	}

	public Schema theme()
	{
		return themeSchema;
	}
}
