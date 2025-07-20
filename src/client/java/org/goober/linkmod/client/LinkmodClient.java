package org.goober.linkmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.BlockRenderLayer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.RenderLayer;
import org.goober.linkmod.gunstuff.items.GunItem;
import org.goober.linkmod.gunstuff.items.Guns;
import org.goober.linkmod.gunstuff.GunBloomComponent;
import org.goober.linkmod.itemstuff.LmodDataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import org.goober.linkmod.blockstuff.LmodBlockRegistry;
import org.goober.linkmod.entitystuff.LmodEntityRegistry;
import org.goober.linkmod.itemstuff.SeedBagTooltipData;
import org.goober.linkmod.gunstuff.GunTooltipData;
import org.goober.linkmod.particlestuff.LmodParticleRegistry;
import org.goober.linkmod.screenstuff.LmodScreenHandlerType;

public class LinkmodClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register screens
        HandledScreens.register(LmodScreenHandlerType.LATHE, LatheScreen::new);
        
        // Register entity renderers
        EntityRendererRegistry.register(LmodEntityRegistry.SEEDBAG_ENTITY, FlyingItemEntityRenderer::new);
        // register empty renderer for bullet entity
        EntityRendererRegistry.register(LmodEntityRegistry.BULLET, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(LmodEntityRegistry.SPARKBULLET, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(LmodEntityRegistry.HPBULLET, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(LmodEntityRegistry.SILVERBULLET, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(LmodEntityRegistry.GYROJETBULLET, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(LmodEntityRegistry.ICEBULLET, EmptyEntityRenderer::new);
        // register renderer for pill grenade entity
        EntityRendererRegistry.register(LmodEntityRegistry.PILLGRENADE, EmptyEntityRenderer::new);
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
        // set render layer for lathe to handle transparency
        BlockRenderLayerMap.putBlock(LmodBlockRegistry.LATHE, BlockRenderLayer.CUTOUT);

        ParticleFactoryRegistry.getInstance().register(LmodParticleRegistry.SMOKERING, SmokeRingParticle.SmokeRingFactory::new);
        
        // register HUD rendering
        HudRenderCallback.EVENT.register(LinkmodClient::renderHud);
    }

    // texture identifiers for custom bloom bar (as GUI textures, not raw textures)
    private static final Identifier BLOOM_BAR_BACKGROUND = Identifier.of("lmod", "hud/bloombarempty");
    private static final Identifier BLOOM_BAR_FULL = Identifier.of("lmod", "hud/bloombarfull");
    private static final Identifier BLOOM_BAR_PROGRESS = Identifier.of("lmod", "hud/bloombarprogress2");
    
    private static void renderHud(DrawContext ctx, RenderTickCounter counter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (client.options.getAttackIndicator().getValue() != AttackIndicator.CROSSHAIR) {
            return;
        }
        
        // check if holding a gun
        ItemStack heldItem = client.player.getMainHandStack();
        if (!(heldItem.getItem() instanceof GunItem gunItem)) {
            return;
        }

        // get gun type and check if it has bloom
        Guns.GunType gunType = Guns.get(gunItem.getGunTypeId());
        if (gunType.bloomMax() <= 0) {
            return;
        }

        // get bloom from item component
        GunBloomComponent bloomComp = heldItem.getOrDefault(LmodDataComponentTypes.GUN_BLOOM, GunBloomComponent.DEFAULT);
        bloomComp = bloomComp.withDecay(gunType.bloomDecayRate());
        float currentBloom = bloomComp.currentBloom();
     //   System.out.println("Bloom: " + currentBloom);

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();


        float progress = currentBloom / gunType.bloomMax();

        int j = screenHeight / 2 - 7 + 16;
        int k = screenWidth / 2 - 8;

        if (progress >= 1.0f) {
            // fully charged - draw full texture
            ctx.drawGuiTexture(RenderPipelines.CROSSHAIR, BLOOM_BAR_FULL, k, j, 16, 16);
        } else if (progress < 1.0f) {
            // draw background bar (horizontal)
            ctx.drawGuiTexture(RenderPipelines.CROSSHAIR, BLOOM_BAR_BACKGROUND, k, j, 16, 4);
            
            if (progress > 0.0f) {
                // draw progress bar (horizontal, partial width)
                int progressWidth = (int)(progress * 17.0f);
                ctx.drawGuiTexture(RenderPipelines.CROSSHAIR, BLOOM_BAR_PROGRESS, 16, 4, 0, 0, k, j, progressWidth, 4);
            }
        }
    }
}

