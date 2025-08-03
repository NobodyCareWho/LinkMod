package org.goober.linkmod;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.goober.linkmod.blockstuff.LmodBlockRegistry;
import org.goober.linkmod.blockstuff.LmodBlockEntityTypes;
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
import org.goober.linkmod.networking.SyncLatheRecipesPayload;
import org.goober.linkmod.networking.ExpChestOperationC2SPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import java.util.List;
import java.util.ArrayList;
import org.goober.linkmod.util.DebugConfig;
import org.goober.linkmod.util.ExpChestTracker;
import org.goober.linkmod.villagerstuff.LmodVillagerTrades;


public class Linkmod implements ModInitializer {
    public static final String MOD_ID = "lmod";
    public static final Identifier SYNC_LATHE_RECIPES_ID = Identifier.of(MOD_ID, "sync_lathe_recipes");
    @Override
    public void onInitialize() {
        LmodDataComponentTypes.initialize();
        LmodItemRegistry.initialize();
        LmodItemGroups.initialize();
        LmodEntityRegistry.initialize();
        LmodBlockRegistry.initialize();
        LmodBlockEntityTypes.initialize();
        LmodScreenHandlerType.initialize();
        LmodRecipeTypes.initialize();
        LmodRecipeSerializers.initialize();
        LmodSoundRegistry.initialize();
        LmodVillagerTrades.initialize();
        
        // Register network payloads
        PayloadTypeRegistry.playS2C().register(SyncLatheRecipesPayload.ID, SyncLatheRecipesPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ExpChestOperationC2SPacket.ID, ExpChestOperationC2SPacket.CODEC);
        ExpChestOperationC2SPacket.register();
        
        // Initialize exp chest tracker
        ExpChestTracker.initialize();
        
        // Register recipe loading callback
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                loadLatheRecipes(server);
            }
        });
        
        // Sync recipes to clients when they join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            syncLathRecipesToClient(handler.player);
        });

    }

    public static final AttachmentType<Integer> BANKED_EXP = AttachmentRegistry.create(
            Identifier.of("lmod", "banked_exp"),
            builder -> builder
                    .initializer(() -> 0)           // default value = 0
                    .persistent(Codec.INT)         // save to player NBT across restarts
                    .syncWith(PacketCodecs.VAR_INT, AttachmentSyncPredicate.targetOnly()) // sync to the owning player
                    .copyOnDeath()                 // (optional) keep value after player respawns:contentReference[oaicite:3]{index=3}
    );
    
    private void onServerStarted(MinecraftServer server) {
        loadLatheRecipes(server);
    }
    
    private void loadLatheRecipes(MinecraftServer server) {
        // Clear existing recipes
        LatheRecipeRegistry.clearRecipes();
        
        // Log total recipes
        DebugConfig.debug("Total recipes in manager: " + server.getRecipeManager().values().size());
        
        // Load all lathe recipes
        int latheCount = 0;
        for (RecipeEntry<?> entry : server.getRecipeManager().values()) {
            if (entry.value() instanceof LatheRecipe) {
                latheCount++;
                LatheRecipeRegistry.registerRecipe((RecipeEntry<LatheRecipe>) entry);
            }
        }
        DebugConfig.debug("Found " + latheCount + " lathe recipes");
        
        // Sync to all connected players after loading
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            syncLathRecipesToClient(player);
        }
    }
    
    private void syncLathRecipesToClient(ServerPlayerEntity player) {
        var allRecipes = LatheRecipeRegistry.getAllRecipes();
        List<SyncLatheRecipesPayload.RecipeData> recipeData = new ArrayList<>();
        
        for (RecipeEntry<LatheRecipe> entry : allRecipes) {
            LatheRecipe recipe = entry.value();
            recipeData.add(new SyncLatheRecipesPayload.RecipeData(
                entry.id().getValue(),
                recipe.getGroup(),
                recipe.getInput(),
                recipe.getResultItem()
            ));
        }
        
        ServerPlayNetworking.send(player, new SyncLatheRecipesPayload(recipeData));
    }

}
