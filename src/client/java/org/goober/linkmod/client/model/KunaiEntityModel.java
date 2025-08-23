package org.goober.linkmod.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.goober.linkmod.projectilestuff.KunaiEntity;

public class KunaiEntityModel extends EntityModel<EntityRenderState> {
        private final ModelPart lmod_main;

        public KunaiEntityModel(ModelPart root) {
        super(root, RenderLayer::getEntityCutoutNoCull);
        this.lmod_main = root.getChild("lmod_main");
        }

        public static TexturedModelData getTexturedModelData() {
            ModelData modelData = new ModelData();
            ModelPartData modelPartData = modelData.getRoot();
            ModelPartData lmod_main = modelPartData.addChild("lmod_main", ModelPartBuilder.create(), ModelTransform.rotation(0.0F, 24.0F, 0.0F));

            ModelPartData cube_r1 = lmod_main.addChild("cube_r1", ModelPartBuilder.create().uv(0, 17).cuboid(0.0F, -2.0F, -6.0F, 0.0F, 3.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.5F, 0.0F, 0.0F, 3.1416F, 0.0F));

            ModelPartData cube_r2 = lmod_main.addChild("cube_r2", ModelPartBuilder.create().uv(2, 0).cuboid(-4.0F, -1.0F, -6.0F, 7.0F, 0.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, 1.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
            return TexturedModelData.of(modelData, 64, 64);
        }

        public void setAngles(KunaiEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        }

        public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
            lmod_main.render(matrices, vertexConsumer, light, overlay);
        }

}
