package org.goober.linkmod.gunstuff;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RecoilTracker {
    // tracks players who have recently fired guns with spatial recoil
    private static final Map<UUID, Long> recoilTimestamps = new ConcurrentHashMap<>();
    private static final long RECOIL_PROTECTION_DURATION = 3000; // 3 seconds in milliseconds
    private static long lastCleanupTime = System.currentTimeMillis();
    private static final long CLEANUP_INTERVAL = 5000; // cleanup every 5 seconds
    
    public static void markPlayerRecoil(PlayerEntity player) {
        recoilTimestamps.put(player.getUuid(), System.currentTimeMillis());
        performPeriodicCleanup();
    }
    
    public static boolean hasRecentRecoil(PlayerEntity player) {
        Long timestamp = recoilTimestamps.get(player.getUuid());
        if (timestamp == null) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - timestamp > RECOIL_PROTECTION_DURATION) {
            recoilTimestamps.remove(player.getUuid());
            return false;
        }
        
        return true;
    }
    
    public static void clearRecoil(PlayerEntity player) {
        recoilTimestamps.remove(player.getUuid());
    }
    
    private static void performPeriodicCleanup() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCleanupTime > CLEANUP_INTERVAL) {
            lastCleanupTime = currentTime;
            cleanupExpiredEntries();
        }
    }
    
    private static void cleanupExpiredEntries() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, Long>> iterator = recoilTimestamps.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Long> entry = iterator.next();
            if (currentTime - entry.getValue() > RECOIL_PROTECTION_DURATION) {
                iterator.remove();
            }
        }
    }
    
    public static void clearAll() {
        recoilTimestamps.clear();
    }
}