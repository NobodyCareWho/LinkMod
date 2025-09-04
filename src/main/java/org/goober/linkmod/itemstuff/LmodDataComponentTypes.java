package org.goober.linkmod.itemstuff;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerType;
import org.goober.linkmod.Linkmod;
import org.goober.linkmod.entitystuff.DesperadoEntity;
import org.goober.linkmod.gunstuff.GunContentsComponent;
import org.goober.linkmod.gunstuff.GunBloomComponent;

public class LmodDataComponentTypes {
    public static final ComponentType<SeedBagContentsComponent> SEEDBAG_CONTENTS = Registry.register(
        Registries.DATA_COMPONENT_TYPE,
        Identifier.of(Linkmod.MOD_ID, "seedbag_contents"),
        ComponentType.<SeedBagContentsComponent>builder()
            .codec(SeedBagContentsComponent.CODEC)
            .packetCodec(SeedBagContentsComponent.PACKET_CODEC)
            .build()
    );

    public static final ComponentType<GunContentsComponent> GUN_CONTENTS = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Linkmod.MOD_ID, "gun_contents"),
            ComponentType.<GunContentsComponent>builder()
                    .codec(GunContentsComponent.CODEC)
                    .packetCodec(GunContentsComponent.PACKET_CODEC)
                    .build()
    );
    
    public static final ComponentType<GunBloomComponent> GUN_BLOOM = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Linkmod.MOD_ID, "gun_bloom"),
            ComponentType.<GunBloomComponent>builder()
                    .codec(GunBloomComponent.CODEC)
                    .packetCodec(GunBloomComponent.PACKET_CODEC)
                    .build()
    );

    public static final ComponentType<DesperadoEntity.Variant> DESPERADO_VARIANT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Linkmod.MOD_ID, "desperado/variant"),
            ComponentType.<DesperadoEntity.Variant>builder()
                    .codec(DesperadoEntity.Variant.CODEC)
                    .packetCodec(DesperadoEntity.Variant.PACKET_CODEC)
                    .build()
    );
    
    public static void initialize() {
        // Force class loading
    }
}