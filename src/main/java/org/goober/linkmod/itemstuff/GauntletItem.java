package org.goober.linkmod.itemstuff;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public class GauntletItem extends Item {

    public GauntletItem(Item.Settings settings) {
        super(settings);
    }

    public static AttributeModifiersComponent createAttributeModifiers() {
        return AttributeModifiersComponent.builder()
                .add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 3.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, 1.5, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
                .build();
    }

    public UseAction getUseAction(ItemStack stack) {
            return UseAction.BOW;
    }

    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        return true;
    }

    public void postHit(ItemStack stack,
                        LivingEntity target,
                        LivingEntity attacker) {
        if (!attacker.getWorld().isClient) {
            if (target instanceof LivingEntity livingEntity) {

                livingEntity.hurtTime = 4;
                livingEntity.timeUntilRegen = 4;
            }
        }

        // keep vanilla behavior
        super.postHit(stack, target, attacker);
    }

    // Whether item can be enchanted at all


}
