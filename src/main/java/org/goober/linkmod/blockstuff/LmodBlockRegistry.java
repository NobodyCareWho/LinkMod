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
    public static final Block AUROS_BLOOM = register("auros_bloom",
            settings -> new FlowerBlock(StatusEffects.SATURATION, 0.1F, settings),
            AbstractBlock.Settings.copy(Blocks.POPPY));
    // Flower constructors need effect parameters for some reason? That's what the saturation effect is.
    public static final Block ROUNDED_SANDSTONE = register("rounded_sandstone", Block::new, Block.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.STONE));
    public static final Block GILDED_SANDSTONE = register("gilded_sandstone", Block::new, Block.Settings.create().mapColor(MapColor.PALE_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F));

    public static final Block HEMATITE = register("hematite", Block::new, Block.Settings.create().mapColor(MapColor.DULL_RED).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.IRON));
    public static final Block HEMATITE_STAIRS = register("hematite_stairs",
            state -> new StairsBlock(HEMATITE.getDefaultState(), state),
            Block.Settings.copy(HEMATITE)
    );
    public static final Block HEMATITE_SLAB = register("hematite_slab",
            SlabBlock::new,
            Block.Settings.copy(HEMATITE)
    );
    public static final Block HEMATITE_WALL = register("hematite_wall",
            WallBlock::new,
            Block.Settings.copy(HEMATITE)
    );

    public static final Block POLISHED_HEMATITE = register("polished_hematite", Block::new, Block.Settings.create().mapColor(MapColor.DULL_RED).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.IRON));
    public static final Block POLISHED_HEMATITE_STAIRS = register("polished_hematite_stairs",
            state -> new StairsBlock(POLISHED_HEMATITE.getDefaultState(), state),
            Block.Settings.copy(POLISHED_HEMATITE)
    );
    public static final Block POLISHED_HEMATITE_SLAB = register("polished_hematite_slab",
            SlabBlock::new,
            Block.Settings.copy(POLISHED_HEMATITE)
    );
    public static final Block POLISHED_HEMATITE_WALL = register("polished_hematite_wall",
            WallBlock::new,
            Block.Settings.copy(POLISHED_HEMATITE)
    );

    public static final Block HEMATITE_BRICKS = register("hematite_bricks", Block::new, Block.Settings.create().mapColor(MapColor.DULL_RED).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.IRON));
    public static final Block HEMATITE_BRICK_STAIRS = register("hematite_brick_stairs",
            state -> new StairsBlock(HEMATITE_BRICKS.getDefaultState(), state),
            Block.Settings.copy(HEMATITE_BRICKS)
    );
    public static final Block HEMATITE_BRICK_SLAB = register("hematite_brick_slab",
            SlabBlock::new,
            Block.Settings.copy(HEMATITE_BRICKS)
    );
    public static final Block HEMATITE_BRICK_WALL = register("hematite_brick_wall",
            WallBlock::new,
            Block.Settings.copy(HEMATITE_BRICKS)
    );

    public static final Block CHISELED_HEMATITE = register("chiseled_hematite", Block::new, Block.Settings.create().mapColor(MapColor.DULL_RED).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(1.5F, 6.0F).sounds(BlockSoundGroup.IRON));

    public static final Block LATHE = register("lathe", LatheBlock::new, Block.Settings.create().mapColor(MapColor.DARK_AQUA).requiresTool().strength(1.5F, 6.0F));
    public static final Block EXP_CHEST = register("exp_chest", ExpChestBlock::new, Block.Settings.create().mapColor(MapColor.PALE_PURPLE).instrument(NoteBlockInstrument.BASS).requiresTool().strength(2.5F, 9900).sounds(BlockSoundGroup.STONE).luminance(state -> 7));


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
