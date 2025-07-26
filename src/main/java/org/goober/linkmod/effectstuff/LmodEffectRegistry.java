package org.goober.linkmod.effectstuff;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.goober.linkmod.Linkmod;

public class LmodEffectRegistry {
    public static RegistryEntry<StatusEffect> CREEPY;

    private static StatusEffect registerEffect(String name, StatusEffect effect) {
        return Registry.register(Registries.STATUS_EFFECT, Identifier.of(Linkmod.MOD_ID, name), effect);
    }

    public static void registerModEffects() {
        System.out.println("Registering Mod Effects for " + Linkmod.MOD_ID);
        
        StatusEffect creepy = registerEffect("mistake", new CreepyEffect());
        CREEPY = Registries.STATUS_EFFECT.getEntry(creepy);
    }
}