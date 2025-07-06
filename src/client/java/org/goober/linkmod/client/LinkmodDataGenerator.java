package org.goober.linkmod.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.goober.linkmod.itemstuff.LmodItemRegistry;

import java.util.function.Consumer;

public class LinkmodDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        
        pack.addProvider((output, registriesFuture) -> new AdvancementsProvider(output, registriesFuture));
    }
    
    static class AdvancementsProvider extends FabricAdvancementProvider {
        protected AdvancementsProvider(FabricDataOutput dataGenerator, java.util.concurrent.CompletableFuture<net.minecraft.registry.RegistryWrapper.WrapperLookup> wrapperLookup) {
            super(dataGenerator, wrapperLookup);
        }

        @Override
        public void generateAdvancement(net.minecraft.registry.RegistryWrapper.WrapperLookup wrapperLookup, Consumer<AdvancementEntry> consumer) {
            AdvancementEntry rootAdvancement = Advancement.Builder.create()
                    .display(
                            LmodItemRegistry.SEEDBAG,
                            Text.translatable("advancement.lmod.root.title"),
                            Text.translatable("advancement.lmod.root.description"),
                            Identifier.of("minecraft", "textures/gui/advancements/backgrounds/adventure.png"),
                            AdvancementFrame.TASK,
                            false,
                            false,
                            false
                    )
                    .criterion("has_seeds", InventoryChangedCriterion.Conditions.items(Items.WHEAT_SEEDS))
                    .build(consumer, "lmod/root");

            AdvancementEntry seedBagAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                    .display(
                            LmodItemRegistry.SEEDBAG,
                            Text.translatable("advancement.lmod.get_seed_bag.title"),
                            Text.translatable("advancement.lmod.get_seed_bag.description"),
                            null,
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false
                    )
                    .criterion("has_seed_bag", InventoryChangedCriterion.Conditions.items(LmodItemRegistry.SEEDBAG_CUSTOM))
                    .build(consumer, "lmod/get_seed_bag");
        }
    }
}
