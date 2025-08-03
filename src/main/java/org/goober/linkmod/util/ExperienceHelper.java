package org.goober.linkmod.util;

import net.minecraft.entity.player.PlayerEntity;
import org.goober.linkmod.Linkmod;

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
    
    /**
     * Gets the player's banked experience
     * @param player The player entity
     * @return Banked experience as a string
     */
    public static String getBankedExp(PlayerEntity player) {
        if (player == null) {
            return "0";
        }
        Integer banked = player.getAttached(Linkmod.BANKED_EXP);
        return String.valueOf(banked != null ? banked : 0);
    }
    
    /**
     * Sets the player's banked experience
     * @param player The player entity
     * @param amount The amount to set
     */
    public static void setBankedExp(PlayerEntity player, int amount) {
        if (player != null) {
            player.setAttached(Linkmod.BANKED_EXP, Math.max(0, amount));
        }
    }
    
    /**
     * Deposits experience from player to bank
     * @param player The player entity
     * @param amount The amount to deposit
     * @return true if successful, false if player doesn't have enough exp
     */
    public static boolean depositExp(PlayerEntity player, int amount) {
        if (player == null || amount <= 0) return false;
        
        int totalExp = Integer.parseInt(getPlayerTotalExp(player));
        if (totalExp < amount) return false;
        
        // Remove exp from player
        player.addExperience(-amount);
        
        // Add to bank
        int currentBanked = Integer.parseInt(getBankedExp(player));
        setBankedExp(player, currentBanked + amount);
        
        return true;
    }
    
    /**
     * Withdraws experience from bank to player
     * @param player The player entity
     * @param amount The amount to withdraw
     * @return true if successful, false if bank doesn't have enough exp
     */
    public static boolean withdrawExp(PlayerEntity player, int amount) {
        if (player == null || amount <= 0) return false;
        
        int bankedExp = Integer.parseInt(getBankedExp(player));
        if (bankedExp < amount) return false;
        
        // Remove from bank
        setBankedExp(player, bankedExp - amount);
        
        // Add to player
        player.addExperience(amount);
        
        return true;
    }
}