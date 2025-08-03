package org.goober.linkmod.blockstuff;

import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.goober.linkmod.blockstuff.blocks.LatheBlock;
import org.goober.linkmod.blockstuff.blocks.ExpChestBlock;

import java.util.function.Function;

public class LmodBlockRegistry {
    public static final Block ROUNDED_SANDSTONE = register("rounded_sandstone", Block::new, Block.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F));
    public static final Block GILDED_SANDSTONE = register("gilded_sandstone", Block::new, Block.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F));
    public static final Block LATHE = register("lathe", LatheBlock::new, Block.Settings.create().mapColor(MapColor.DARK_AQUA).requiresTool().strength(1.5F, 6.0F));
    public static final Block AUROS_BLOOM = register("auros_bloom", 
            settings -> new FlowerBlock(StatusEffects.SATURATION, 0.1F, settings),
            AbstractBlock.Settings.copy(Blocks.POPPY));
    public static final Block EXP_CHEST = register("exp_chest", ExpChestBlock::new, Block.Settings.create().mapColor(MapColor.PALE_PURPLE).instrument(NoteBlockInstrument.BASS).requiresTool().strength(2.5F, 9900).sounds(BlockSoundGroup.STONE));
    // Flower constructors need effect parameters for some reason? That's what the saturation effect is.
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
