package io.github.daomephsta.inscribe.client.guide.parser;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.GuideLoadingException.Severity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class XmlResources
{
    public static final DocumentBuilder GENERAL;
    static
    {
        try
        {
            GENERAL = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            throw new RuntimeException("Could not initialise general document builder", e);
        }
    }

    private XmlResources() {}

    public static Document readDocument(DocumentBuilder builder, ResourceManager resourceManager, Identifier path) throws GuideLoadingException
    {
        try
        {
            return builder.parse(resourceManager.getResource(path).getInputStream());
        }
        catch (SAXException e)
        {
            throw new GuideLoadingException(e, Severity.NON_FATAL);
        }
        catch(IOException e)
        {
            throw new GuideLoadingException(e, Severity.FATAL);
        }
    }
}
