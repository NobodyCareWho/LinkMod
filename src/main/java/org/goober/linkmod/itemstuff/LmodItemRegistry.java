package org.goober.linkmod.itemstuff;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.goober.linkmod.Linkmod;

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
    
    public static void initialize() {
        // Force class loading
    }
}
