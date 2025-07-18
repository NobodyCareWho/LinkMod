package org.goober.linkmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.goober.linkmod.blockstuff.LmodBlockRegistry;
import org.goober.linkmod.itemstuff.LmodItemRegistry;
import org.goober.linkmod.itemstuff.LmodDataComponentTypes;
import org.goober.linkmod.itemstuff.LmodItemGroups;
import org.goober.linkmod.entitystuff.LmodEntityRegistry;
import org.goober.linkmod.particlestuff.LmodParticleRegistry;
import org.goober.linkmod.soundstuff.LmodSoundRegistry;


public class Linkmod implements ModInitializer {
    public static final String MOD_ID = "lmod";
    @Override
    public void onInitialize() {
        LmodDataComponentTypes.initialize();
        LmodItemRegistry.initialize();
        LmodItemGroups.initialize();
        LmodEntityRegistry.initialize();
        LmodBlockRegistry.initialize();
        LmodEntityRegistry.initialize();
        LmodSoundRegistry.initialize();
    }

}
