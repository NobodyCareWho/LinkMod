package org.goober.linkmod.effectstuff;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

public class CreepyEffect extends StatusEffect {
    public CreepyEffect() {
        super(StatusEffectCategory.HARMFUL, 0x12873d);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient()) {
            int duration = entity.getStatusEffect((RegistryEntry<StatusEffect>) entity).getDuration();
            if (duration <= 1) {
                World world = entity.getWorld();
                world.createExplosion(
                        entity,                 // entity causing the explosion
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        5.0F,                  // explosion power
                        World.ExplosionSourceType.MOB
                );
            }
        }
    }
}
