package io.github.daomephsta.inscribe.client.guide.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.daomephsta.inscribe.client.InscribeRenderLayers;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

public class CraftingRecipeDisplayWidget extends GuideWidget
{
    private static final int STACK_CHANGE_SPEED = 30,
                             WIDTH = 111,
                             HEIGHT = 56;
    private static final float LEFT = 1F,
                               TOP = 289F;
    private final CraftingRecipe recipe;

    public CraftingRecipeDisplayWidget(CraftingRecipe recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void renderWidget(VertexConsumerProvider vertexConsumers, MatrixStack matrices, int mouseX, int mouseY, float lastFrameDuration)
    {
        Identifier guiTexture = getGuide().getTheme().getGuiTexture();
        VertexConsumer vertices = vertexConsumers.getBuffer(InscribeRenderLayers.textureQuads(guiTexture));
        int originX = MathHelper.floor(Alignment.CENTER.offsetX(x(), this, WIDTH));
        matrices.push();
        matrices.translate(originX, y() + padding().top(), 0);
        // Slots
        RenderSystem.setShaderTexture(0, guiTexture);
        Matrix4f model = matrices.peek().getModel();
        vertices.vertex(model, 0, 0, 0).texture(LEFT / 440F, (TOP - HEIGHT) / 290F).next();
        vertices.vertex(model, 0, HEIGHT, 0).texture(LEFT / 440F, TOP / 290F).next();
        vertices.vertex(model, WIDTH, HEIGHT, 0).texture((LEFT + WIDTH) / 440F, TOP / 290F).next();
        vertices.vertex(model, WIDTH, 0, 0).texture((LEFT + WIDTH) / 440F, (TOP - HEIGHT) / 290F).next();
        matrices.pop();
        // Items
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        int time = MinecraftClient.getInstance().player.age;
        DefaultedList<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++)
        {
            int x = originX + (i % 3) * 19 + 1,
                y = y() + i / 3 * 19 + 1;
            Ingredient ingredient = ingredients.get(i);
            ItemStack[] stacks = ingredient.getMatchingStacksClient();
            if (stacks.length != 0)
                itemRenderer.renderGuiItemIcon(stacks[time / STACK_CHANGE_SPEED % stacks.length], x, y);
        }
        itemRenderer.renderGuiItemIcon(recipe.getOutput(), originX + 94, y() + 20);
    }

    @Override
    public int hintHeight()
    {
        return HEIGHT;
    }

    @Override
    public int hintWidth()
    {
        return WIDTH;
    }
}
