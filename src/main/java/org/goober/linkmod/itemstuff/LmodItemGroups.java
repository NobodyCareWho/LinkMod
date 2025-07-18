package org.goober.linkmod.itemstuff;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.goober.linkmod.Linkmod;

public class LmodItemGroups {
    // create the item group key
    public static final RegistryKey<ItemGroup> LINKMOD_GROUP_KEY = RegistryKey.of(
            RegistryKeys.ITEM_GROUP,
            Identifier.of(Linkmod.MOD_ID, "main")
    );

    // create the actual item group
    public static final ItemGroup LINKMOD_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(LmodItemRegistry.SEEDBAG))
            .displayName(Text.translatable("itemGroup.lmod.main"))
            .build();

    public static void initialize() {
        // register the item group
        Registry.register(Registries.ITEM_GROUP, LINKMOD_GROUP_KEY, LINKMOD_GROUP);
        
        // add all mod items to the creative tab
        ItemGroupEvents.modifyEntriesEvent(LINKMOD_GROUP_KEY).register(entries -> {
            // iterate through all registered items and add ones from the mod
            Registries.ITEM.stream()
                .filter(item -> Registries.ITEM.getId(item).getNamespace().equals(Linkmod.MOD_ID))
                .forEach(entries::add);
        });
    }
}