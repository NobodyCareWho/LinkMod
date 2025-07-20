package org.goober.linkmod.itemstuff;


import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.util.ActionResult;


public class MaskItem extends Item{
    public MaskItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        EquipmentSlot slot = EquipmentSlot.HEAD;

        if (user.getEquippedStack(slot).isEmpty()) {
            if (!world.isClient()) {
                user.equipStack(slot, stack.copyWithCount(1));
                stack.decrement(1);
                    world.playSound(null, user.getX(), user.getY(), user.getZ(),
                            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
                            SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F));
            }
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.FAIL;
        }
    }
}
