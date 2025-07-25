package org.goober.linkmod.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinEntity.class)
public abstract class PiglinEntityMixin extends MobEntity {
    private int rifleAttackCooldown = 0;
    private int currentAmmo = -1; // -1 means not initialized
    private boolean isReloading = false;
    
    protected PiglinEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }
    
    @Inject(method = "initEquipment", at = @At("TAIL"))
    private void giveRifleToSomePiglins(Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
        // 15% chance to spawn with a boiler pistol
        if (this.random.nextFloat() < 0.15f) {
            ItemStack boilerpistol = new ItemStack(LmodItemRegistry.BOILERPISTOL);
            this.equipStack(EquipmentSlot.MAINHAND, boilerpistol);
            this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.085f);
        }
    }
    
    @Inject(method = "mobTick", at = @At("HEAD"))
    private void maintainRangedDistance(CallbackInfo ci) {
        PiglinEntity piglin = (PiglinEntity)(Object)this;
        ItemStack mainHand = piglin.getStackInHand(Hand.MAIN_HAND);
        if (mainHand.getItem() instanceof GunItem) {
            LivingEntity target = piglin.getTarget();
            if (target != null) {
                double distance = piglin.squaredDistanceTo(target);
                // If too close (within 6 blocks), back away
                if (distance < 36.0) {
                    piglin.getNavigation().stop();
                    // Move away from target
                    Vec3d awayVector = piglin.getPos().subtract(target.getPos()).normalize().multiply(0.3);
                    piglin.setVelocity(awayVector.x, piglin.getVelocity().y, awayVector.z);
                    piglin.velocityModified = true;
                }
            }
        }
    }

    @Inject(method = "canUseRangedWeapon", at = @At("HEAD"), cancellable = true)
    private void treatGunAsCrossbow(CallbackInfoReturnable<Boolean> cir) {
        PiglinEntity piglin = (PiglinEntity)(Object)this;
        ItemStack mainHand = piglin.getStackInHand(Hand.MAIN_HAND);
        if (mainHand.getItem() instanceof GunItem) {
            // Make piglin AI think it's holding a crossbow
            cir.setReturnValue(true);
        }
    }
    
    @Inject(method = "mobTick", at = @At("TAIL"))
    private void tickRifleAttack(CallbackInfo ci) {
        if (!this.getWorld().isClient && this.isAlive()) {
            if (rifleAttackCooldown > 0) {
                rifleAttackCooldown--;
                if (isReloading && rifleAttackCooldown == 0) {
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
            if (mainHand.getItem() instanceof GunItem gunItem && rifleAttackCooldown == 0 && !isReloading) {
                // Initialize ammo if needed
                if (currentAmmo == -1) {
                    Guns.GunType gunType = Guns.get(gunItem.getGunTypeId());
                    currentAmmo = gunType.maxAmmo();
                }
                
                LivingEntity target = this.getTarget();
                if (target != null && this.canSee(target) && currentAmmo > 0) {
                    double distance = this.squaredDistanceTo(target);
                    // Attack if within 20 blocks
                    if (distance < 400.0) {
                        this.lookAtEntity(target, 30.0F, 30.0F);
                        this.shootAtTarget(target);
                        currentAmmo--;
                        
                        // Check if we need to reload
                        if (currentAmmo <= 0) {
                            isReloading = true;
                            rifleAttackCooldown = 65; // 3.25 second reload for regular piglins
                        } else {
                            // Normal attack cooldown
                            Guns.GunType gunType = Guns.get(gunItem.getGunTypeId());
                            rifleAttackCooldown = gunType.cooldownTicks();
                        }
                    }
                }
            }
        }
    }
    
    private void shootAtTarget(LivingEntity target) {
        ItemStack rifle = this.getStackInHand(Hand.MAIN_HAND);
        if (rifle.getItem() instanceof GunItem gunItem && this.getWorld() instanceof ServerWorld) {
            // Get gun type info
            Guns.GunType gunType = Guns.get(gunItem.getGunTypeId());
            
            // Get bullet type info for copper bullets
            Bullets.BulletType bulletType = Bullets.get("copper_bullet");
            
            // Create a copper bullet stack for projectile creation
            ItemStack bulletStack = new ItemStack(LmodItemRegistry.COPPER_BULLET);
            
            // Create bullet projectile directly (like skeleton arrows)
            BulletEntity projectile = new BulletEntity(this.getWorld(), this, bulletStack);
            
            // Set reduced damage for piglin shots
            projectile.setDamage(gunType.damage());
            
            // Calculate velocity and spread
            Vec3d targetPos = target.getEyePos();
            Vec3d shooterPos = this.getEyePos();
            Vec3d direction = targetPos.subtract(shooterPos).normalize();
            
            // Add some inaccuracy 
            float spread = 1.0F;
            direction = direction.add(
                (this.random.nextFloat() - 0.5) * spread * 0.1,
                (this.random.nextFloat() - 0.5) * spread * 0.1,
                (this.random.nextFloat() - 0.5) * spread * 0.1
            ).normalize();
            
            // Set projectile velocity
            projectile.setVelocity(direction.x, direction.y, direction.z, gunType.velocity() * bulletType.vMultiplier(), spread);
            
            // Spawn the projectile
            this.getWorld().spawnEntity(projectile);
            
            // Play gun sound
            if (bulletType.soundprofile() != null && bulletType.soundprofile().firesound() != null) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        bulletType.soundprofile().firesound(),
                        SoundCategory.HOSTILE, 1.0F, 1.0F / (this.random.nextFloat() * 0.4F + 0.8F));
            }
            
            // Play attack animation
            this.swingHand(Hand.MAIN_HAND);
        }
    }
}