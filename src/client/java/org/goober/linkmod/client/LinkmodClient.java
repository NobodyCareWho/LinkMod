package org.goober.linkmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import org.goober.linkmod.blockstuff.LmodBlockRegistry;
import org.goober.linkmod.entitystuff.LmodEntityRegistry;
import org.goober.linkmod.itemstuff.SeedBagTooltipData;
import org.goober.linkmod.gunstuff.GunTooltipData;
import org.goober.linkmod.particlestuff.LmodParticleRegistry;

public class LinkmodClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register entity renderers
        EntityRendererRegistry.register(LmodEntityRegistry.SEEDBAG_ENTITY, FlyingItemEntityRenderer::new);
        // register empty renderer for bullet entity
        EntityRendererRegistry.register(LmodEntityRegistry.BULLET, EmptyEntityRenderer::new);
        // Register tooltip components
        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof SeedBagTooltipData seedBagData) {
                return new SeedBagTooltipComponent(seedBagData);
            } else if (data instanceof GunTooltipData gunData) {
                return new GunTooltipComponent(gunData);
            }
            return null;
        });
        // set render layer to be transparent for the flower
        BlockRenderLayerMap.putBlock(LmodBlockRegistry.AUROS_BLOOM, BlockRenderLayer.CUTOUT);

        ParticleFactoryRegistry.getInstance().register(LmodParticleRegistry.SMOKERING, SmokeRingParticle.SmokeRingFactory::new);
    }
}
