package org.goober.linkmod.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.goober.linkmod.gunstuff.RecoilTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    
    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float reduceFallDamageFromRecoil(float amount, ServerWorld world, DamageSource source) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        
        // check if this is fall damage and the player has recent recoil
        if (source.isOf(DamageTypes.FALL) && RecoilTracker.hasRecentRecoil(player)) {
            // reduce fall damage by 80% for players with recent gun recoil
            return amount * 0.2f;
        }
        
        return amount;
    }
}