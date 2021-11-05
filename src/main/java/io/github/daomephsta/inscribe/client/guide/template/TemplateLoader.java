package io.github.daomephsta.inscribe.client.guide.template;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pivovarit.function.ThrowingFunction;

import io.github.daomephsta.inscribe.client.guide.parser.XmlResources;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.LanguageAdapter;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public enum TemplateLoader
{
    INSTANCE;

    private final Map<Identifier, Template> templates = new HashMap<>();

    public Template get(ResourceManager resourceManager, Identifier id)
    {
        return templates.computeIfAbsent(id, ThrowingFunction.unchecked(path ->
        {
           Document doc = XmlResources.readDocument(XmlResources.GENERAL, resourceManager, path);
           Element root = doc.getDocumentElement();
           String processorPath = root.getAttribute("processor");
           if (!processorPath.isEmpty())
           {
               TemplateProcessor processor = LanguageAdapter.getDefault().create(
                   FabricLoader.getInstance().getModContainer(path.getNamespace()).get(),
                   processorPath, TemplateProcessor.class);
               return new Template(root, processor);
           }
           return new Template(root, null);
        }));
    }
}
