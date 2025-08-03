package org.goober.linkmod.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.goober.linkmod.blockstuff.blockentities.ExpChestBlockEntity;
import org.goober.linkmod.screenstuff.ExpChestScreenHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExpChestTracker {
    // Track which exp chest each player has open
    private static final Map<UUID, BlockPos> openChests = new HashMap<>();
    
    public static void initialize() {
        // Handle player disconnect
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.player;
            
            // Check if player has an exp chest open
            if (player.currentScreenHandler instanceof ExpChestScreenHandler) {
                BlockPos chestPos = openChests.remove(player.getUuid());
                if (chestPos != null && player.getWorld() != null) {
                    BlockEntity blockEntity = player.getWorld().getBlockEntity(chestPos);
                    if (blockEntity instanceof ExpChestBlockEntity expChest) {
                        // Force close the chest
                        expChest.onClose(player);
                    }
                }
            }
        });
    }
    
    public static void trackOpenChest(ServerPlayerEntity player, BlockPos pos) {
        openChests.put(player.getUuid(), pos);
    }
    
    public static void untrackOpenChest(ServerPlayerEntity player) {
        openChests.remove(player.getUuid());
    }
}