package org.goober.linkmod.projectilestuff;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import org.goober.linkmod.entitystuff.LmodEntityRegistry;
import org.goober.linkmod.itemstuff.LmodItemRegistry;

public class SeedbagEntity extends ThrownItemEntity {
    public SeedbagEntity(EntityType<? extends SeedbagEntity> entityType, World world) {
        super(entityType, world);
    }

    public SeedbagEntity(World world, LivingEntity owner) {
        super(LmodEntityRegistry.SEEDBAG_ENTITY, owner, world, new ItemStack(LmodItemRegistry.SEEDBAG));
    }

    public SeedbagEntity(World world, double x, double y, double z) {
        super(LmodEntityRegistry.SEEDBAG_ENTITY, x, y, z, world, new ItemStack(LmodItemRegistry.SEEDBAG));
    }

    protected Item getDefaultItem() {
        return Items.WHEAT_SEEDS;
    }

    private ParticleEffect getParticleParameters() {
        ItemStack itemStack = this.getStack();
        return (ParticleEffect)(itemStack.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
    }

    public void handleStatus(byte status) {
        if (status == 3) {
            ParticleEffect particleEffect = this.getParticleParameters();

            for(int i = 0; i < 8; ++i) {
                this.getWorld().addParticleClient(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }

    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        int i = entity instanceof BlazeEntity ? 3 : 0;
        entity.serverDamage(this.getDamageSources().thrown(this, this.getOwner()), (float)i);
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (hitResult.getType() == HitResult.Type.BLOCK){
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            BlockPos pos = blockHit.getBlockPos();
            World world = this.getWorld();
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            System.out.println("hit block" + block);
            if (block==Blocks.FARMLAND){
                world.setBlockState(pos.up(), Blocks.WHEAT.getDefaultState());

            }else{
                    ItemStack stack = new ItemStack(Items.WHEAT_SEEDS, 1); // Replace with any item
                    Vec3d dropPos = Vec3d.ofCenter(pos.up()); // Center of the block

                    ItemEntity itemEntity = new ItemEntity(world, dropPos.x, dropPos.y, dropPos.z, stack);
                    world.spawnEntity(itemEntity);
            }




        };

        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, (byte)3);
            this.discard();
        }


    }
  }
