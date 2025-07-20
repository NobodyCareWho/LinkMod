package org.goober.linkmod.recipestuff;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;

public class LatheRecipeSerializer implements RecipeSerializer<LatheRecipe> {
    public static final MapCodec<LatheRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("group", "").forGetter(LatheRecipe::getGroup),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(LatheRecipe::getInput),
            ItemStack.CODEC.fieldOf("result").forGetter(LatheRecipe::getResultItem)
    ).apply(instance, LatheRecipe::new));

    public static final PacketCodec<RegistryByteBuf, LatheRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            LatheRecipeSerializer::write, LatheRecipeSerializer::read
    );

    @Override
    public MapCodec<LatheRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, LatheRecipe> packetCodec() {
        return PACKET_CODEC;
    }

    private static LatheRecipe read(RegistryByteBuf buf) {
        String group = buf.readString();
        Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
        ItemStack result = ItemStack.PACKET_CODEC.decode(buf);
        return new LatheRecipe(group, ingredient, result);
    }

    private static void write(RegistryByteBuf buf, LatheRecipe recipe) {
        buf.writeString(recipe.getGroup());
        Ingredient.PACKET_CODEC.encode(buf, recipe.getInput());
        ItemStack.PACKET_CODEC.encode(buf, recipe.getResultItem());
    }
}