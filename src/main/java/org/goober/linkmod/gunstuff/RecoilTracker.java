package org.goober.linkmod.gunstuff;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RecoilTracker {
    // tracks players who have recently fired guns with spatial recoil
    private static final Map<UUID, Long> recoilTimestamps = new HashMap<>();
    private static final long RECOIL_PROTECTION_DURATION = 3000; // 3 seconds in milliseconds
    
    public static void markPlayerRecoil(PlayerEntity player) {
        recoilTimestamps.put(player.getUuid(), System.currentTimeMillis());
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
}