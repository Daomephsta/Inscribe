package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import java.util.List;

import io.github.daomephsta.inscribe.client.guide.gui.RenderFormatConverter;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlGuideGuiElement;
import net.minecraft.util.Identifier;

public record XmlIfElse(
    Identifier condition,
    List<Object> trueBranch, 
    List<Object> falseBranch) implements XmlGuideGuiElement
{
    @Override
    public void acceptPage(GuideFlow output)
    {
        List<Object> branch = output.getGuide().getFlags().isTrue(condition) ? trueBranch : falseBranch;
        for (Object element : branch)
            RenderFormatConverter.convert(output, element);
    }
}
