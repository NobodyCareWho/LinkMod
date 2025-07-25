package org.goober.linkmod.client.renderer;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.goober.linkmod.projectilestuff.PillGrenadeEntity;
import org.goober.linkmod.client.model.PillGrenadeEntityModel;

public class PillGrenadeEntityRenderer extends EntityRenderer<PillGrenadeEntity, PillGrenadeEntityRenderer.PillGrenadeRenderState> {
    private static final Identifier TEXTURE = Identifier.of("lmod", "textures/entity/pillgrenade.png");
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Identifier.of("lmod", "pill_grenade"), "main");
    private final PillGrenadeEntityModel model;
    
    public PillGrenadeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new PillGrenadeEntityModel(context.getPart(MODEL_LAYER));
    }
    
    @Override
    public PillGrenadeRenderState createRenderState() {
        return new PillGrenadeRenderState();
    }
    
    @Override
    public void updateRenderState(PillGrenadeEntity entity, PillGrenadeRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.age = entity.age + tickDelta;
    }
    
    @Override
    public void render(PillGrenadeRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0.0, -1.5, 0.0);
        this.model.setAngles(state.age);
        var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));
        ModelPart root = this.model.getPart();
        root.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        
        matrices.pop();
        super.render(state, matrices, vertexConsumers, light);
    }
    
    public static class PillGrenadeRenderState extends EntityRenderState {
        public float age;
    }
}