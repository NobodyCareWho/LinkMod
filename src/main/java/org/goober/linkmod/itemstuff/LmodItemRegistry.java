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

    public static final Item REVOLVER = register(
            "revolver",
            settings -> new GunItem(settings, "revolver"),
            new Item.Settings()
                    .maxCount(1)
                    .component(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY)
    );

    public static final Item EJECTORPISTOL = register(
            "ejectorpistol",
            settings -> new GunItem(settings, "ejectorpistol"),
            new Item.Settings()
                    .maxCount(1)
                    .component(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY)
    );

    public static final Item BOILERPISTOL = register(
            "boilerpistol",
            settings -> new GunItem(settings, "boilerpistol"),
            new Item.Settings()
                    .maxCount(1)
                    .component(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY)
    );

    public static final Item PUMPSG = register(
            "pumpshotgun",
            settings -> new GunItem(settings, "pumpsg"),
            new Item.Settings()
                    .maxCount(1)
                    .component(LmodDataComponentTypes.GUN_CONTENTS, GunContentsComponent.EMPTY)
    );

    public static final Item GRENADELAUNCHER = register(
            "grenadelauncher",
            settings -> new GunItem(settings, "grenadelauncher"),
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

    public static final Item COPPER_BULLET = register(
            "copper_bullet",
            settings -> new BulletItem(settings, "copper_bullet"),
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item SILVER_BULLET = register(
            "silver_bullet",
            settings -> new BulletItem(settings, "silver_bullet"),
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item DIAMOND_BULLET = register(
            "diamond_bullet",
            settings -> new BulletItem(settings, "diamond_bullet"),
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item HPBULLET = register(
            "hollowpointbullet",
            settings -> new BulletItem(settings, "hollowpointbullet"),
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item GYROJETBULLET = register(
            "gyrojetbullet",
            settings -> new BulletItem(settings, "gyrojetbullet"),
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item RATSHOT = register(
            "ratshot_bullet",
            settings -> new BulletItem(settings, "ratshot_bullet"),
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item SLUGSHELL = register(
            "slugshell",
            settings -> new BulletItem(settings, "slug"),
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item BUCKSHELL = register(
            "buckshell",
            settings -> new BulletItem(settings, "buckshot"),
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item BREEZESHELL = register(
            "breezeshell",
            settings -> new BulletItem(settings, "breezeshot"),
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item BLAZESHELL = register(
            "blazeshell",
            settings -> new BulletItem(settings, "blazeshot"),
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item FREEZESHELL = register(
            "freezeshell",
            settings -> new BulletItem(settings, "freezeshot"),
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item PILLGRENADE = register(
            "pillgrenade",
            settings -> new BulletItem(settings, "pillgrenade"),
            new Item.Settings()
                    .maxCount(8)
    );

    public static final Item THUMPERSHELL = register(
            "thumpershell",
            settings -> new BulletItem(settings, "thumpershell"),
            new Item.Settings()
                    .maxCount(8)
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

    public static final Item GRENADESHELL = register(
            "grenadeshellempty",
            settings -> new BulletItem(settings, "grenadeshellempty"),
            new Item.Settings()
                    .maxCount(32)
    );

    // INGREDIENTS

    public static final Item GUNCOMPONENTS = register(
            "gun_components",
            Item::new,
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item COPPERBARREL = register(
            "copper_barrel",
            Item::new,
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item IRONBARREL = register(
            "iron_barrel",
            Item::new,
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item COPPERTIP = register(
            "copper_tip",
            Item::new,
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item IRONTIP = register(
            "iron_tip",
            Item::new,
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item PINCHOFGUNPOWDER = register(
            "pinch_of_gunpowder",
            Item::new,
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item ROCKETCOMPOUND = register(
            "rocket_compound",
            Item::new,
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item DIAMONDSPINE = register(
            "diamond_spine",
            Item::new,
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item COPPERCAP = register(
            "copper_cap",
            Item::new,
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item SLUG = register(
            "slug",
            Item::new,
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item SHOTPELLETS = register(
            "shot_pellets",
            Item::new,
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item HOLLOWPOINTTIP = register(
            "hollowpoint_tip",
            Item::new,
            new Item.Settings()
                    .maxCount(64)
    );

    public static final Item EMPTYGYROJET = register(
            "empty_gyrojet",
            Item::new,
            new Item.Settings()
                    .maxCount(64)
    );

    public static void initialize() {
        // Force class loading
    }
}
