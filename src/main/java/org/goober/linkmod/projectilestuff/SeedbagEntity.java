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
import net.minecraft.item.BlockItem;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.block.CropBlock;
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
    
    public SeedbagEntity(World world, LivingEntity owner, ItemStack seedStack) {
        super(LmodEntityRegistry.SEEDBAG_ENTITY, owner, world, seedStack);
    }

    public SeedbagEntity(World world, double x, double y, double z) {
        super(LmodEntityRegistry.SEEDBAG_ENTITY, x, y, z, world, new ItemStack(LmodItemRegistry.SEEDBAG));
    }

    protected Item getDefaultItem() {
        return Items.BEETROOT_SEEDS; // testing for dynamic items change this later
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
       //     System.out.println("hit block" + block);
            // we can use this to get the seed item
            ItemStack seedStack = this.getStack();
            Item seedItem = seedStack.isEmpty() ? Items.WHEAT_SEEDS : seedStack.getItem();
            
            // Try to plant the crop
            if (block == Blocks.FARMLAND && world.getBlockState(pos.up()).isAir()) {
                plantSeed(world, pos.up(), seedItem);
            } else if (world.getBlockState(pos).getBlock() instanceof CropBlock || world.getBlockState(pos.up()).getBlock() instanceof CropBlock) {
                // Only search for nearby farmland if we hit a crop
                BlockPos nearestFarmland = findNearestEmptyFarmland(world, pos, 3);
                if (nearestFarmland != null) {
                    plantSeed(world, nearestFarmland.up(), seedItem);
                } else {
                    // drop as item if no farmland found
                    ItemStack dropStack = new ItemStack(seedItem, 1);
                    Vec3d dropPos = Vec3d.ofCenter(pos.up());
                    ItemEntity itemEntity = new ItemEntity(world, dropPos.x, dropPos.y, dropPos.z, dropStack);
                    world.spawnEntity(itemEntity);
                }
            } else {
                // Hit something other than farmland or crops - just drop the seed
                ItemStack dropStack = new ItemStack(seedItem, 1);
                Vec3d dropPos = Vec3d.ofCenter(pos.up());
                ItemEntity itemEntity = new ItemEntity(world, dropPos.x, dropPos.y, dropPos.z, dropStack);
                world.spawnEntity(itemEntity);
            }
        }

        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, (byte)3);
            this.discard();
        }


    }
    
    private BlockPos findNearestEmptyFarmland(World world, BlockPos center, int radius) {
        BlockPos nearestPos = null;
        double nearestDistance = Double.MAX_VALUE;
        
        // search in a radius to a cube in the center position
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = center.add(x, y, z);
                    
                    // check if it's farmland with air above
                    if (world.getBlockState(checkPos).getBlock() == Blocks.FARMLAND 
                        && world.getBlockState(checkPos.up()).isAir()) {
                        
                        double distance = checkPos.getSquaredDistance(center);
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearestPos = checkPos;
                        }
                    }
                }
            }
        }
        
        return nearestPos;
    }
    
    private void plantSeed(World world, BlockPos pos, Item seedItem) {
        // check if the seed item is a BlockItem
        if (seedItem instanceof BlockItem blockItem) {
            Block cropBlock = blockItem.getBlock();
            world.setBlockState(pos, cropBlock.getDefaultState());
        } else {
            // fall back to drop item
            ItemStack dropStack = new ItemStack(seedItem, 1);
            Vec3d dropPos = Vec3d.ofCenter(pos);
            ItemEntity itemEntity = new ItemEntity(world, dropPos.x, dropPos.y, dropPos.z, dropStack);
            world.spawnEntity(itemEntity);
        }
    }
  }
