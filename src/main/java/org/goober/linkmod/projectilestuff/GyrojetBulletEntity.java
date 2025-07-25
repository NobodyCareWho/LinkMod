package org.goober.linkmod.projectilestuff;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.goober.linkmod.entitystuff.LmodEntityRegistry;
import org.goober.linkmod.gunstuff.items.BulletItem;
import org.goober.linkmod.gunstuff.items.Bullets;
import org.goober.linkmod.soundstuff.LmodSoundRegistry;

public class GyrojetBulletEntity extends PersistentProjectileEntity implements DamageableProjectile {
    private float damage = 5.0F;
    private ItemStack bulletStack;

    public GyrojetBulletEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.bulletStack = ItemStack.EMPTY;
        this.setNoGravity(true);
    }

    public GyrojetBulletEntity(World world, LivingEntity owner, ItemStack bulletStack) {
        this(LmodEntityRegistry.BULLET, world);
        this.bulletStack = bulletStack.copy();
        this.setOwner(owner);
        this.setPosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        this.setNoGravity(true);
        this.setCritical(false);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        // return the bullet item stack if available, otherwise return a default bullet
        if (bulletStack != null && !bulletStack.isEmpty()) {
            return bulletStack.copy();
        }
        return new ItemStack(org.goober.linkmod.itemstuff.LmodItemRegistry.BULLET);

    }

    @Override
    public void tick() {
        super.tick();
        
        // exponentially increase velocity (on both client and server)
        if (!this.getWorld().isClient) {
            Vec3d currentVelocity = this.getVelocity();
            float accelerationFactor = 1.11f;
            Vec3d newVelocity = currentVelocity.multiply(accelerationFactor);
            
            // Here is a max cap for the velocity to prevent it from getting too fast, its optional but thought it'd fit
            double maxSpeed = 6.0;
            if (newVelocity.length() > maxSpeed) {
                newVelocity = newVelocity.normalize().multiply(maxSpeed);
            }
            
            this.setVelocity(newVelocity);
        }

        // add particle trail using particle profile
        if (this.getWorld() instanceof ServerWorld serverWorld && !bulletStack.isEmpty() && bulletStack.getItem() instanceof BulletItem bulletItem) {
            Bullets.BulletType bulletType = bulletItem.getBulletType();
            if (bulletType.particleprofile() != null) {
                Vec3d pos = this.getPos();
                Vec3d velocity = this.getVelocity();

                // create a trail of particles using trail particle
                if (bulletType.particleprofile().trailparticle() != null) {
                    for (int i = 0; i < 3; i++) {
                        double factor = i * 0.3;
                        serverWorld.spawnParticles(
                                bulletType.particleprofile().trailparticle(),
                                pos.x - velocity.x * factor,
                                pos.y - velocity.y * factor,
                                pos.z - velocity.z * factor,
                                1,
                                0.0, 0.0, 0.0,
                                0.01
                        );
                    }
                }

                // add bullet particles
                if (this.age % 2 == 0 && bulletType.particleprofile().bulletparticle() != null) {
                    serverWorld.spawnParticles(
                            bulletType.particleprofile().bulletparticle(),
                            pos.x, pos.y, pos.z,
                            1,
                            0.05, 0.05, 0.05,
                            0.02
                    );
                }
            }
        }



        // remove bullet after 6 seconds
        if (this.age > 120) {
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        int fun = random.nextInt(123) + 1; // Generates a random integer from 1 to 100

        // don't hit the owner
        if (entity == this.getOwner()) {
            return;
        }

        // calculate damage with bullet type multiplier
        float finalDamage = this.damage;
        if (!bulletStack.isEmpty() && bulletStack.getItem() instanceof BulletItem bulletItem) {
            Bullets.BulletType bulletType = bulletItem.getBulletType();
            // calculate velocity-based damage multiplier
            double velocityMagnitude = this.getVelocity().length();
            // assuming initial velocity is around 1.0, scale damage based on current velocity
            float velocityDamageMultiplier = (float)(velocityMagnitude / 1.0);
            finalDamage *= (bulletType.damageMultiplier() * velocityDamageMultiplier);

        }

        // deal damage
        DamageSource damageSource = this.getDamageSources().arrow(this, this.getOwner());
        if (this.getWorld() instanceof ServerWorld serverWorld) {

            if (entity instanceof PlayerEntity player && player.isBlocking()) {
                ItemStack activeItem = player.getActiveItem();
                if (activeItem.getItem() == Items.SHIELD) {
                    int shieldDamage = 1 + (int)(finalDamage / 5);
                    activeItem.damage(shieldDamage, player);
                }
            }
            entity.damage(serverWorld, damageSource, finalDamage);
            double velocityMagnitude = this.getVelocity().length();
            System.out.println("damage dealt: " + finalDamage + " (velocity: " + velocityMagnitude + ")");
            // remove immunity frames so shotgun pellets can all hit
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.hurtTime = 0;
                livingEntity.timeUntilRegen = 0;
            }
        }

        // add impact particles
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            Vec3d hitPos = entityHitResult.getPos();
            // use fire particle from profile for impact
            if (!bulletStack.isEmpty() && bulletStack.getItem() instanceof BulletItem bulletItem) {
                Bullets.BulletType bulletType = bulletItem.getBulletType();
                if (bulletType.particleprofile() != null && bulletType.particleprofile().entityimpactparticle() != null) {
                    serverWorld.spawnParticles(
                            bulletType.particleprofile().entityimpactparticle(),
                            hitPos.x, hitPos.y, hitPos.z,
                            5,
                            0.2, 0.2, 0.2,
                            0.1
                    );
                }
            }
        }

        // play impact sound from bullet sound profile
        if (!bulletStack.isEmpty() && bulletStack.getItem() instanceof BulletItem bulletItem) {
            Bullets.BulletType bulletType = bulletItem.getBulletType();
            if (bulletType.soundprofile() != null && bulletType.soundprofile().entityhitsound() != null) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        bulletType.soundprofile().entityhitsound(), SoundCategory.PLAYERS, 1.0F, 1.2F);
                if (fun==3) {
                    this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                            LmodSoundRegistry.JET2HOLIDAY, SoundCategory.PLAYERS, 1, 1);
                }
            }
        }

        this.discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        // add impact particles
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            Vec3d hitPos = blockHitResult.getPos();
            // use fire particle from profile for impact
            if (!bulletStack.isEmpty() && bulletStack.getItem() instanceof BulletItem bulletItem) {
                Bullets.BulletType bulletType = bulletItem.getBulletType();
                if (bulletType.particleprofile() != null && bulletType.particleprofile().impactparticle() != null) {
                    serverWorld.spawnParticles(
                            bulletType.particleprofile().impactparticle(),
                            hitPos.x, hitPos.y, hitPos.z,
                            10,
                            0.1, 0.1, 0.1,
                            0.05
                    );
                }
            }
        }

        // play impact sound from bullet sound profile
        if (!bulletStack.isEmpty() && bulletStack.getItem() instanceof BulletItem bulletItem) {
            Bullets.BulletType bulletType = bulletItem.getBulletType();
            if (bulletType.soundprofile() != null && bulletType.soundprofile().groundhitsound() != null) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        bulletType.soundprofile().groundhitsound(), SoundCategory.PLAYERS, 0.8F, 1.5F);
            }
        }

        this.discard();
    }


    @Override
    protected boolean tryPickup(PlayerEntity player) {
        // bullets can't be picked up
        return false;
    }
}