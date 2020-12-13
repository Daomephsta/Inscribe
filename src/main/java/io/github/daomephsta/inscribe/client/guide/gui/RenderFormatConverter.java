package io.github.daomephsta.inscribe.client.guide.gui;

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
import io.github.daomephsta.mosaic.flow.FlowLayoutData;
import io.github.daomephsta.mosaic.Size;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.registry.Registry;

public class RenderFormatConverter
{
    private static final Logger LOGGER = LogManager.getLogger("inscribe.dedicated.render_format_converter");

    public static void convert(GuideFlow output, Object intermediateForm)
    {
        if (intermediateForm instanceof XmlImage)
        {
            XmlImage intermediate = (XmlImage) intermediateForm;
            ImageWidget widget = new ImageWidget(intermediate.getSrc(), intermediate.getAltText(), intermediate.getWidth(), intermediate.getHeight());
            widget.setPadding(intermediate.padding);
            widget.setMargin(intermediate.margin);
            addWidget(output, widget, intermediate.size);
        }
        else if (intermediateForm instanceof XmlItemStack)
        {
            XmlItemStack intermediate = (XmlItemStack) intermediateForm;
            addWidget(output, new StackDisplayWidget(intermediate.stack), intermediate.size);
        }
        else if (intermediateForm instanceof XmlEntityDisplay)
        {
            XmlEntityDisplay intermediate = (XmlEntityDisplay) intermediateForm;
            try
            {
                Entity entity = Registry.ENTITY_TYPE.get(intermediate.entityId).create(MinecraftClient.getInstance().world);
                if (entity != null) //EntityType.create(World) is nullable
                {
                    entity.fromTag(intermediate.nbt);
                    EntityDisplayWidget widget = new EntityDisplayWidget(entity, intermediate.transform, intermediate.animation);
                    widget.setPadding(intermediate.padding);
                    widget.setMargin(intermediate.margin);
                    addWidget(output, widget, intermediate.size);
                }
            }
            catch (Exception e)
            {
                LOGGER.error(e);
            }
        }
        else if (intermediateForm instanceof Node)
            ((Node) intermediateForm).accept(new InscribeMarkdownVisitor(output));
        else
        {
            output.add(new LabelWidget(new FormattedTextNode("CONVERT_FAIL",
                MinecraftClient.DEFAULT_TEXT_RENDERER_ID, 0), Alignment.CENTER, Alignment.CENTER, 1.0F));
        }
    }

    private static void addWidget(GuideFlow output, GuideWidget widget, Size size)
    {
        if (size.isDefault())
            output.add(widget);
        else
            output.add(widget, new FlowLayoutData(size));
    }
}
