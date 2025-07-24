package org.goober.linkmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.goober.linkmod.blockstuff.LmodBlockRegistry;
import org.goober.linkmod.itemstuff.LmodItemRegistry;
import org.goober.linkmod.itemstuff.LmodDataComponentTypes;
import org.goober.linkmod.itemstuff.LmodItemGroups;
import org.goober.linkmod.entitystuff.LmodEntityRegistry;
import org.goober.linkmod.particlestuff.LmodParticleRegistry;
import org.goober.linkmod.soundstuff.LmodSoundRegistry;
import org.goober.linkmod.screenstuff.LmodScreenHandlerType;
import org.goober.linkmod.recipestuff.LmodRecipeTypes;
import org.goober.linkmod.recipestuff.LmodRecipeSerializers;
import org.goober.linkmod.recipestuff.LatheRecipe;
import org.goober.linkmod.recipestuff.LatheRecipeRegistry;


public class Linkmod implements ModInitializer {
    public static final String MOD_ID = "lmod";
    @Override
    public void onInitialize() {
        LmodDataComponentTypes.initialize();
        LmodItemRegistry.initialize();
        LmodItemGroups.initialize();
        LmodEntityRegistry.initialize();
        LmodBlockRegistry.initialize();
        LmodScreenHandlerType.initialize();
        LmodRecipeTypes.initialize();
        LmodRecipeSerializers.initialize();
        LmodEntityRegistry.initialize();
        LmodSoundRegistry.initialize();

        
        // Register recipe loading callback
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                loadLatheRecipes(server);
            }
        });

    }
    
    private void onServerStarted(MinecraftServer server) {
        loadLatheRecipes(server);
    }
    
    private void loadLatheRecipes(MinecraftServer server) {
        // Clear existing recipes
        LatheRecipeRegistry.clearRecipes();

        // Log total recipes
        System.out.println("Total recipes in manager: " + server.getRecipeManager().values().size());

        // Load all lathe recipes
        int latheCount = 0;
        for (RecipeEntry<?> entry : server.getRecipeManager().values()) {
            if (entry.value() instanceof LatheRecipe) {
                latheCount++;
                LatheRecipeRegistry.registerRecipe((RecipeEntry<LatheRecipe>) entry);
            }
        }
        System.out.println("Found " + latheCount + " lathe recipes");
    }

}
