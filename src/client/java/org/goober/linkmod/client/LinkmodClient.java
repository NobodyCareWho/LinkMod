package org.goober.linkmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import org.goober.linkmod.entitystuff.LmodEntityRegistry;

public class LinkmodClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register entity renderer
        EntityRendererRegistry.register(LmodEntityRegistry.SEEDBAG_ENTITY, FlyingItemEntityRenderer::new);
    }
}
