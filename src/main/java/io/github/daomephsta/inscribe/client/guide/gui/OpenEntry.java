package io.github.daomephsta.inscribe.client.guide.gui;

import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.parser.v100.RenderFormatConverter;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.mosaic.flow.Flow.Direction;

public class OpenEntry implements VisibleContent
{
    private final GuideFlow root;

    public OpenEntry(XmlEntry entry, int x, int y, int width, int height)
    {
        this.root = new GuideFlow(Direction.VERTICAL);
        root.padding().setLeft(13).setTop(10);
        entry.getPages().get(0).getContent().stream()
            .map(RenderFormatConverter::convert)
            .forEachOrdered(root::add);
    }

    @Override
    public void setRenderArea(int x, int y, int width, int height)
    {
        root.setLayoutParameters(x, y, width, height);
        root.layoutChildren();
    }

    @Override
    public void render(int mouseX, int mouseY, float lastFrameDuration)
    {
        root.render(mouseX, mouseY, lastFrameDuration);
    }
}
