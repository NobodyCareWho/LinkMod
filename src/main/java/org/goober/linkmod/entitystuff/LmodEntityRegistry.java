package org.goober.linkmod.entitystuff;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.goober.linkmod.Linkmod;
import org.goober.linkmod.projectilestuff.SeedbagEntity;

public class LmodEntityRegistry {
    public static final EntityType<SeedbagEntity> SEEDBAG_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Linkmod.MOD_ID, "seedbag"),
            EntityType.Builder.<SeedbagEntity>create(SeedbagEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25F, 0.25F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Linkmod.MOD_ID, "seedbag")))
    );

    public static void initialize() {
        // Force class loading
    }
}