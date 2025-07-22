package org.goober.linkmod.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.goober.linkmod.Linkmod;
import org.goober.linkmod.recipestuff.LatheRecipe;

import java.util.List;
import java.util.ArrayList;

// Simplified payload that just sends recipe data
public record SyncLatheRecipesPayload(List<RecipeData> recipes) implements CustomPayload {
    public static final CustomPayload.Id<SyncLatheRecipesPayload> ID = new CustomPayload.Id<>(Linkmod.SYNC_LATHE_RECIPES_ID);
    
    // Simple data holder for recipe information
    public record RecipeData(Identifier id, String group, Ingredient ingredient, ItemStack result) {}
    
    public static final PacketCodec<RegistryByteBuf, SyncLatheRecipesPayload> CODEC = PacketCodec.of(
        (value, buf) -> write(buf, value),
        buf -> read(buf)
    );
    
    private static void write(RegistryByteBuf buf, SyncLatheRecipesPayload payload) {
        buf.writeVarInt(payload.recipes.size());
        for (RecipeData data : payload.recipes) {
            // Write recipe data
            buf.writeIdentifier(data.id);
            buf.writeString(data.group);
            Ingredient.PACKET_CODEC.encode(buf, data.ingredient);
            ItemStack.PACKET_CODEC.encode(buf, data.result);
        }
    }
    
    private static SyncLatheRecipesPayload read(RegistryByteBuf buf) {
        int size = buf.readVarInt();
        List<RecipeData> recipes = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            // Read recipe data
            Identifier id = buf.readIdentifier();
            String group = buf.readString();
            Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
            ItemStack result = ItemStack.PACKET_CODEC.decode(buf);
            
            recipes.add(new RecipeData(id, group, ingredient, result));
        }
        
        return new SyncLatheRecipesPayload(recipes);
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}