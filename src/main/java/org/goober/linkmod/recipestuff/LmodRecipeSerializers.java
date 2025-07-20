package org.goober.linkmod.recipestuff;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LmodRecipeSerializers {
    public static final RecipeSerializer<LatheRecipe> LATHE = Registry.register(
            Registries.RECIPE_SERIALIZER, 
            Identifier.of("lmod", "lathe"), 
            new LatheRecipeSerializer()
    );

    public static void initialize() {
        // Force class loading
    }
}
