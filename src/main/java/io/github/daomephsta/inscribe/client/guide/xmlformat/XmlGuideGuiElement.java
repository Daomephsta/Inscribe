package io.github.daomephsta.inscribe.client.guide.xmlformat;

import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.xmlformat.base.IXmlRepresentation;
import io.github.daomephsta.mosaic.Size;
import io.github.daomephsta.mosaic.flow.FlowLayoutData;

public interface XmlGuideGuiElement extends IXmlRepresentation
{
    public void acceptPage(GuideFlow output);

    public default void addWidget(GuideFlow output, GuideWidget widget, Size size)
    {
        if (size.isDefault())
            output.add(widget);
        else
            output.add(widget, new FlowLayoutData(size));
    }
}
