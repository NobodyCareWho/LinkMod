package org.goober.linkmod.gunstuff;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BloomTracker {
    // track bloom values per player per gun item
    private static final Map<UUID, Map<ItemStack, BloomData>> playerBloomData = new HashMap<>();
    
    public static class BloomData {
        public float currentBloom;
        public long lastShotTime;
        
        public BloomData(float bloom, long time) {
            this.currentBloom = bloom;
            this.lastShotTime = time;
        }
    }
    
    public static BloomData getBloomData(PlayerEntity player, ItemStack gunStack) {
        Map<ItemStack, BloomData> playerData = playerBloomData.computeIfAbsent(
            player.getUuid(), 
            k -> new HashMap<>()
        );
        
        // return existing bloom data or create new one
        return playerData.computeIfAbsent(gunStack, k -> new BloomData(0f, System.currentTimeMillis()));
    }
    
    public static void updateBloom(PlayerEntity player, ItemStack gunStack, float bloomIncrease, float maxBloom) {
        BloomData data = getBloomData(player, gunStack);
        
        // increase bloom up to max
        data.currentBloom = Math.min(data.currentBloom + bloomIncrease, maxBloom);
        data.lastShotTime = System.currentTimeMillis();
    }
    
    public static float getCurrentBloom(PlayerEntity player, ItemStack gunStack) {
        BloomData data = getBloomData(player, gunStack);
        return data.currentBloom;
    }
    
    public static void applyBloomDecay(PlayerEntity player, ItemStack gunStack, float bloomDecayRate) {
        BloomData data = getBloomData(player, gunStack);
        
        // calculate time-based decay
        long currentTime = System.currentTimeMillis();
        long timeSinceLastShot = currentTime - data.lastShotTime;
        float secondsSinceLastShot = timeSinceLastShot / 1000f;
        
        // decay bloom over time
        float decayAmount = secondsSinceLastShot * bloomDecayRate;
        data.currentBloom = Math.max(0, data.currentBloom - decayAmount);
        data.lastShotTime = currentTime; // update time to prevent multiple decay calculations
    }
    
    public static void clearPlayerData(PlayerEntity player) {
        playerBloomData.remove(player.getUuid());
    }
}