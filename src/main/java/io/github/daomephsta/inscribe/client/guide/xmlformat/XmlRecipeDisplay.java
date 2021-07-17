package io.github.daomephsta.inscribe.client.guide.xmlformat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.daomephsta.inscribe.client.guide.gui.widget.CraftingRecipeDisplayWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.GuideWidget;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.Alignment;
import io.github.daomephsta.inscribe.client.guide.gui.widget.layout.GuideFlow;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.FormattedTextNode;
import io.github.daomephsta.inscribe.client.guide.gui.widget.text.TextBlockWidget;
import io.github.daomephsta.inscribe.common.Inscribe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public record XmlRecipeDisplay(Identifier recipeId) implements XmlGuideGuiElement
{
    private static final Logger LOGGER = LogManager.getLogger(Inscribe.MOD_ID);
    
    @Override
    public void acceptPage(GuideFlow output)
    {
        MinecraftClient.getInstance().world.getRecipeManager().get(recipeId)
            .map(this::createRecipeDisplay)
            .ifPresentOrElse(output::add, () -> LOGGER.error("Unknown recipe {}", recipeId));
    }
    
    private GuideWidget createRecipeDisplay(Recipe<?> recipe)
    {
        if (recipe instanceof CraftingRecipe crafting)
            return new CraftingRecipeDisplayWidget(crafting);
        else
        {
            LOGGER.error("Cannot create a widget for recipe class {}", recipe.getClass().getName());
            return new TextBlockWidget(Alignment.CENTER, Alignment.CENTER, 
                new FormattedTextNode("ERR", MinecraftClient.DEFAULT_FONT_ID, 0xFF0000));
        }           
    }
}
