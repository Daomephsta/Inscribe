package io.github.daomephsta.inscribe.client.guide.parser.v100;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

import io.github.daomephsta.inscribe.client.guide.GuideLoadingException;
import io.github.daomephsta.inscribe.client.guide.gui.InteractableElement;
import io.github.daomephsta.inscribe.client.guide.parser.XmlElementType;
import io.github.daomephsta.inscribe.client.guide.xmlformat.InscribeSyntaxException;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlAttributes;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlButton;
import io.github.daomephsta.inscribe.common.Inscribe;

public class XmlButtonElementType implements XmlElementType<XmlButton>
{
    public static final String ID = "button";
    private static final Logger LOGGER = Inscribe.getDedicatedLogger();

    @Override
    public XmlButton fromXml(Element xml) throws GuideLoadingException
    {
        return new XmlButton(createHandler(xml), V100Parser.parseContentAsText(xml, 0x0033FF));
    }

    private InteractableElement createHandler(Element args) throws InscribeSyntaxException
    {
        String handler = XmlAttributes.getValue(args, "handler");
        try
        {
            Class<?> handlerClass = Class.forName(handler);
            Constructor<?> ctor = handlerClass.getConstructor(Element.class);
            return (InteractableElement) ctor.newInstance(args);
        }
        catch (ClassNotFoundException e)
        {
            LOGGER.error(e);
            throw new InscribeSyntaxException("Handler type not found");
        }
        catch (NoSuchMethodException | SecurityException e)
        {
            LOGGER.error(e);
            throw new InscribeSyntaxException("Constructor " + handler + "(org.w3c.dom.Element) unavailable");
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException e)
        {
            LOGGER.error(e);
            throw new InscribeSyntaxException("Handler instantiation failed");
        }
        catch (InvocationTargetException e)
        {
            LOGGER.error(e.getCause());
            throw new InscribeSyntaxException("Handler instantiation failed");
        }
    }
}
