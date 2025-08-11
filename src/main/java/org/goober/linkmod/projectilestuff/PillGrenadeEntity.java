package org.goober.linkmod.projectilestuff;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
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
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import org.goober.linkmod.entitystuff.LmodEntityRegistry;
import org.goober.linkmod.gunstuff.items.BulletItem;
import org.goober.linkmod.gunstuff.items.Bullets;
import org.goober.linkmod.gunstuff.items.Grenades;
import org.goober.linkmod.itemstuff.LmodItemRegistry;

import java.util.Optional;

public class PillGrenadeEntity extends PersistentProjectileEntity implements DamageableProjectile {
    private static final TrackedData<String> GRENADE_TYPE_ID = DataTracker.registerData(PillGrenadeEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> REMAINING_BOUNCES = DataTracker.registerData(PillGrenadeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    
    private float damage = 15.0F;
    private ItemStack bulletStack;
    private Grenades.GrenadeType grenadeType = Grenades.get("standard"); // default grenade type

    public PillGrenadeEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.bulletStack = ItemStack.EMPTY;
        this.setNoGravity(false);
        this.pickupType = PickupPermission.DISALLOWED; // can't be picked up
    }
    
    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        // when grenade type ID is set/changed, update the grenade type
        if (data.equals(GRENADE_TYPE_ID)) {
            String typeId = this.dataTracker.get(GRENADE_TYPE_ID);
            Grenades.GrenadeType type = Grenades.get(typeId);
            if (type != null) {
                this.grenadeType = type;
            }
        }
    }
    
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(GRENADE_TYPE_ID, "standard");
        builder.add(REMAINING_BOUNCES, 3);
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
        
        // get grenade type from item stack
        Grenades.GrenadeType type = Grenades.getFromItemStack(bulletStack);
        if (type != null) {
            this.grenadeType = type;
            this.dataTracker.set(REMAINING_BOUNCES, grenadeType.bounces());
        } else {
            // fallback to standard if not a valid grenade type
            this.grenadeType = Grenades.get("standard");
            this.dataTracker.set(REMAINING_BOUNCES, this.grenadeType != null ? this.grenadeType.bounces() : 3);
        }
        
        // store the grenade type ID for rendering (synced to client)
        if (bulletStack.getItem() instanceof BulletItem bulletItem) {
            String typeId = bulletItem.getBulletTypeId();
            this.dataTracker.set(GRENADE_TYPE_ID, typeId);
        }
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
        if (this.age > (grenadeType.lifetime() * 20)) { // convert seconds to ticks
            if (this.getWorld() instanceof World world) {
                // use grenade type settings for explosion
                if (grenadeType.destroysTerrain()) {
                    world.createExplosion(this, getX(), getY(), getZ(), grenadeType.explosionSize(), grenadeType.createsFire(), World.ExplosionSourceType.MOB);
                } else {
                    // Use NONE source type to prevent block damage
                    // spawn big explosion particles for non-destructive grenades
                    if (world instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
                    }
                    world.createExplosion(this, getX(), getY(), getZ(), grenadeType.explosionSize(), grenadeType.createsFire(), World.ExplosionSourceType.NONE);
                }
            }
            this.discard();
        }
    }
    
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        
        // don't hit the owner
        if (entity == this.getOwner() && this.getRemainingBounces() == grenadeType.bounces()) {
            return;
        }
        
        // calculate damage with grenade impact damage multiplier
        float finalDamage = this.damage;
        if (grenadeType != null) {
            finalDamage *= grenadeType.impactDamageMultiplier();
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
            // use grenade type settings for explosion
            if (grenadeType.destroysTerrain()) {
                serverWorld.createExplosion(this, getX(), getY(), getZ(), grenadeType.explosionSize(), grenadeType.createsFire(), World.ExplosionSourceType.MOB);
            } else {
                // Use NONE source type to prevent block damage
                // spawn big explosion particles for non-destructive grenades
                serverWorld.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
                serverWorld.createExplosion(this, getX(), getY(), getZ(), grenadeType.explosionSize(), grenadeType.createsFire(), World.ExplosionSourceType.NONE);
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
        // Only handle bounces on the server side
        if (this.getWorld().isClient) {
            return;
        }
        
        // decrement bounce counter (synced to client)
        int bounces = this.dataTracker.get(REMAINING_BOUNCES) - 1;
        this.dataTracker.set(REMAINING_BOUNCES, bounces);
        
        if (bounces <= 0) {
            // no more bounces, explode
            if (grenadeType.destroysTerrain()) {
                // spawn big explosion particles for destructive grenades (server-side only)
                this.getWorld().createExplosion(this, getX(), getY(), getZ(), grenadeType.explosionSize(), grenadeType.createsFire(), World.ExplosionSourceType.MOB);
            } else {
                // Use NONE source type to prevent block damage
                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
                }
                this.getWorld().createExplosion(this, getX(), getY(), getZ(), grenadeType.explosionSize(), grenadeType.createsFire(), World.ExplosionSourceType.NONE);
            }
            this.discard();
        } else {
            // calculate bounce velocity
            Vec3d velocity = this.getVelocity();
            Vec3d normal = Vec3d.of(blockHitResult.getSide().getVector());

            // reflect velocity off the surface
            Vec3d newVelocity = velocity.subtract(normal.multiply(2 * velocity.dotProduct(normal)));

            // reduce velocity on bounce based on grenade bounciness
            newVelocity = newVelocity.multiply(grenadeType.bounciness());

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
    
    // getter for grenade type ID (used by renderer)
    public String getGrenadeTypeId() {
        return this.dataTracker.get(GRENADE_TYPE_ID);
    }
    
    // getter for remaining bounces (synced)
    public int getRemainingBounces() {
        return this.dataTracker.get(REMAINING_BOUNCES);
    }
}