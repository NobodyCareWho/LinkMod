package org.goober.linkmod.client.renderer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.goober.linkmod.client.model.DynamiteEntityModel;
import org.goober.linkmod.projectilestuff.DynamiteEntity;
import org.goober.linkmod.projectilestuff.PillagerDynamiteEntity;

public class PillagerDynamiteEntityRenderer extends EntityRenderer<PillagerDynamiteEntity, PillagerDynamiteEntityRenderer.PillagerDynamiteEntityRenderState> {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Identifier.of("lmod", "dynamite"), "main");
    public static final Identifier TEXTURE = Identifier.of("lmod", "textures/entity/dynamite.png");
    private final DynamiteEntityModel model;

    public PillagerDynamiteEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new DynamiteEntityModel(context.getPart(MODEL_LAYER));
    }

    public void render(PillagerDynamiteEntityRenderState pillagerDynamiteEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(pillagerDynamiteEntityRenderState.yaw - 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(pillagerDynamiteEntityRenderState.pitch + 90.0F));
        VertexConsumer vertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumerProvider, this.model.getLayer(TEXTURE), false, pillagerDynamiteEntityRenderState.enchanted);
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(pillagerDynamiteEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public PillagerDynamiteEntityRenderer.PillagerDynamiteEntityRenderState createRenderState() {
        return new PillagerDynamiteEntityRenderer.PillagerDynamiteEntityRenderState();
    }

    public void updateRenderState(PillagerDynamiteEntity pillagerDynamiteEntity, PillagerDynamiteEntityRenderState pillagerDynamiteEntityRenderState, float f) {
        super.updateRenderState(pillagerDynamiteEntity, pillagerDynamiteEntityRenderState, f);
        pillagerDynamiteEntityRenderState.yaw = pillagerDynamiteEntity.getLerpedYaw(f);
        pillagerDynamiteEntityRenderState.pitch = pillagerDynamiteEntity.getLerpedPitch(f);
    }

    public static class PillagerDynamiteEntityRenderState extends EntityRenderState {
        public float yaw;
        public float pitch;
        public boolean enchanted;
    }

}