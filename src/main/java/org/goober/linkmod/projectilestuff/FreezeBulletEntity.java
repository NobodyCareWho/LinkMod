package org.goober.linkmod.projectilestuff;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.goober.linkmod.entitystuff.LmodEntityRegistry;
import org.goober.linkmod.gunstuff.items.BulletItem;
import org.goober.linkmod.gunstuff.items.Bullets;

public class FreezeBulletEntity extends PersistentProjectileEntity {
    private float damage = 5.0F;
    private ItemStack bulletStack;

    public FreezeBulletEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.bulletStack = ItemStack.EMPTY;
        this.setNoGravity(true);
    }

    public FreezeBulletEntity(World world, LivingEntity owner, ItemStack bulletStack) {
        this(LmodEntityRegistry.ICEBULLET, world);
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


        // add particle trail
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            Vec3d pos = this.getPos();
            Vec3d velocity = this.getVelocity();

            BlockPos entity = this.getBlockPos();
            BlockState blockState = serverWorld.getBlockState(entity);

            if (blockState.isOf(Blocks.WATER)) {
                serverWorld.setBlockState(entity, Blocks.FROSTED_ICE.getDefaultState());
            } else if (blockState.isOf(Blocks.LAVA)) {
                serverWorld.setBlockState(entity, Blocks.MAGMA_BLOCK.getDefaultState());
            }

            // create a trail of particles
            for (int i = 0; i < 3; i++) {
                double factor = i * 0.3;
                serverWorld.spawnParticles(
                    ParticleTypes.SNOWFLAKE,
                    pos.x - velocity.x * factor,
                    pos.y - velocity.y * factor,
                    pos.z - velocity.z * factor,
                    1,
                    0.0, 0.0, 0.0,
                    0.01
                );
            }
            
            // add spark particles
            if (this.age % 2 == 0) {
                serverWorld.spawnParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    pos.x, pos.y, pos.z,
                    1,
                    0.05, 0.05, 0.05,
                    0.02
                );
            }
        }
        
        // remove bullet after 3 seconds
        if (this.age > 60) {
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
            entity.damage(serverWorld, damageSource, finalDamage);
            // remove immunity frames so shotgun pellets can all hit
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.hurtTime = 0;
                livingEntity.timeUntilRegen = 0;
                int freezeticks = livingEntity.getFrozenTicks();
                livingEntity.setFrozenTicks(2+freezeticks); // Freezes the entity for 2 seconds
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
            SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.PLAYERS, 1.0F, 1.2F);
        
        this.discard();
    }
    
    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        
        // add impact particles
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            Vec3d hitPos = blockHitResult.getPos();
            serverWorld.spawnParticles(
                ParticleTypes.SMOKE,
                hitPos.x, hitPos.y, hitPos.z,
                10,
                0.1, 0.1, 0.1,
                0.05
            );
        }
        
        // play impact sound
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), 
            SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.PLAYERS, 0.8F, 1.5F);
        
        this.discard();
    }
    
    
    @Override
    protected boolean tryPickup(PlayerEntity player) {
        // bullets can't be picked up
        return false;
    }
}