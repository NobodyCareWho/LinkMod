package org.goober.linkmod.util;

import net.minecraft.entity.player.PlayerEntity;

public class ExperienceHelper {
    
    /**
     * Gets the player's total experience points
     * @param player The player entity
     * @return Total experience points as a string
     */
    public static String getPlayerTotalExp(PlayerEntity player) {
        if (player == null) {
            return "0";
        }
        
        // Calculate total experience from levels
        int totalExp = 0;
        
        // Add experience from completed levels
        for (int i = 0; i < player.experienceLevel; i++) {
            totalExp += getExpToNextLevel(i);
        }
        
        // Add current progress to next level
        totalExp += Math.round(player.experienceProgress * getExpToNextLevel(player.experienceLevel));
        
        return String.valueOf(totalExp);
    }
    
    /**
     * Gets the player's current experience level
     * @param player The player entity
     * @return Experience level as a string
     */
    public static String getPlayerLevel(PlayerEntity player) {
        if (player == null) {
            return "0";
        }
        return String.valueOf(player.experienceLevel);
    }
    
    /**
     * Gets experience required to reach the next level
     * Uses vanilla Minecraft experience formula
     */
    private static int getExpToNextLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else if (level >= 15) {
            return 37 + (level - 15) * 5;
        } else {
            return 7 + level * 2;
        }
    }
}