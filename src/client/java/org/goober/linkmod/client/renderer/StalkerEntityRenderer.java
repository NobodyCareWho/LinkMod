package org.goober.linkmod.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.util.Identifier;
import org.goober.linkmod.client.model.AgentPillagerEntityModel;
import org.goober.linkmod.client.model.StalkerEntityModel;
import org.goober.linkmod.entitystuff.AgentPillagerEntity;

public class StalkerEntityRenderer extends IllagerEntityRenderer<AgentPillagerEntity, IllagerEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.of("lmod","textures/entity/snowpillager2.png");
    public static final EntityModelLayer STALKER_LAYER =
            new EntityModelLayer(Identifier.of("lmod", "stalker"), "main");


    public StalkerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new StalkerEntityModel(context.getPart(StalkerEntityRenderer.STALKER_LAYER)), 0.5F);
        this.addFeature(new HeldItemFeatureRenderer<>(this));
    }

    public Identifier getTexture(IllagerEntityRenderState illagerEntityRenderState) {
        return TEXTURE;
    }

    public IllagerEntityRenderState createRenderState() {
        return new IllagerEntityRenderState();
    }
}
