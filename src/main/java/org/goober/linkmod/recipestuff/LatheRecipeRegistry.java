package org.goober.linkmod.recipestuff;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LatheRecipeRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(LatheRecipeRegistry.class);
    private static final List<RecipeEntry<LatheRecipe>> ALL_RECIPES = new ArrayList<>();
    private static CuttingRecipeDisplay.Grouping<LatheRecipe> EMPTY_GROUPING = CuttingRecipeDisplay.Grouping.empty();
    
    public static void registerRecipe(RecipeEntry<LatheRecipe> recipe) {
        ALL_RECIPES.add(recipe);
        LOGGER.info("Registered lathe recipe: {} -> {}", recipe.value().getInput(), recipe.value().getResultItem());
    }
    
    public static void clearRecipes() {
        LOGGER.info("Clearing {} lathe recipes", ALL_RECIPES.size());
        ALL_RECIPES.clear();
    }
    
    public static CuttingRecipeDisplay.Grouping<LatheRecipe> getFilteredRecipes(ItemStack input) {
        LOGGER.info("Filtering recipes for input: {}, total recipes: {}", input, ALL_RECIPES.size());
        if (input.isEmpty()) {
            return EMPTY_GROUPING;
        }
        
        List<CuttingRecipeDisplay.GroupEntry<LatheRecipe>> entries = new ArrayList<>();
        SingleStackRecipeInput recipeInput = new SingleStackRecipeInput(input);
        
        for (RecipeEntry<LatheRecipe> entry : ALL_RECIPES) {
            LatheRecipe recipe = entry.value();
            // Simple match check - just checks if the input ingredient matches
            if (recipe.getInput().test(input)) {
                CuttingRecipeDisplay<LatheRecipe> display = new CuttingRecipeDisplay<>(
                    recipe.createResultDisplay(),
                    Optional.of(entry)
                );
                entries.add(new CuttingRecipeDisplay.GroupEntry<>(recipe.getInput(), display));
            }
        }
        
        return entries.isEmpty() ? EMPTY_GROUPING : new CuttingRecipeDisplay.Grouping<>(entries);
    }
    
    public static boolean isValidInput(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        
        for (RecipeEntry<LatheRecipe> entry : ALL_RECIPES) {
            if (entry.value().getInput().test(stack)) {
                return true;
            }
        }
        return false;
    }
}