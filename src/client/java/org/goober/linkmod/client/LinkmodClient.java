package org.goober.linkmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.RenderLayer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import org.goober.linkmod.blockstuff.LmodBlockRegistry;
import org.goober.linkmod.entitystuff.LmodEntityRegistry;
import org.goober.linkmod.itemstuff.SeedBagTooltipData;

public class LinkmodClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register entity renderer
        EntityRendererRegistry.register(LmodEntityRegistry.SEEDBAG_ENTITY, FlyingItemEntityRenderer::new);
        // Register tooltip component
        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof SeedBagTooltipData seedBagData) {
                return new SeedBagTooltipComponent(seedBagData);
            }
            return null;
        });
        
        // set render layer to be transparent for the flower
        BlockRenderLayerMap.putBlock(LmodBlockRegistry.AUROS_BLOOM, BlockRenderLayer.CUTOUT);
    }
}
