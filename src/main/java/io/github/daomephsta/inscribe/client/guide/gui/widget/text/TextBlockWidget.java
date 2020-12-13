package io.github.daomephsta.inscribe.client.guide.gui.widget.text;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;

public class TextBlockWidget extends GuideWidget
{
    public static final Identifier MONO_FONT = new Identifier(Inscribe.MOD_ID, "mono");
    private final Alignment horizontalAlignment,
                            verticalAlignment;
    private final int widthHint,
                      heightHint;
    private final Collection<TextNode> content;

    public TextBlockWidget(Alignment horizontalAlignment, Alignment verticalAlignment, Collection<TextNode> content)
    {
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.content = content;
        int width = 0, height = 0;
        int lineWidth = 0, lineHeight = 0;
        for (Iterator<TextNode> iter = content.iterator(); iter.hasNext();)
        {
            TextNode node = iter.next();
            lineWidth += node.getWidth();
            lineHeight = Math.max(lineHeight, node.getHeight());
            if (!iter.hasNext() || node instanceof LineBreak)
            {
                width = Math.max(width, lineWidth);
                height += lineHeight;
                //Reset for next line
                lineWidth = lineHeight = 0;
            }
        }
        this.widthHint = width;
        this.heightHint = height;
        margin().setVertical(1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (super.mouseClicked(mouseX, mouseY, button))
            return true;
        float left = horizontalAlignment.offsetX(x(), this, hintWidth());
        float x = left;
        float y = verticalAlignment.offsetY(y(), this, hintHeight());
        for (TextNode node : content)
        {
            if (node instanceof FormattedTextNode)
                ((FormattedTextNode) node).mouseClicked(x, y, (int) mouseX, (int) mouseY, button);
            if (node instanceof LineBreak)
            {
                y += 9;
                x = left;
            }
            else
                x += node.getWidth();
            node = node.next;
        }
        return false;
    }

    @Override
    public void renderWidget(int mouseX, int mouseY, float lastFrameDuration)
    {
        float left = horizontalAlignment.offsetX(x(), this, hintWidth());
        float x = left;
        float y = verticalAlignment.offsetY(y(), this, hintHeight());
        int lineHeight = 0;
        Map<Vec2f, ElementHostNode> renderables = new HashMap<>();
        for (TextNode node : content)
        {
            lineHeight = Math.max(lineHeight, node.getHeight());
            node.render(x, y, mouseX, mouseY, lastFrameDuration);
            if (node instanceof ElementHostNode)
                renderables.put(new Vec2f(x, y), (ElementHostNode) node);
            if (node instanceof LineBreak)
            {
                y += lineHeight;
                x = left;
                lineHeight = 0;
            }
            else
                x += node.getWidth();
        }
        // Attached renderables render over/after nodes
        for (Entry<Vec2f, ElementHostNode> entry : renderables.entrySet())
        {
            float x2 = entry.getKey().x, y2 = entry.getKey().y;
            entry.getValue().renderAttached(x2, y2, mouseX, mouseY, lastFrameDuration);
        }
    }

    @Override
    public int hintHeight()
    {
        return heightHint;
    }

    @Override
    public int hintWidth()
    {
        return widthHint;
    }

    @Override
    public String toString()
    {
        return String.format("TextBlockWidget[nodes=%s, horizontalAlignment=%s, verticalAlignment=%s]", content, horizontalAlignment, verticalAlignment);
    }
}
