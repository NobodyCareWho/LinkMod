package org.goober.linkmod.itemstuff;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.goober.linkmod.Linkmod;
import org.goober.linkmod.gunstuff.GunContentsComponent;

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
    
    public static void initialize() {
        // Force class loading
    }
}