package org.goober.linkmod.blockstuff;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.goober.linkmod.Linkmod;
import org.goober.linkmod.blockstuff.blockentities.ExpChestBlockEntity;

public class LmodBlockEntityTypes {
    public static BlockEntityType<ExpChestBlockEntity> EXP_CHEST;

    public static void initialize() {
        EXP_CHEST = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Linkmod.MOD_ID, "exp_chest"),
            FabricBlockEntityTypeBuilder.create(ExpChestBlockEntity::new, LmodBlockRegistry.EXP_CHEST).build()
        );
    }
}