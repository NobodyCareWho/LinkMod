package org.goober.linkmod.potionstuff;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.goober.linkmod.Linkmod;
import org.goober.linkmod.effectstuff.LmodEffectRegistry;

public class ModPotions {
    public static RegistryEntry<Potion> CREEPY;

    private static Potion registerPotion(String name, Potion potion) {
        return Registry.register(Registries.POTION, Identifier.of(Linkmod.MOD_ID, name), potion);
    }

    public static void registerModPotions() {
        System.out.println("Registering Mod Potions for " + Linkmod.MOD_ID);
        
        // Standard duration potions (8 minutes = 14400 ticks)
        Potion potion1 = new Potion("creepy", new StatusEffectInstance(LmodEffectRegistry.CREEPY, 300, 0));
        
        // Register all potions
        registerPotion("creepy", potion1);
        
        // Get registry entries
        CREEPY = Registries.POTION.getEntry(potion1);

    }
}