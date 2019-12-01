package io.github.daomephsta.inscribe.client.guide.parser.v100;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.commonmark.node.Node;

import io.github.daomephsta.inscribe.client.guide.gui.widget.EntityDisplayWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.ImageWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.StackDisplayWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.FormattedTextNode;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.LabelWidget;
import io.github.daomephsta.inscribe.client.guide.parser.markdown.InscribeMarkdownVisitor;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlEntityDisplay;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlImage;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlItemStack;
import io.github.daomephsta.mosaic.flow.Flow.Direction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.registry.Registry;

public class RenderFormatConverter
{
    private static final Logger LOGGER = LogManager.getLogger("inscribe.dedicated.render_format_converter");

	public static GuideWidget convert(Object xmlForm)
	{
		if (xmlForm instanceof XmlImage)
		{
		    XmlImage xmlImage = (XmlImage) xmlForm;
		    return new ImageWidget(xmlImage.getSrc(), xmlImage.getAltText(), xmlImage.getWidth(), xmlImage.getHeight());
		}
		else if (xmlForm instanceof XmlItemStack)
		{
		    return new StackDisplayWidget(((XmlItemStack) xmlForm).stack);
		}
	    else if (xmlForm instanceof XmlEntityDisplay)
	    {
	        XmlEntityDisplay xed = (XmlEntityDisplay) xmlForm;
	        try
	        {
	            Entity entity = Registry.ENTITY_TYPE.get(xed.entityId).create(MinecraftClient.getInstance().world);
	            if (entity != null) //EntityType.create(World) is nullable
	            {
	                entity.fromTag(xed.nbt);
	                return new EntityDisplayWidget(entity, xed.transform, xed.animation, xed.lighting);
	            }
	        }
	        catch (Exception e)
	        {
	            LOGGER.error(e);
	        }
	    }
		else if (xmlForm instanceof Node)
		    return parseMarkDown((Node) xmlForm);
		return new LabelWidget(new FormattedTextNode("CONVERT_FAIL", 0), Alignment.CENTER, Alignment.CENTER, 1.0F);
	}

	private static GuideFlow parseMarkDown(Node markDownRoot)
	{
	    GuideFlow output = new GuideFlow(Direction.VERTICAL);
	    markDownRoot.accept(new InscribeMarkdownVisitor(output ));
	    return output;
	}
}
