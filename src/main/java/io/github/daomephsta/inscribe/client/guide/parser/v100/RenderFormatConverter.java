package io.github.daomephsta.inscribe.client.guide.parser.v100;

import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.ImageWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.StackDisplayWidget;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlGuideGuiElement;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlImage;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements.XmlItemStack;

public class RenderFormatConverter
{
	public static GuideWidget convert(XmlGuideGuiElement xmlForm)
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
		else
			throw new IllegalArgumentException("Cannot convert " + xmlForm);
	}
}
