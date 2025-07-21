package org.goober.linkmod.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.goober.linkmod.blockstuff.LmodBlockRegistry;
import org.goober.linkmod.itemstuff.LmodItemRegistry;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

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
                            Identifier.of("lmod", "gui/advancements/backgrounds/lmod"),
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

            AdvancementEntry stoneCutterAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                    .display(
                            Items.STONECUTTER,
                            Text.translatable("advancement.lmod.get_stonecutter.title"),
                            Text.translatable("advancement.lmod.get_stonecutter.description"),
                            null,
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false
                    )
                    .criterion("has_stonecutter", InventoryChangedCriterion.Conditions.items(Items.STONECUTTER))
                    .rewards(AdvancementRewards.Builder.recipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "lathe"))))
                    .build(consumer, "lmod/get_stonecutter");

            AdvancementEntry latheAdvancement = Advancement.Builder.create().parent(stoneCutterAdvancement)
                    .display(
                            LmodBlockRegistry.LATHE,
                            Text.translatable("advancement.lmod.get_lathe.title"),
                            Text.translatable("advancement.lmod.get_lathe.description"),
                            null,
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false
                    )
                    .criterion("has_lathe", InventoryChangedCriterion.Conditions.items(LmodBlockRegistry.LATHE))
                    .build(consumer, "lmod/get_lathe");

            AdvancementEntry croissantAdvancement = Advancement.Builder.create().parent(latheAdvancement)
                    .display(
                            LmodItemRegistry.CROISSANT,
                            Text.translatable("advancement.lmod.get_croissant.title"),
                            Text.translatable("advancement.lmod.get_croissant.description"),
                            null,
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false
                    )
                    .criterion("has_croissant", InventoryChangedCriterion.Conditions.items(LmodItemRegistry.CROISSANT))
                    .build(consumer, "lmod/get_croissant");

            AdvancementEntry gunComponentsAdvancement = Advancement.Builder.create().parent(latheAdvancement)
                    .display(
                            LmodItemRegistry.GUNCOMPONENTS,
                            Text.translatable("advancement.lmod.get_gun_components.title"),
                            Text.translatable("advancement.lmod.get_gun_components.description"),
                            null,
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false
                    )
                    .criterion("has_gun_components", InventoryChangedCriterion.Conditions.items(LmodItemRegistry.GUNCOMPONENTS))
                    .rewards(AdvancementRewards.Builder.experience(352)
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "autorifle")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "blazeshot")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "boilerpistol")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "breezeshot")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "buckshot")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "bullet")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "diamond_spine")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "diamond_bullet")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "ejectorpistol")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "freezeshot")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "gyrojetbullet")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "pinch_of_gunpowder")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "pumpsg")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "ratshot_bullet")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "revolver")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "rifle")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "rocket_compound")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "shotgun")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "shotgunshellempty")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "copper_cap")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "copper_bullet")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "slugshot"))))
                    .build(consumer, "lmod/get_gun_components");
        }

    }
}
