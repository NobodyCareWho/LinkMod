package org.goober.linkmod.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.goober.linkmod.gunstuff.items.GunItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    
    @Shadow
    private MinecraftClient client;
    
    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    private void adjustGunPosition(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (item.getItem() instanceof GunItem) {
            // Adjust the position to hold it like a crossbow
            boolean isMainHand = hand == Hand.MAIN_HAND;
            Arm arm = isMainHand ? player.getMainArm() : player.getMainArm().getOpposite();
            boolean isRightArm = arm == Arm.RIGHT;
            
            // Apply crossbow-like transformations
            if (isMainHand) {
                // Move gun to a more centered position like crossbow
                matrices.translate(isRightArm ? -0.1F : 0.1F, 0.15F, -0.3F);
                
                // Rotate to aim position
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-10.0F));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(isRightArm ? -5.0F : 5.0F));
            }
        }
    }
    
}