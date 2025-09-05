package org.goober.linkmod.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
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

public class DesperadoEntityRenderer extends IllagerEntityRenderer<DesperadoEntity, IllagerEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.of("lmod","textures/entity/cowboypillager2.png");
    public static final EntityModelLayer DESPERADO_LAYER =
            new EntityModelLayer(Identifier.of("lmod", "desperado"), "main");
    private static final Identifier RED_TEXTURE = Identifier.of("lmod","textures/entity/cowboypillager2.png");
    private static final Identifier BLUE_TEXTURE = Identifier.of("lmod", "textures/entity/cowboypillager.png");
    private static final Identifier GREEN_TEXTURE = Identifier.of("lmod", "textures/entity/cowboypillager3.png");

    public DesperadoEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new DesperadoEntityModel(context.getPart(DesperadoEntityRenderer.DESPERADO_LAYER)), 0.5F);
        this.addFeature(new HeldItemFeatureRenderer<>(this));
    }

    public Identifier getTexture(IllagerEntityRenderState illagerEntityRenderState) {
        //Identifier var10000;
        //switch (desperadoEntityRenderState.type) {
        //    case RED -> var10000 = RED_TEXTURE;
        //    case BLUE -> var10000 = BLUE_TEXTURE;
        //    default -> throw new MatchException((String)null, (Throwable)null);
        //}

        return GREEN_TEXTURE;
    }

    public IllagerEntityRenderState createRenderState() {
        return new IllagerEntityRenderState();
    }
}
