package org.goober.linkmod.recipestuff;

import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LmodRecipeTypes {
    public static final RecipeType<LatheRecipe> LATHE = Registry.register(
            Registries.RECIPE_TYPE,
            Identifier.of("lmod", "lathe"),
            new RecipeType<LatheRecipe>() {
                @Override
                public String toString() {
                    return "lmod:lathe";
                }
            }
    );

    public static void initialize() {

    }
}