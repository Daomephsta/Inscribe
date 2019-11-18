package io.github.daomephsta.inscribe.client.guide.parser.v100;

import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.ImageWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.LabelWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.StackDisplayWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlImage;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlItemStack;

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
		else if (xmlForm instanceof String)
		{
		    return new LabelWidget(((String) xmlForm).trim(), Alignment.LEADING, Alignment.CENTER, 0x000000);
		}
		else
			throw new IllegalArgumentException("Cannot convert " + xmlForm);
	}
}
