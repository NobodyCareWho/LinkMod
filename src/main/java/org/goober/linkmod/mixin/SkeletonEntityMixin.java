package org.goober.linkmod.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.goober.linkmod.gunstuff.items.GunItem;
import org.goober.linkmod.gunstuff.items.Guns;
import org.goober.linkmod.gunstuff.items.Bullets;
import org.goober.linkmod.itemstuff.LmodItemRegistry;
import org.goober.linkmod.projectilestuff.BulletEntity;
import org.goober.linkmod.projectilestuff.DamageableProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSkeletonEntity.class)
public abstract class SkeletonEntityMixin {
    private int revolverAttackCooldown = 0;
    private int currentAmmo = -1; // -1 means not initialized
    private boolean isReloading = false;
    
    @Inject(method = "initEquipment", at = @At("TAIL"))
    private void giveRevolverToSomeSkeletons(Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
        // Only apply to regular skeletons, not wither skeletons
        if (((AbstractSkeletonEntity)(Object)this).getType() == EntityType.SKELETON && random.nextFloat() < 0.025f) {
            ItemStack revolver = new ItemStack(LmodItemRegistry.REVOLVER);
            ((AbstractSkeletonEntity)(Object)this).equipStack(EquipmentSlot.MAINHAND, revolver);
            ((AbstractSkeletonEntity)(Object)this).setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.085f);
            
            // Remove the bow if they had one
            if (((AbstractSkeletonEntity)(Object)this).getStackInHand(Hand.OFF_HAND).getItem() == Items.BOW) {
                ((AbstractSkeletonEntity)(Object)this).equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }
        }
    }
    
    @Inject(method = "canUseRangedWeapon", at = @At("HEAD"), cancellable = true)
    private void treatGunAsRangedWeapon(RangedWeaponItem weapon, CallbackInfoReturnable<Boolean> cir) {
        AbstractSkeletonEntity skeleton = (AbstractSkeletonEntity)(Object)this;
        ItemStack mainHand = skeleton.getStackInHand(Hand.MAIN_HAND);
        // Make the skeleton AI treat guns as ranged weapons like bows
        if (mainHand.getItem() instanceof GunItem) {
            cir.setReturnValue(true);
        }
    }
    
    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void tickRevolverAttack(CallbackInfo ci) {
        AbstractSkeletonEntity skeleton = (AbstractSkeletonEntity)(Object)this;
        if (!skeleton.getWorld().isClient && skeleton.isAlive()) {
            if (revolverAttackCooldown > 0) {
                revolverAttackCooldown--;
                if (isReloading && revolverAttackCooldown == 0) {
                    // Reload complete
                    isReloading = false;
                    ItemStack mainHand = skeleton.getStackInHand(Hand.MAIN_HAND);
                    if (mainHand.getItem() instanceof GunItem gunItem) {
                        Guns.GunType gunType = Guns.get(gunItem.getGunTypeId());
                        currentAmmo = gunType.maxAmmo();
                    }
                }
            }
            
            ItemStack mainHand = skeleton.getStackInHand(Hand.MAIN_HAND);
            if (mainHand.getItem() instanceof GunItem gunItem && revolverAttackCooldown == 0 && !isReloading) {
                // Initialize ammo if needed
                if (currentAmmo == -1) {
                    Guns.GunType gunType = Guns.get(gunItem.getGunTypeId());
                    currentAmmo = gunType.maxAmmo();
                }
                
                LivingEntity target = skeleton.getTarget();
                if (target != null && skeleton.canSee(target) && currentAmmo > 0) {
                    double distance = skeleton.squaredDistanceTo(target);
                    // Attack if within 20 blocks
                    if (distance < 400.0) {
                        skeleton.lookAtEntity(target, 30.0F, 30.0F);
                        this.shootAtTarget(target);
                        currentAmmo--;
                        
                        // Check if we need to reload
                        if (currentAmmo <= 0) {
                            isReloading = true;
                            revolverAttackCooldown = 80; // 4 second reload for skeletons
                        } else {
                            // Normal attack cooldown for revolver
                           // Guns.GunType gunType = Guns.get(gunItem.getGunTypeId());
                            revolverAttackCooldown = 20;
                        }
                    }
                }
            }
        }
    }
    
    private void shootAtTarget(LivingEntity target) {
        AbstractSkeletonEntity skeleton = (AbstractSkeletonEntity)(Object)this;
        ItemStack revolver = skeleton.getStackInHand(Hand.MAIN_HAND);
        if (revolver.getItem() instanceof GunItem gunItem && skeleton.getWorld() instanceof ServerWorld) {
            // Get gun type info
            Guns.GunType gunType = Guns.get(gunItem.getGunTypeId());
            
            // Get bullet type info for copper bullets
            Bullets.BulletType bulletType = Bullets.get("copper_bullet");
            
            // Create a copper bullet stack for projectile creation
            ItemStack bulletStack = new ItemStack(LmodItemRegistry.COPPER_BULLET);
            
            // Create bullet projectile directly (like skeleton arrows)
            BulletEntity projectile = new BulletEntity(skeleton.getWorld(), skeleton, bulletStack);
            
            // Set reduced damage for skeleton shots (60% of normal revolver damage)
            projectile.setDamage(gunType.damage() * 0.6f);
            
            // Calculate velocity and spread with skeleton accuracy
            Vec3d targetPos = target.getEyePos();
            Vec3d shooterPos = skeleton.getEyePos();
            Vec3d direction = targetPos.subtract(shooterPos).normalize();
            
            // Add some inaccuracy (less than piglins, skeletons are good shots)
            float spread = 0.5F;
            direction = direction.add(
                (skeleton.getRandom().nextFloat() - 0.5) * spread * 0.1,
                (skeleton.getRandom().nextFloat() - 0.5) * spread * 0.1,
                (skeleton.getRandom().nextFloat() - 0.5) * spread * 0.1
            ).normalize();
            
            // Set projectile velocity
            projectile.setVelocity(direction.x, direction.y, direction.z, gunType.velocity() * bulletType.vMultiplier(), spread);
            
            // Spawn the projectile
            skeleton.getWorld().spawnEntity(projectile);
            
            // Play gun sound
            if (bulletType.soundprofile() != null && bulletType.soundprofile().firesound() != null) {
                skeleton.getWorld().playSound(null, skeleton.getX(), skeleton.getY(), skeleton.getZ(),
                        bulletType.soundprofile().firesound(),
                        SoundCategory.HOSTILE, 0.8F, 1.2F + skeleton.getRandom().nextFloat() * 0.4F); // Slightly quieter and higher pitched
            }
            
            // Play attack animation
            skeleton.swingHand(Hand.MAIN_HAND);
        }
    }
}