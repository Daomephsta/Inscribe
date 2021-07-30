package io.github.daomephsta.inscribe.client.guide.gui;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.daomephsta.inscribe.client.guide.Guide;
import io.github.daomephsta.inscribe.client.guide.GuideManager;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.xmlformat.definition.TableOfContents;
import io.github.daomephsta.inscribe.client.guide.xmlformat.entry.XmlEntry;
import io.github.daomephsta.inscribe.common.util.Identifiers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public abstract class PageSpreadScreen extends Screen implements GuideGui
{
    private static final Logger LOGGER = LogManager.getLogger("Inscribe");
    protected final GuideSession session;
    private PageSpreads pageSpreads;
    private ButtonWidget prevPage, nextPage, back;

    public PageSpreadScreen(GuideSession session)
    {
        super(new TranslatableText(session.getGuide().getTranslationKey()));
        this.session = session;
    }

    protected abstract List<GuideFlow> buildPages();

    @Override
    public void init()
    {
        this.pageSpreads = new PageSpreads(session.getGuide(), buildPages());
        int guideHeight = 232;
        int pageWidth = 176;
        int guideTop = (height - guideHeight) / 2;
        int guideLeft = width / 2 - pageWidth;
        for (Pair<GuideFlow, GuideFlow> spread : pageSpreads)
        {
            spread.getLeft().setLayoutParameters(guideLeft, guideTop, pageWidth, guideHeight);
            spread.getLeft().layoutChildren();
            spread.getRight().setLayoutParameters(guideLeft + pageWidth + 4, guideTop, pageWidth, guideHeight);
            spread.getRight().layoutChildren();
        }
        int controlsX = pageSpreads.rightPage().right() + 12;
        int controlsY = pageSpreads.rightPage().bottom() - 22;
        this.prevPage = addDrawableChild(createControl(controlsX, controlsY, 0 * 18, b ->
        {
            pageSpreads.previous();
            updateButtonVisibility();
        }));
        this.nextPage = addDrawableChild(createControl(controlsX, controlsY, 1 * 18, b ->
        {
            pageSpreads.next();
            updateButtonVisibility();
        }));
        // Home
        addDrawableChild(createControl(controlsX, controlsY, 2 * 18, b ->
        {
            open(session.getGuide().getMainTableOfContentsId());
        }));
        this.back = addDrawableChild(createControl(controlsX, controlsY, 3 * 18, b ->
        {
            session.openLast();
            updateButtonVisibility();
        }));
        updateButtonVisibility();
    }

    private TexturedButtonWidget createControl(int x, int y, int yOffset, PressAction pressAction)
    {
        return new TexturedButtonWidget(x, y - yOffset, 18, 18, 402, 210 - yOffset, 
            0, session.getGuide().getTheme().getGuiTexture(), 440, 290, pressAction);
    }

    private void updateButtonVisibility()
    {
        prevPage.visible = pageSpreads.hasPrevious();
        nextPage.visible = pageSpreads.hasNext();
        back.visible = session.hasHistory();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        renderBackground(matrices);
        RenderSystem.setShaderTexture(0, session.getGuide().getTheme().getGuiTexture());
        drawTexture(matrices, (width - 381) / 2, (height - 232) / 2, 0, 0, 401, 232, 440, 290);

        VertexConsumerProvider.Immediate vertices = VertexConsumerProvider.immediate(
            Tessellator.getInstance().getBuffer());
        pageSpreads.leftPage().render(vertices, matrices , mouseX,
            mouseY, delta, pageSpreads.leftPage().contains(mouseX, mouseY));
        pageSpreads.rightPage().render(vertices, matrices, mouseX,
            mouseY, delta, pageSpreads.rightPage().contains(mouseX, mouseY));
        vertices.draw();

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (super.mouseClicked(mouseX, mouseY, button))
            return true;
        if (pageSpreads.leftPage().contains(mouseX, mouseY))
            return pageSpreads.leftPage().mouseClicked(mouseX, mouseY, button);
        if (pageSpreads.rightPage().contains(mouseX, mouseY))
            return pageSpreads.rightPage().mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (super.mouseReleased(mouseX, mouseY, button))
            return true;
        if (pageSpreads.leftPage().contains(mouseX, mouseY))
            return pageSpreads.leftPage().mouseReleased(mouseX, mouseY, button);
        if (pageSpreads.rightPage().contains(mouseX, mouseY))
            return pageSpreads.rightPage().mouseReleased(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
            return true;
        if (pageSpreads.leftPage().contains(mouseX, mouseY))
            return pageSpreads.leftPage().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        if (pageSpreads.rightPage().contains(mouseX, mouseY))
            return pageSpreads.rightPage().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double wheelDelta)
    {
        if (super.mouseScrolled(mouseX, mouseY, wheelDelta))
            return true;
        if (pageSpreads.leftPage().contains(mouseX, mouseY))
            return pageSpreads.leftPage().mouseScrolled(mouseX, mouseY, wheelDelta);
        if (pageSpreads.rightPage().contains(mouseX, mouseY))
            return pageSpreads.rightPage().mouseScrolled(mouseX, mouseY, wheelDelta);
        return false;
    }

    @Override
    public void open(Identifier id)
    {
        Identifier guideId = Identifiers.working(id).subIdentifier(0, 1).toIdentifier();
        Guide owningGuide = GuideManager.INSTANCE.getGuide(guideId);
        XmlEntry entry = owningGuide.getEntry(id);
        if (entry != null)
        {
            MinecraftClient.getInstance().openScreen(new OpenEntryScreen(session.openEntry(entry)));
            return;
        }
        TableOfContents toc = owningGuide.getTableOfContents(id);
        if (toc != null)
        {
            MinecraftClient.getInstance().openScreen(new OpenTableOfContentsScreen(session.openToC(toc)));
            return;
        }
        LOGGER.error("Could not open unknown entry or ToC {}", id);
    }

    @Override
    public void onClose()
    {
        session.end();
        for (Pair<GuideFlow, GuideFlow> spread : pageSpreads)
        {
            spread.getLeft().dispose();
            spread.getRight().dispose();
        }
        super.onClose();
    }

    @Override
    public Identifier getOpenGuideId()
    {
        return session.getGuide().getIdentifier();
    }
}
