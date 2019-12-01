package io.github.daomephsta.util;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.GuideLoadingException.Severity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class XmlResources
{
    private XmlResources() {}

    public static Document readDocument(SAXBuilder builder, ResourceManager resourceManager, Identifier path) throws GuideLoadingException
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
}
