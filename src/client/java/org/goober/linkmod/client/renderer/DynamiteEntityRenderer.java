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

public class DynamiteEntityRenderer extends EntityRenderer<DynamiteEntity, DynamiteEntityRenderer.DynamiteEntityRenderState> {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Identifier.of("lmod", "dynamite"), "main");
    public static final Identifier TEXTURE = Identifier.of("lmod", "textures/entity/dynamite.png");
    private final DynamiteEntityModel model;

    public DynamiteEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new DynamiteEntityModel(context.getPart(MODEL_LAYER));
    }

    public void render(DynamiteEntityRenderState dynamiteEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(dynamiteEntityRenderState.yaw - 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(dynamiteEntityRenderState.pitch + 90.0F));
        VertexConsumer vertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumerProvider, this.model.getLayer(TEXTURE), false, dynamiteEntityRenderState.enchanted);
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(dynamiteEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public DynamiteEntityRenderer.DynamiteEntityRenderState createRenderState() {
        return new DynamiteEntityRenderer.DynamiteEntityRenderState();
    }

    public void updateRenderState(DynamiteEntity dynamiteEntity, DynamiteEntityRenderState dynamiteEntityRenderState, float f) {
        super.updateRenderState(dynamiteEntity, dynamiteEntityRenderState, f);
        dynamiteEntityRenderState.yaw = dynamiteEntity.getLerpedYaw(f);
        dynamiteEntityRenderState.pitch = dynamiteEntity.getLerpedPitch(f);
    }

    public static class DynamiteEntityRenderState extends EntityRenderState {
        public float yaw;
        public float pitch;
        public boolean enchanted;
    }

}