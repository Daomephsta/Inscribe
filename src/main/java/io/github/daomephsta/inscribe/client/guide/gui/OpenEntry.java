package io.github.daomephsta.inscribe.client.guide.gui;

import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.mosaic.flow.Flow.Direction;
import net.minecraft.util.Identifier;

public class OpenEntry implements VisibleContent
{
    private final XmlEntry entry;
    private final GuideFlow root;

    public OpenEntry(XmlEntry entry)
    {
        this.entry = entry;
        this.root = new GuideFlow(Direction.VERTICAL);
        root.padding()
            .setHorizontal(13)
            .setVertical(10);
        if (!entry.getPages().isEmpty())
        {
            for (Object content : entry.getPages().get(0).getContent())
                RenderFormatConverter.convert(root, content);
        }
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

    @Override
    public void onClose()
    {
        root.dispose();
    }

    Identifier getEntryId()
    {
        return entry.getId();
    }
}
