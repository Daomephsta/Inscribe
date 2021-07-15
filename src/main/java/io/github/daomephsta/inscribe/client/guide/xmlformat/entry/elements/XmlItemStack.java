package io.github.daomephsta.inscribe.client.guide.xmlformat.entry.elements;

import io.github.daomephsta.inscribe.client.guide.gui.widget.StackDisplayWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.xmlformat.XmlGuideGuiElement;
import io.github.daomephsta.mosaic.EdgeSpacing;
import io.github.daomephsta.mosaic.Size;
import net.minecraft.item.ItemStack;

public record XmlItemStack(
    ItemStack stack,    
    EdgeSpacing padding,
    EdgeSpacing margin,
    Size size          
) 
implements XmlGuideGuiElement
{
    public void acceptPage(GuideFlow output)
    {       
        addWidget(output, new StackDisplayWidget(stack), size);
    }
}
