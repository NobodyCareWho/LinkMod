package org.goober.linkmod.client.renderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.TridentEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.goober.linkmod.client.model.KunaiEntityModel;
import org.goober.linkmod.projectilestuff.KunaiEntity;

import static org.goober.linkmod.client.renderer.PillGrenadeEntityRenderer.MODEL_LAYER;

public class KunaiEntityRenderer extends EntityRenderer<KunaiEntity, KunaiEntityRenderer.KunaiEntityRenderState> {
    public static final Identifier TEXTURE = Identifier.of("lmod", "textures/entity/kunai.png");
    private final KunaiEntityModel model;

    public KunaiEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new KunaiEntityModel(context.getPart(MODEL_LAYER));
    }

    public void render(KunaiEntityRenderState kunaiEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(kunaiEntityRenderState.yaw - 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(kunaiEntityRenderState.pitch + 90.0F));
        VertexConsumer vertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumerProvider, this.model.getLayer(TEXTURE), false, kunaiEntityRenderState.enchanted);
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(kunaiEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public KunaiEntityRenderer.KunaiEntityRenderState createRenderState() {
        return new KunaiEntityRenderer.KunaiEntityRenderState();
    }

    public void updateRenderState(KunaiEntity kunaiEntity, KunaiEntityRenderState kunaiEntityRenderState, float f) {
        super.updateRenderState(kunaiEntity, kunaiEntityRenderState, f);
        kunaiEntityRenderState.yaw = kunaiEntity.getLerpedYaw(f);
        kunaiEntityRenderState.pitch = kunaiEntity.getLerpedPitch(f);
        kunaiEntityRenderState.enchanted = kunaiEntity.isEnchanted();
    }

    public static class KunaiEntityRenderState extends EntityRenderState {
        public float yaw;
        public float pitch;
        public boolean enchanted;
    }

}