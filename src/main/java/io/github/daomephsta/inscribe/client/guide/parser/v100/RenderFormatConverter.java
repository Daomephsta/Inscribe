package io.github.daomephsta.inscribe.client.guide.parser.v100;

import org.commonmark.node.Node;

import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.ImageWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.StackDisplayWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.parser.markdown.InscribeMarkdownVisitor;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlImage;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlItemStack;
import io.github.daomephsta.mosaic.flow.Flow.Direction;

public class RenderFormatConverter
{
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
		else if (xmlForm instanceof Node)
		    return parseMarkDown((Node) xmlForm);
		else
		    throw new IllegalArgumentException("Cannot convert " + xmlForm);
	}

	private static GuideFlow parseMarkDown(Node markDownRoot)
	{
	    GuideFlow output = new GuideFlow(Direction.VERTICAL);
	    markDownRoot.accept(new InscribeMarkdownVisitor(output ));
	    return output;
	}
}
