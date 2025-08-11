package org.goober.linkmod.mixin.client;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.goober.linkmod.itemstuff.MaskItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    
    @Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true)
    private void hideNametagWhenWearingMask(T entity, double distance, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof PlayerEntity player) {
            ItemStack headStack = player.getEquippedStack(EquipmentSlot.HEAD);
            if (!headStack.isEmpty() && headStack.getItem() instanceof MaskItem) {
                cir.setReturnValue(false);
            }
        }
    }
}