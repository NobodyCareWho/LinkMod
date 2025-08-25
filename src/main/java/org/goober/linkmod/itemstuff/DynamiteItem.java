package org.goober.linkmod.itemstuff;

import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.goober.linkmod.projectilestuff.DynamiteEntity;
import org.goober.linkmod.projectilestuff.KunaiEntity;

import java.util.List;

public class DynamiteItem extends Item implements ProjectileItem {
    public static final int MIN_DRAW_DURATION = 10;
    public static final float ATTACK_DAMAGE = 5.0F;
    public static final float THROW_SPEED = 2.7F;

    public DynamiteItem(Item.Settings settings) {
        super(settings);
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            int var6 = this.getMaxUseTime(stack, user) - remainingUseTicks;
            if (var6 < 10) {
                return false;
            } else {
                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                    if (world instanceof ServerWorld) {
                        ServerWorld serverWorld = (ServerWorld)world;


                            ItemStack itemStack = stack.splitUnlessCreative(1, playerEntity);
                            DynamiteEntity dynamiteEntity = new DynamiteEntity(serverWorld, playerEntity);
                            dynamiteEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, 2.5F, 1.0F);
                            if (playerEntity.isInCreativeMode()) {
                                dynamiteEntity.pickupType = PickupPermission.CREATIVE_ONLY;
                            }
                            serverWorld.spawnEntity(dynamiteEntity);

                            world.playSoundFromEntity((Entity)null, dynamiteEntity, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS, 1.0F, 1.0F);
                            return true;

                    }

            }
        } else {
            return false;
        }
        return true;
    }

    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        DynamiteEntity dynamiteEntity = new DynamiteEntity(world, null);
        dynamiteEntity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        dynamiteEntity.pickupType = PickupPermission.DISALLOWED;
        return dynamiteEntity;
    }
}
