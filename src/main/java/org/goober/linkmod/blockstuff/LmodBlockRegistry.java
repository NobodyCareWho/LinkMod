package org.goober.linkmod.blockstuff;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class LmodBlockRegistry {
    public static final Block ROUNDED_SANDSTONE = register("rounded_sandstone", Block::new, Block.Settings.create().strength(4.0f));
    public static final Block GILDED_SANDSTONE = register("gilded_sandstone", Block::new, Block.Settings.create().strength(4.0f));
    public static final Block AUROS_BLOOM = register("auros_bloom", Block::new, AbstractBlock.Settings.copy(Blocks.POPPY));
    private static Block register(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        final Identifier identifier = Identifier.of("lmod", path);
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);

        final Block block = Blocks.register(registryKey, factory, settings);
        Items.register(block);
        return block;
    }
    public static void initialize() {
        // Force class loading
    }
}
