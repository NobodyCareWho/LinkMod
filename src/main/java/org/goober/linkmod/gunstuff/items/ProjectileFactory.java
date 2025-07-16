package org.goober.linkmod.gunstuff.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@FunctionalInterface
public interface ProjectileFactory {
    PersistentProjectileEntity create(World world, LivingEntity owner, ItemStack bulletStack);
}