package org.goober.linkmod.itemstuff;


import net.minecraft.component.ComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MaskItem extends Item{
    private static final String MASK_TEAM_NAME = "maskteam";
    
    public MaskItem(Item.Settings settings) {
        super(settings);
    }



    @Override
    public void inventoryTick(ItemStack stack,
                              ServerWorld world,
                              Entity entity,
                              @Nullable EquipmentSlot slot) {

        if (!(entity instanceof ServerPlayerEntity player)) return;

        boolean maskEquipped =
                player.getEquippedStack(EquipmentSlot.HEAD).getItem() == this;

        MinecraftServer server = player.getServer();
        if (server == null) return;

        Scoreboard scoreboard = server.getScoreboard();
        Team maskTeam = scoreboard.getTeam(MASK_TEAM_NAME);

        // make team if it doesn't exist
        if (maskTeam == null) {
            maskTeam = scoreboard.addTeam(MASK_TEAM_NAME);
            maskTeam.setShowFriendlyInvisibles(false);
            maskTeam.setNameTagVisibilityRule(Team.VisibilityRule.NEVER);
        }

        String playerName = player.getName().getString();
        boolean isOnTeam = maskTeam.getPlayerList().contains(playerName);

        // remove or add players if mask is equipped
        if (maskEquipped && !isOnTeam) {
            scoreboard.addScoreHolderToTeam(playerName, maskTeam);
        } else if (!maskEquipped && isOnTeam) {
            scoreboard.removeScoreHolderFromTeam(playerName, maskTeam);
        }
    }
}