package org.goober.linkmod.projectilestuff;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
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

public class PillGrenadeEntity extends PersistentProjectileEntity implements DamageableProjectile {
    private float damage = 15.0F;
    private ItemStack bulletStack;
    private int remainingBounces = 3; // number of bounces before exploding

    public PillGrenadeEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.bulletStack = ItemStack.EMPTY;
        this.setNoGravity(false);
        this.pickupType = PickupPermission.DISALLOWED; // can't be picked up
    }

    public PillGrenadeEntity(World world, LivingEntity owner, ItemStack bulletStack) {
        this(LmodEntityRegistry.PILLGRENADE, world);
        this.bulletStack = bulletStack.copy();
        this.setOwner(owner);
        this.setPosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        this.setNoGravity(false); // enable gravity for bouncing
        this.setCritical(false);
        this.pickupType = PickupPermission.DISALLOWED; // can't be picked up
        this.setNoClip(false); // ensure collision is enabled
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
        // remove after 10 seconds to prevent entities from existing forever
        if (this.age > 200) {
            this.discard();
        }
    }
    
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        
        // don't hit the owner
        if (entity == this.getOwner()) {
            return;
        }
        
        // calculate damage with bullet type multiplier
        float finalDamage = this.damage;
        if (!bulletStack.isEmpty() && bulletStack.getItem() instanceof BulletItem bulletItem) {
            Bullets.BulletType bulletType = bulletItem.getBulletType();
            finalDamage *= bulletType.damageMultiplier();
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
            // remove immunity frames so shotgun pellets can all hit
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.hurtTime = 0;
                livingEntity.timeUntilRegen = 0;
            }
        }
        
        // add impact particles
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            Vec3d hitPos = entityHitResult.getPos();
            serverWorld.spawnParticles(
                ParticleTypes.DAMAGE_INDICATOR,
                hitPos.x, hitPos.y, hitPos.z,
                5,
                0.2, 0.2, 0.2,
                0.1
            );
        }
        
        // play impact sound
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), 
            SoundEvents.ENTITY_ARROW_HIT, SoundCategory.PLAYERS, 1.0F, 1.2F);

        
        this.discard();
    }
    
    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        // decrement bounce counter
        this.remainingBounces--;
        
        if (this.remainingBounces <= 0) {
            // mo more bounces, just stop
            this.discard();
        } else {
            
            // calculate bounce velocity
            Vec3d velocity = this.getVelocity();
            Vec3d normal = Vec3d.of(blockHitResult.getSide().getVector());
            
            // reflect velocity off the surface
            Vec3d newVelocity = velocity.subtract(normal.multiply(2 * velocity.dotProduct(normal)));
            
            // reduce velocity on bounce (energy loss)
            newVelocity = newVelocity.multiply(0.6);
            
            // add a small upward component to prevent sliding
            if (Math.abs(newVelocity.y) < 0.1 && blockHitResult.getSide().getAxis().isHorizontal()) {
                newVelocity = newVelocity.add(0, 0.2, 0);
            }
            
            // set the new velocity
            this.setVelocity(newVelocity);
            
            // move the grenade away from the surface to prevent getting stuck
            Vec3d hitPos = blockHitResult.getPos();
            Vec3d currentPos = this.getPos();
            
            // calculate a safe position away from the collision point
            Vec3d safePos = hitPos.add(normal.multiply(0.3));
            this.setPosition(safePos.x, safePos.y, safePos.z);
        }
    }
    
    @Override
    protected boolean canHit(Entity entity) {
        // can hit entities but not the owner immediately after firing
        return super.canHit(entity) && (this.age > 5 || entity != this.getOwner());
    }
    
    @Override
    public boolean isInGround() {
        // never consider the grenade as "in ground" to prevent it from getting stuck
        return false;
    }
    
    @Override
    protected boolean tryPickup(PlayerEntity player) {
        // bullets can't be picked up
        return false;
    }
}