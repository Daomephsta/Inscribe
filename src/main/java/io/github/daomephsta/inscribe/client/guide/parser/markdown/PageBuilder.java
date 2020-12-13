package io.github.daomephsta.inscribe.client.guide.parser.markdown;

import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Stream;

import io.github.daomephsta.inscribe.client.guide.gui.InteractableElement;
import io.github.daomephsta.inscribe.client.guide.gui.RenderableElement;
import io.github.daomephsta.inscribe.client.guide.gui.widget.HorizontalRuleWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.VerticalRuleWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.component.GotoEntry;
import io.github.daomephsta.inscribe.client.guide.gui.widget.component.GotoURI;
import io.github.daomephsta.inscribe.client.guide.gui.widget.component.Tooltip;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.FormattedTextNode;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.InlineImage;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.LabelWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.LineBreak;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextBlockWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextNode;
import io.github.daomephsta.inscribe.client.guide.parser.FormatFlags;
import io.github.daomephsta.inscribe.client.guide.parser.markdown.ListData.ListType;
import io.github.daomephsta.mosaic.flow.Flow.Direction;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class PageBuilder
{
    private final Deque<GuideFlow> output;
    private final Deque<FormatFlags> formatFlags = new ArrayDeque<>();
    private final Deque<Identifier> fonts = new ArrayDeque<>();
    private final IntStack colours = new IntArrayList(new int[] {0x000000});
    private final Deque<TextNode> textNodes = new ArrayDeque<>();
    private final Deque<ListData> listData = new ArrayDeque<>();
    private final Deque<InteractableElement> interactables = new ArrayDeque<>();
    private final Deque<RenderableElement> renderables = new ArrayDeque<>();
    private int indentLevel = 0;

    public PageBuilder(GuideFlow output)
    {
        this.output = new ArrayDeque<>();
        this.output.add(output);
        //Default font
        this.fonts.add(MinecraftClient.DEFAULT_TEXT_RENDERER_ID);
    }

    public void pushFormatting(FormatFlags formatFlag)
    {
        formatFlags.push(formatFlag);
    }

    public void popFormatting()
    {
        formatFlags.pop();
    }

    public void pushFont(Identifier font)
    {
        fonts.push(font);
    }

    public void popFont()
    {
        fonts.pop();
    }

    public void pushColour(int colour)
    {
        colours.push(colour);
    }

    public void popColour()
    {
        colours.popInt();
    }

    public void pushInteractable(InteractableElement e)
    {
        interactables.push(e);
    }

    public InteractableElement popInteractable()
    {
        return interactables.pop();
    }

    public void pushRenderable(RenderableElement e)
    {
        renderables.push(e);
    }

    public RenderableElement popRenderable()
    {
        return renderables.pop();
    }

    public void pushLiteral(String literal)
    {
        FormattedTextNode textNode = new FormattedTextNode(literal, fonts.peek(), colours.topInt(), formatFlags.toArray(new FormatFlags[0]));
        if (!interactables.isEmpty())
            textNode.attach(interactables.peek());
        if (!renderables.isEmpty())
            textNode.attach(renderables.peek());
        textNodes.add(textNode);
    }

    public void addHardLineBreak()
    {
        textNodes.add(new LineBreak());
    }

    public void addLabel(Alignment horizontalAlignment, Alignment verticalAlignment, float scale)
    {
        LabelWidget label = new LabelWidget(textNodes.poll(), horizontalAlignment, verticalAlignment, scale);
        if (!interactables.isEmpty())
            label.attach(interactables.peek());
        if (!renderables.isEmpty())
            label.attach(renderables.peek());
        output.peek().add(label);
    }

    public void addTextBlock(Alignment horizontalAlignment, Alignment verticalAlignment)
    {
        ListData listDatum = listData.peek();
        if (listDatum != null)
        {
            listDatum.addListMarker(textNodes, indentLevel);
        }
        List<TextNode> nodes = Stream.generate(textNodes::poll)
            .limit(textNodes.size())
            .collect(toList());
        TextBlockWidget textBlock = new TextBlockWidget(
            horizontalAlignment, verticalAlignment, nodes);
        if (!interactables.isEmpty())
            textBlock.attach(interactables.peek());
        if (!renderables.isEmpty())
            textBlock.attach(renderables.peek());
        output.peek().add(textBlock);
    }

    public void startBlockQuote()
    {
        GuideFlow flow = new GuideFlow(Direction.HORIZONTAL);
        flow.add(new VerticalRuleWidget(0x808080));
        output.push(flow);
    }

    public void endBlockQuote()
    {
        GuideFlow flow = output.pop();
        output.peek().add(flow);
    }

    public void startWebLink(URI destination)
    {
        pushColour(0x0033FF);
        pushFormatting(FormatFlags.UNDERLINE);
        pushRenderable(new Tooltip(tooltip -> tooltip.accept(destination.toString())));
        pushInteractable(new GotoURI(destination));
    }

    public void endWebLink()
    {
        popColour();
        popFormatting();
        popRenderable();
        popInteractable();
    }

    public void startEntryLink(Identifier entryId, String tooltipText)
    {
        pushColour(0x0033FF);
        pushRenderable(new Tooltip(tooltip -> tooltip.accept(tooltipText)));
        pushInteractable(new GotoEntry(entryId));
    }

    public void endEntryLink()
    {
        popColour();
        popRenderable();
        popInteractable();
    }

    public void startList(ListType type)
    {
        listData.push(new ListData(type));
        indentLevel += 1;
    }

    public void endList()
    {
        listData.pop();
        indentLevel -= 1;
    }

    public void nextListItem()
    {
        listData.peek().nextItem();
    }

    public void addHorizontalRule()
    {
        output.peek().add(new HorizontalRuleWidget());
    }

    public void addInlineImage(Identifier location, String tooltipText)
    {
        InlineImage image = new InlineImage(location);
        if (tooltipText != null)
            image.attach(new Tooltip(tooltip -> tooltip.accept(tooltipText)));
        if (!interactables.isEmpty())
            image.attach(interactables.peek());
        if (!renderables.isEmpty())
            image.attach(renderables.peek());
        textNodes.add(image);
    }
}
