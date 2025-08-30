package org.goober.linkmod.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Identifier;
import org.goober.linkmod.entitystuff.AgentPillagerEntity;
import org.goober.linkmod.client.model.AgentPillagerEntityModel;

public class AgentPillagerEntityRenderer extends IllagerEntityRenderer<AgentPillagerEntity, IllagerEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.of("lmod","textures/entity/irspillager.png");

    public AgentPillagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new AgentPillagerEntityModel(context.getPart(EntityModelLayers.PILLAGER)), 0.5F);
        this.addFeature(new HeldItemFeatureRenderer(this));
    }

    public Identifier getTexture(IllagerEntityRenderState illagerEntityRenderState) {
        return TEXTURE;
    }

    public IllagerEntityRenderState createRenderState() {
        return new IllagerEntityRenderState();
    }
}
}
