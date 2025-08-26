package org.goober.linkmod.itemstuff;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity.PickupPermission;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.goober.linkmod.projectilestuff.DynamiteEntity;

import java.util.List;

public class DynamiteItem extends Item implements ProjectileItem {
    public static final int MIN_DRAW_DURATION = 10;
    public static final float ATTACK_DAMAGE = 5.0F;
    public static final float THROW_SPEED = 2.7F;

    public DynamiteItem(Item.Settings settings) {
        super(settings);
    }

    public static AttributeModifiersComponent createAttributeModifiers() {
        return AttributeModifiersComponent.builder().add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 4.0, Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND).build();
    }

    public static ToolComponent createToolComponent() {
        return new ToolComponent(List.of(), 1.0F, 2, false);
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }
    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            int useTicks = this.getMaxUseTime(stack, user) - remainingUseTicks;
            if (useTicks < 10) {
                return false;
            }
            int useTime = this.getMaxUseTime(stack, user) - remainingUseTicks; // how long it was held


            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            
            if (world instanceof ServerWorld serverWorld) {
                // consume one item from stack unless in creative mode
                if (!playerEntity.isInCreativeMode()) {
                    stack.decrement(1);
                }
                // how long it was held

                // create and spawn the dynamite entity
                DynamiteEntity dynamiteEntity = new DynamiteEntity(serverWorld, playerEntity, useTime);
                dynamiteEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, 1.5F, 1.0F);

                if (playerEntity.isInCreativeMode()) {
                    dynamiteEntity.pickupType = PickupPermission.CREATIVE_ONLY;
                }
                
                serverWorld.spawnEntity(dynamiteEntity);

                return true;
            }
            
            return false;
        }
        return false;
    }


    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int useTicks) {
        super.usageTick(world, user, stack, useTicks);

        if (useTicks < (getMaxUseTime(stack, user) - 4*20) && user instanceof PlayerEntity) {
            double x = user.lastX;
            double y = user.lastY;
            double z = user.lastZ;
            world.createExplosion(user, x, y, z, 5, false, World.ExplosionSourceType.MOB);
            System.out.println("EXPLODE!!!" + useTicks);
            if (!user.isInCreativeMode()) {
                stack.decrement(1);

            }

            user.stopUsingItem();
        } else if (getMaxUseTime(stack, user) - useTicks <=0) {
            double x = user.lastX;
            double y = user.lastY;
            double z = user.lastZ;
            world.playSound(null, x, y, z, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS, 1, 1);
        }
    }

    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        int useTime = 0;
        DynamiteEntity dynamiteEntity = new DynamiteEntity(world, null, useTime);
        dynamiteEntity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        dynamiteEntity.pickupType = PickupPermission.DISALLOWED;





        return dynamiteEntity;
    }
}
