package org.goober.linkmod.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBruteEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
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
import org.goober.linkmod.projectilestuff.SparkBulletEntity;
import org.goober.linkmod.projectilestuff.DamageableProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBruteEntity.class)
public abstract class PiglinBruteEntityMixin extends MobEntity {
    private int shotgunAttackCooldown = 0;
    private int currentAmmo = -1; // -1 means not initialized
    private boolean isReloading = false;
    
    protected PiglinBruteEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }
    
    @Inject(method = "initEquipment", at = @At("TAIL"))
    private void giveShotgunToBrutes(Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
        // 33% chance for brutes to spawn with a DB shotgun
        if (this.random.nextFloat() < 0.33f) {
            ItemStack shotgun = new ItemStack(LmodItemRegistry.SHOTGUN);
            this.equipStack(EquipmentSlot.MAINHAND, shotgun);
            this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.085f);
        }
    }
    
    @Inject(method = "mobTick", at = @At("HEAD"))
    private void overrideBruteAI(CallbackInfo ci) {
        PiglinBruteEntity brute = (PiglinBruteEntity)(Object)this;
        ItemStack mainHand = brute.getStackInHand(Hand.MAIN_HAND);
        if (mainHand.getItem() instanceof GunItem) {
            LivingEntity target = brute.getTarget();
            if (target != null) {
                double distance = brute.squaredDistanceTo(target);
                
                // If too far (more than 10 blocks), move closer
                if (distance > 100.0 && distance < 400.0) {
                    brute.getNavigation().startMovingTo(target, 1.0);
                } 
                // If too close (less than 6 blocks), back away
                else if (distance < 36.0) {
                    brute.getNavigation().stop();
                    Vec3d direction = brute.getPos().subtract(target.getPos()).normalize();
                    Vec3d newPos = brute.getPos().add(direction.multiply(2));
                    brute.getNavigation().startMovingTo(newPos.x, newPos.y, newPos.z, 1.2);
                }
                // In range, stop and face target
                else {
                    brute.getNavigation().stop();
                    brute.lookAtEntity(target, 30.0F, 30.0F);
                }
            }
        }
    }

    @Inject(method = "mobTick", at = @At("HEAD"))
    private void preventMeleeWithGun(CallbackInfo ci) {
        PiglinBruteEntity brute = (PiglinBruteEntity)(Object)this;
        ItemStack mainHand = brute.getStackInHand(Hand.MAIN_HAND);

        if (mainHand.getItem() instanceof GunItem) {
            // Clear target or stop attacking
            brute.setTarget(null);
            brute.setAttacking(false);
        }
    }

    @Inject(method = "mobTick", at = @At("TAIL"))
    private void tickShotgunAttack(CallbackInfo ci) {
        if (!this.getWorld().isClient && this.isAlive()) {
            if (shotgunAttackCooldown > 0) {
                shotgunAttackCooldown--;
                if (isReloading && shotgunAttackCooldown == 0) {
                    // Reload complete
                    isReloading = false;
                    ItemStack mainHand = this.getStackInHand(Hand.MAIN_HAND);
                    if (mainHand.getItem() instanceof GunItem gunItem) {
                        Guns.GunType gunType = Guns.get(gunItem.getGunTypeId());
                        currentAmmo = gunType.maxAmmo();
                    }
                }
            }
            
            ItemStack mainHand = this.getStackInHand(Hand.MAIN_HAND);
            if (mainHand.getItem() instanceof GunItem gunItem && shotgunAttackCooldown == 0 && !isReloading) {
                // Initialize ammo if needed
                if (currentAmmo == -1) {
                    Guns.GunType gunType = Guns.get(gunItem.getGunTypeId());
                    currentAmmo = gunType.maxAmmo();
                }
                
                LivingEntity target = this.getTarget();
                if (target != null && this.canSee(target) && currentAmmo > 0) {
                    double distance = this.squaredDistanceTo(target);
                    // Attack if within 10 blocks (shorter range for shotgun)
                    if (distance < 100.0) {
                        this.lookAtEntity(target, 30.0F, 30.0F);
                        this.shootAtTarget(target);
                        currentAmmo--;
                        
                        // Check if we need to reload
                        if (currentAmmo <= 0) {
                            isReloading = true;
                            shotgunAttackCooldown = 60; // 3 second reload for brutes
                        } else {
                            // Fixed attack cooldown for brutes
                            shotgunAttackCooldown = 15; // 0.75 seconds between shots
                        }
                    }
                }
            }
        }
    }
    
    private void shootAtTarget(LivingEntity target) {
        ItemStack shotgun = this.getStackInHand(Hand.MAIN_HAND);
        if (shotgun.getItem() instanceof GunItem gunItem && this.getWorld() instanceof ServerWorld) {
            // Get gun type info
            Guns.GunType gunType = Guns.get(gunItem.getGunTypeId());
            
            // Get bullet type info for buckshot
            Bullets.BulletType bulletType = Bullets.get("blazeshot");
            
            // Create a buckshot stack for projectile creation
            ItemStack bulletStack = new ItemStack(LmodItemRegistry.BLAZESHELL);
            
            // Shotguns shoot multiple pellets, so we need to handle that
            float maxSpread = gunType.baseInaccuracy() + bulletType.baseSpreadIncrease();
            
            for (int i = 0; i < bulletType.pelletsPerShot(); i++) {
                // Create spark bullet projectile for fire damage
                SparkBulletEntity projectile = new SparkBulletEntity(this.getWorld(), this, bulletStack);
                
                // Set reduced damage for brute shots (70% of normal shotgun damage per pellet)
                projectile.setDamage(gunType.damage() * 0.7f);
                
                // Calculate velocity and spread for each pellet
                Vec3d targetPos = target.getEyePos();
                Vec3d shooterPos = this.getEyePos();
                Vec3d direction = targetPos.subtract(shooterPos).normalize();
                
                // Add shotgun spread
                float spread = this.random.nextFloat() * maxSpread;
                direction = direction.add(
                    (this.random.nextFloat() - 0.5) * spread * 0.1,
                    (this.random.nextFloat() - 0.5) * spread * 0.1,
                    (this.random.nextFloat() - 0.5) * spread * 0.1
                ).normalize();
                
                // Set projectile velocity
                projectile.setVelocity(direction.x, direction.y, direction.z, gunType.velocity() * bulletType.vMultiplier(), spread);
                
                // Spawn the projectile
                this.getWorld().spawnEntity(projectile);
            }
            
            // Play gun sound once for all pellets
            if (bulletType.soundprofile() != null && bulletType.soundprofile().firesound() != null) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        bulletType.soundprofile().firesound(),
                        SoundCategory.HOSTILE, 1.2F, 0.8F); // Louder and deeper for brutes
            }
            
            // Play attack animation
            this.swingHand(Hand.MAIN_HAND);
        }
    }
}