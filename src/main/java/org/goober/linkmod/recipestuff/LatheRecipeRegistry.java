package org.goober.linkmod.recipestuff;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.recipe.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LatheRecipeRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(LatheRecipeRegistry.class);
    
    // Store recipe data separately
    private static final Map<Identifier, LatheRecipe> RECIPE_MAP = new HashMap<>();
    private static final Map<Identifier, RegistryKey<Recipe<?>>> RECIPE_KEYS = new HashMap<>();
    
    // Keep original RecipeEntry objects when available (server-side)
    private static final List<RecipeEntry<LatheRecipe>> SERVER_RECIPE_ENTRIES = new ArrayList<>();
    
    private static CuttingRecipeDisplay.Grouping<LatheRecipe> EMPTY_GROUPING = CuttingRecipeDisplay.Grouping.empty();
    
    public static void registerRecipe(RecipeEntry<LatheRecipe> recipe) {
        Identifier id = recipe.id().getValue();
        RECIPE_MAP.put(id, recipe.value());
        RECIPE_KEYS.put(id, recipe.id());
        SERVER_RECIPE_ENTRIES.add(recipe);
        LOGGER.info("Registered lathe recipe: {} -> {}", recipe.value().getInput(), recipe.value().getResultItem());
    }
    
    public static void clearRecipes() {
        LOGGER.info("Clearing {} lathe recipes", RECIPE_MAP.size());
        RECIPE_MAP.clear();
        RECIPE_KEYS.clear();
        SERVER_RECIPE_ENTRIES.clear();
        RECIPE_TO_ID.clear();
    }
    
    // Keep track of recipe instances for client-side display
    private static final Map<LatheRecipe, Identifier> RECIPE_TO_ID = new HashMap<>();
    
    public static CuttingRecipeDisplay.Grouping<LatheRecipe> getFilteredRecipes(ItemStack input) {
        LOGGER.info("Filtering recipes for input: {}, total recipes: {}", input, RECIPE_MAP.size());
        if (input.isEmpty()) {
            return EMPTY_GROUPING;
        }
        
        List<CuttingRecipeDisplay.GroupEntry<LatheRecipe>> entries = new ArrayList<>();
        
        for (Map.Entry<Identifier, LatheRecipe> entry : RECIPE_MAP.entrySet()) {
            LatheRecipe recipe = entry.getValue();
            Identifier recipeId = entry.getKey();
            
            // Simple match check - just checks if the input ingredient matches
            if (recipe.getInput().test(input)) {
                // Store the mapping for later use
                RECIPE_TO_ID.put(recipe, recipeId);
                
                // Create display without RecipeEntry - the handler will use the recipe directly
                CuttingRecipeDisplay<LatheRecipe> display = new CuttingRecipeDisplay<>(
                    recipe.createResultDisplay(),
                    Optional.empty() // Will be handled differently in the screen handler
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
        
        for (LatheRecipe recipe : RECIPE_MAP.values()) {
            if (recipe.getInput().test(stack)) {
                return true;
            }
        }
        return false;
    }
    
    public static List<RecipeEntry<LatheRecipe>> getAllRecipes() {
        // Return the actual RecipeEntry objects if available (server-side)
        return new ArrayList<>(SERVER_RECIPE_ENTRIES);
    }
    
    // Method to register recipe from simple data (for client-side sync)
    public static void registerRecipeFromData(Identifier id, String group, net.minecraft.recipe.Ingredient ingredient, ItemStack result) {
        LatheRecipe recipe = new LatheRecipe(group, ingredient, result);
        RegistryKey<Recipe<?>> key = RegistryKey.of(RegistryKeys.RECIPE, id);
        
        // Store the recipe data without creating a RecipeEntry
        RECIPE_MAP.put(id, recipe);
        RECIPE_KEYS.put(id, key);
        LOGGER.info("Registered client-side lathe recipe: {} -> {}", ingredient, result);
    }
    
    // Get recipe by its position in the filtered list
    public static Optional<LatheRecipe> getRecipeByIndex(ItemStack input, int index) {
        if (input.isEmpty() || index < 0) {
            return Optional.empty();
        }
        
        List<LatheRecipe> matchingRecipes = new ArrayList<>();
        for (LatheRecipe recipe : RECIPE_MAP.values()) {
            if (recipe.getInput().test(input)) {
                matchingRecipes.add(recipe);
            }
        }
        
        if (index >= matchingRecipes.size()) {
            return Optional.empty();
        }
        
        return Optional.of(matchingRecipes.get(index));
    }
}