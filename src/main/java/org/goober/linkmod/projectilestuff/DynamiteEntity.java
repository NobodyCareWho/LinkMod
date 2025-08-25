package org.goober.linkmod.projectilestuff;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.goober.linkmod.entitystuff.LmodEntityRegistry;
import org.goober.linkmod.gunstuff.items.BulletItem;
import org.goober.linkmod.gunstuff.items.Grenades;
import org.goober.linkmod.itemstuff.LmodItemRegistry;

import java.util.Optional;

public class DynamiteEntity extends PersistentProjectileEntity implements DamageableProjectile {
    private static final TrackedData<Integer> REMAINING_BOUNCES = DataTracker.registerData(DynamiteEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private float damage = 15.0F;
    private ItemStack bulletStack;

    public DynamiteEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.bulletStack = ItemStack.EMPTY;
        this.setNoGravity(false);
        this.pickupType = PickupPermission.DISALLOWED; // can't be picked up
    }


    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(REMAINING_BOUNCES, 3);
    }

    public DynamiteEntity(World world, LivingEntity owner) {
        this(LmodEntityRegistry.DYNAMITE, world);
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

    public static final ExplosionBehavior NO_BLOCK_DAMAGE_BEHAVIOR = new ExplosionBehavior() {
        public Optional<BlockState> getDestroyedBlockState(
                Explosion explosion,
                BlockView world,
                BlockPos pos,
                BlockState state,
                float power
        ) {
            return Optional.empty();
        }
    };


    @Override
    protected ItemStack getDefaultItemStack() {
        // return the bullet item stack if available, otherwise return a default bullet
        if (bulletStack != null && !bulletStack.isEmpty()) {
            return bulletStack.copy();
        }
        return new ItemStack(LmodItemRegistry.BULLET);
    }
    
    @Override
    public void tick() {
        super.tick();

        // remove grenade after lifetime expires
        if (this.age > (4 * 20)) { // convert seconds to ticks
            if (this.getWorld() instanceof World world) {
                // use grenade type settings for explosion
                    world.createExplosion(this, getX(), getY(), getZ(), 5, false, World.ExplosionSourceType.MOB);
            }
            this.discard();
        }
    }
    
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        
        // don't hit the owner
        if (entity == this.getOwner() && age<=5) {
            return;
        }
        
        // calculate damage with grenade impact damage multiplier
        float finalDamage = this.damage;
        
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
            // use grenade type settings for explosion
                serverWorld.createExplosion(this, getX(), getY(), getZ(), 5, false, World.ExplosionSourceType.MOB);

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
        Vec3d velocityspeed = this.getVelocity();
        double speed = velocityspeed.length();
            if (speed > 0.2) {
                // calculate bounce velocity
                Vec3d velocity = this.getVelocity();
                Vec3d normal = Vec3d.of(blockHitResult.getSide().getVector());

                // reflect velocity off the surface
                Vec3d newVelocity = velocity.subtract(normal.multiply(2 * velocity.dotProduct(normal)));

                // reduce velocity on bounce based on grenade bounciness
                newVelocity = newVelocity.multiply(0.8);

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
    protected float getDragInWater() {
        // grenades move slower in water
        return 0.8F;
    }
    

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        // bullets can't be picked up
        return false;
    }

}