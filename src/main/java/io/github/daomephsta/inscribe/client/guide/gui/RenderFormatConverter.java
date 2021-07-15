package io.github.daomephsta.inscribe.client.guide.gui;

import org.commonmark.node.Node;

import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.FormattedTextNode;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextBlockWidget;
import io.github.daomephsta.inscribe.client.guide.parser.markdown.InscribeMarkdownVisitor;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlGuideGuiElement;
import net.minecraft.client.MinecraftClient;

public class RenderFormatConverter
{
    public static void convert(GuideFlow output, Object intermediateForm)
    {
        if (intermediateForm instanceof XmlGuideGuiElement guiElement)
            guiElement.acceptPage(output);
        else if (intermediateForm instanceof Node)
            ((Node) intermediateForm).accept(new InscribeMarkdownVisitor(output));
        else
        {
            output.add(new TextBlockWidget(Alignment.CENTER, Alignment.CENTER, 
                new FormattedTextNode("CONVERT_FAIL", MinecraftClient.DEFAULT_FONT_ID, 0)));
        }
    }
}
