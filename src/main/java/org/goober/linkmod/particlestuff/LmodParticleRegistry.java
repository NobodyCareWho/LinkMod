package org.goober.linkmod.particlestuff;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.goober.linkmod.Linkmod;

public class LmodParticleRegistry {
    public static final SimpleParticleType SMOKERING =
            registerParticle("smoke_ring", FabricParticleTypes.simple(true));

    private static SimpleParticleType registerParticle(String name, SimpleParticleType particleType) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Linkmod.MOD_ID, name), particleType);

    }

}