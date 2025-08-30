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
import org.goober.linkmod.projectilestuff.*;

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

    public static final EntityType<BulletEntity> HPBULLET = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Linkmod.MOD_ID, "hollowpointbullet"),
            EntityType.Builder.<BulletEntity>create(BulletEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F)
                    .maxTrackingRange(80)
                    .trackingTickInterval(1)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Linkmod.MOD_ID, "hollowpointbullet")))
    );

    public static final EntityType<BulletEntity> SILVERBULLET = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Linkmod.MOD_ID, "silverbullet"),
            EntityType.Builder.<BulletEntity>create(BulletEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F)
                    .maxTrackingRange(80)
                    .trackingTickInterval(1)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Linkmod.MOD_ID, "silverbullet")))
    );

    public static final EntityType<BulletEntity> GYROJETBULLET = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Linkmod.MOD_ID, "gyrojetbullet"),
            EntityType.Builder.<BulletEntity>create(BulletEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F)
                    .maxTrackingRange(80)
                    .trackingTickInterval(1)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Linkmod.MOD_ID, "gyrojetbullet")))
    );

    public static final EntityType<BulletEntity> ICEBULLET = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Linkmod.MOD_ID, "icebullet"),
            EntityType.Builder.<BulletEntity>create(BulletEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F)
                    .maxTrackingRange(80)
                    .trackingTickInterval(1)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Linkmod.MOD_ID, "icebullet")))
    );

    public static final EntityType<BulletEntity> BOUNCYBULLET = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Linkmod.MOD_ID, "bouncybullet"),
            EntityType.Builder.<BulletEntity>create(BulletEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F)
                    .maxTrackingRange(80)
                    .trackingTickInterval(1)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Linkmod.MOD_ID, "bouncy")))
    );

    public static final EntityType<PillGrenadeEntity> PILLGRENADE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Linkmod.MOD_ID, "pillgrenade"),
            EntityType.Builder.<PillGrenadeEntity>create(PillGrenadeEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F)
                    .maxTrackingRange(80)
                    .trackingTickInterval(1)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Linkmod.MOD_ID, "pillgrenade")))
    );

    public static final EntityType<KunaiEntity> KUNAI = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Linkmod.MOD_ID, "kunai"),
            EntityType.Builder.<KunaiEntity>create(KunaiEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F)
                    .maxTrackingRange(80)
                    .trackingTickInterval(1)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Linkmod.MOD_ID, "kunai")))
    );

    public static final EntityType<DynamiteEntity> DYNAMITE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Linkmod.MOD_ID, "dynamite"),
            EntityType.Builder.<DynamiteEntity>create(DynamiteEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F)
                    .maxTrackingRange(80)
                    .trackingTickInterval(1)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Linkmod.MOD_ID, "dynamite")))
    );

    public static final EntityType<PillagerDynamiteEntity> PILLAGERDYNAMITE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Linkmod.MOD_ID, "pillager_dynamite"),
            EntityType.Builder.<PillagerDynamiteEntity>create(PillagerDynamiteEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F)
                    .maxTrackingRange(80)
                    .trackingTickInterval(1)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Linkmod.MOD_ID, "pillager_dynamite")))
    );

    public static void initialize() {
        // Force class loading
    }
}