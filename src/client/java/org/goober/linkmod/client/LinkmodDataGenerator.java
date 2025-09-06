package org.goober.linkmod.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Blocks;
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
                            Items.WHEAT_SEEDS,
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
                            LmodItemRegistry.SEEDBAG_CUSTOM,
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



            AdvancementEntry enderchestAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                    .criterion("has_echest", InventoryChangedCriterion.Conditions.items(Blocks.ENDER_CHEST))
                    .rewards(AdvancementRewards.Builder.recipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "exp_chest"))))
                    .build(consumer, "lmod/get_ender_chest");

            AdvancementEntry bowlAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                    .criterion("has_bowl", InventoryChangedCriterion.Conditions.items(Items.BOWL))
                    .rewards(AdvancementRewards.Builder.recipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "mask_of_obscurity"))))
                    .build(consumer, "lmod/get_bowl");

            AdvancementEntry tntAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                    .criterion("has_tnt", InventoryChangedCriterion.Conditions.items(Blocks.TNT))
                    .rewards(AdvancementRewards.Builder.recipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "dynamite"))))
                    .build(consumer, "lmod/get_tnt");

            AdvancementEntry stringAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                    .criterion("has_string", InventoryChangedCriterion.Conditions.items(Items.STRING))
                    .rewards(AdvancementRewards.Builder.recipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "kunai"))))
                    .build(consumer, "lmod/get_string");

            AdvancementEntry ironNuggetAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                    .criterion("has_ironnugget", InventoryChangedCriterion.Conditions.items(Items.IRON_NUGGET))
                    .rewards(AdvancementRewards.Builder.recipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "hematite")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "kunai"))))
                    .build(consumer, "lmod/get_ironnugget");

            AdvancementEntry hematiteAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                    .criterion("has_hematite", InventoryChangedCriterion.Conditions.items(LmodBlockRegistry.HEMATITE))
                    .rewards(AdvancementRewards.Builder.recipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "chiseled_hematite_block")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "hematite_brick_slab")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "hematite_brick_stairs")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "hematite_brick_wall")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "hematite_bricks")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "hematite_slab")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "hematite_stairs")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "hematite_wall")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "polished_hematite")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "polished_hematite_slab")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "polished_hematite_stairs")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "polished_hematite_wall"))))
                    .build(consumer, "lmod/get_hematite");

            AdvancementEntry grenadeshellAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                    .criterion("has_grenadeshell", InventoryChangedCriterion.Conditions.items(LmodItemRegistry.GRENADESHELL))
                    .rewards(AdvancementRewards.Builder.experience(0)
                    .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "pillgrenade")))
                    .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "he_grenade")))
                    .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "bouncy_grenade")))
                    .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "shape_grenade")))
                    .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "incendiary_grenade")))
                    .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "demo_grenade")))
                    .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "thumpershell")))
                    .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "large_barrel")))
                    .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "grenadelauncher"))))
                    .build(consumer, "lmod/get_grenadeshell");


            AdvancementEntry expchestAdvancement = Advancement.Builder.create().parent(rootAdvancement)
                    .display(
                            LmodBlockRegistry.EXP_CHEST,
                            Text.translatable("advancement.lmod.get_exp_chest.title"),
                            Text.translatable("advancement.lmod.get_exp_chest.description"),
                            null,
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false
                    )
                    .criterion("has_exp_chest", InventoryChangedCriterion.Conditions.items(LmodBlockRegistry.EXP_CHEST))
                    .build(consumer, "lmod/get_exp_chest");

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
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "slimy_slugshot")))
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
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "pillgrenade")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "he_grenade")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "bouncy_grenade")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "shape_grenade")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "incendiary_grenade")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "demo_grenade")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "thumpershell")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "large_barrel")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "grenadelauncher")))
                            .addRecipe(RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("lmod", "slugshot"))))


                    .build(consumer, "lmod/get_gun_components");

            AdvancementEntry allGunsAdvancement = Advancement.Builder.create().parent(gunComponentsAdvancement)
                    .display(
                            LmodItemRegistry.REVOLVER,
                            Text.translatable("advancement.lmod.allguns.title"),
                            Text.translatable("advancement.lmod.allguns.description"),
                            null,
                            AdvancementFrame.CHALLENGE,
                            true,
                            true,
                            false
                    )
                    .criterion("has_allguns", InventoryChangedCriterion.Conditions.items(LmodItemRegistry.REVOLVER,LmodItemRegistry.SHOTGUN,LmodItemRegistry.RIFLE,LmodItemRegistry.AUTORIFLE,LmodItemRegistry.GRENADELAUNCHER,LmodItemRegistry.BOILERPISTOL,LmodItemRegistry.PUMPSG,LmodItemRegistry.EJECTORPISTOL))
                    .rewards(AdvancementRewards.Builder.experience(522))

                    .build(consumer, "lmod/get_allguns");

        }

    }
}
