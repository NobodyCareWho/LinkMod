package org.goober.linkmod.itemstuff;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.goober.linkmod.Linkmod;
import org.goober.linkmod.gunstuff.items.BulletItem;
import org.goober.linkmod.gunstuff.items.GunItem;
import org.goober.linkmod.gunstuff.GunContentsComponent;

import java.util.function.Function;

public class LmodItemRegistry {
    private LmodItemRegistry() {}

    // Single-point registration method
    public static <T extends Item> T register(String name, Function<Item.Settings, T> factory, Item.Settings settings) {
        Identifier id = Identifier.of(Linkmod.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);

        // Embed the registry key into settings
        Item.Settings withKey = settings.registryKey(key);

        // Create the item instance with the modified settings
        T item = factory.apply(withKey);

        // Register it properly
        return Registry.register(Registries.ITEM, key, item);
    }

    // Define items here:
    public static final Item SEEDBAG = register(
            "seed_bag",
            SeedBagItem::new,
            new Item.Settings()
                    .maxCount(1)
                    .component(net.minecraft.component.DataComponentTypes.BUNDLE_CONTENTS, net.minecraft.component.type.BundleContentsComponent.DEFAULT)
    );
    
    public static final Item SEEDBAG_CUSTOM = register(
            "seed_bag_custom",
            SeedBagItemCustom::new,
            new Item.Settings()
                    .maxCount(1)
                    .component(LmodDataComponentTypes.SEEDBAG_CONTENTS, SeedBagContentsComponent.EMPTY)
    );

    // guns
    public static final Item RIFLE = register(
            "rifle",
            settings -> new GunItem(settings, "rifle"),
            new Item.Settings()
                    .maxCount(1)
                    .component(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY)
    );
    
    public static final Item SHOTGUN = register(
            "shotgun",
            settings -> new GunItem(settings, "shotgun"),
            new Item.Settings()
                    .maxCount(1)
                    .component(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY)
    );

    public static final Item PISTOL = register(
            "revolver",
            settings -> new GunItem(settings, "revolver"),
            new Item.Settings()
                    .maxCount(1)
                    .component(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY)
    );

    // bullets
    public static final Item BULLET = register(
            "bullet",
            settings -> new BulletItem(settings, "standard"),
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item BUCKSHOTGUNSHELL = register(
            "buckshotgunshell",
            settings -> new BulletItem(settings, "buckshotgunshell"),
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item SHOTGUNSHELLEMPTY = register(
            "shotgunshellempty",
            settings -> new BulletItem(settings, "shotgunshellempty"),
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item BULLETCASING = register(
            "bulletcasing",
            settings -> new BulletItem(settings, "bulletcasing"),
            new Item.Settings()
                    .maxCount(64)
    );

    public static void initialize() {
        // Force class loading
    }
}
