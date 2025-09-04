package org.goober.linkmod.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.client.render.entity.state.RabbitEntityRenderState;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.goober.linkmod.client.model.DesperadoEntityModel;
import org.goober.linkmod.client.model.StalkerEntityModel;
import org.goober.linkmod.entitystuff.AgentPillagerEntity;
import org.goober.linkmod.entitystuff.DesperadoEntity;

public class DesperadoEntityRenderer extends LinksIllagerEntityRenderer<DesperadoEntity, LinksIllagerEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.of("lmod","textures/entity/snowpillager2.png");
    public static final EntityModelLayer DESPERADO_LAYER =
            new EntityModelLayer(Identifier.of("lmod", "stalker"), "main");
    private static final Identifier RED_TEXTURE = Identifier.of("lmod","textures/entity/cowboypillager2");
    private static final Identifier BLUE_TEXTURE = Identifier.of("lmod", "textures/entity/cowboypillager");


    public DesperadoEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new DesperadoEntityModel(context.getPart(DesperadoEntityRenderer.DESPERADO_LAYER)), 0.5F);
        this.addFeature(new HeldItemFeatureRenderer<>(this));
    }

    public Identifier getTexture(LinksIllagerEntityRenderState linksillagerEntityRenderState) {
        Identifier var10000;
        switch (linksillagerEntityRenderState.type) {
            case RED -> var10000 = RED_TEXTURE;
            case BLUE -> var10000 = BLUE_TEXTURE;
            default -> throw new MatchException((String)null, (Throwable)null);
        }

        return var10000;
    }

    public LinksIllagerEntityRenderState createRenderState() {
        return new LinksIllagerEntityRenderState();
    }
    public void updateRenderState(DesperadoEntity desperadoEntity, LinksIllagerEntityRenderState linksIllagerEntityRenderState, float f) {
        super.updateRenderState(desperadoEntity, linksIllagerEntityRenderState, f);
        linksIllagerEntityRenderState.type = desperadoEntity.getVariant();
    }
}
