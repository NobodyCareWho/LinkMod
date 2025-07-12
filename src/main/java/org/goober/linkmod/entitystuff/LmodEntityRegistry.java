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
import org.goober.linkmod.projectilestuff.BulletEntity;

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
    
    public static final EntityType<BulletEntity> BULLET = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Linkmod.MOD_ID, "bullet"),
            EntityType.Builder.<BulletEntity>create(BulletEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F)
                    .maxTrackingRange(80)
                    .trackingTickInterval(1)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Linkmod.MOD_ID, "bullet")))
    );

    public static final EntityType<BulletEntity> SPARKBULLET = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Linkmod.MOD_ID, "sparkbullet"),
            EntityType.Builder.<BulletEntity>create(BulletEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F)
                    .maxTrackingRange(80)
                    .trackingTickInterval(1)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Linkmod.MOD_ID, "sparkbullet")))
    );

    public static void initialize() {
        // Force class loading
    }
}