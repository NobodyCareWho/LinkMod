package org.goober.linkmod.recipestuff;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class LatheRecipe implements Recipe<SingleStackRecipeInput> {
    private final Ingredient input;
    private final ItemStack output;
    private final String group;

    public LatheRecipe(String group, Ingredient input, ItemStack output) {
        this.group = group;
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(SingleStackRecipeInput input, World world) {
        return this.input.test(input.item());
    }

    @Override
    public ItemStack craft(SingleStackRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return this.output.copy();
    }


    @Override
    public String getGroup() {
        return this.group;
    }

    public Ingredient getInput() {
        return this.input;
    }

    public ItemStack getResultItem() {
        return this.output;
    }

    public SlotDisplay createResultDisplay() {
        return new SlotDisplay.StackSlotDisplay(this.output);
    }

    @Override
    public RecipeSerializer<LatheRecipe> getSerializer() {
        return LmodRecipeSerializers.LATHE;
    }

    @Override
    public RecipeType<LatheRecipe> getType() {
        return LmodRecipeTypes.LATHE;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        // stone-cutting always uses one slot
        return IngredientPlacement.forSingleSlot(this.input);
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return RecipeBookCategories.STONECUTTER;
    }
}