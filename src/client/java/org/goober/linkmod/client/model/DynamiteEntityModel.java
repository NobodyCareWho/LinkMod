package org.goober.linkmod.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.goober.linkmod.projectilestuff.DynamiteEntity;

public class DynamiteEntityModel extends EntityModel<EntityRenderState> {
	private final ModelPart lmod_main;

	public DynamiteEntityModel(ModelPart root) {
		super(root, RenderLayer::getEntityCutoutNoCull);
		this.lmod_main = root.getChild("lmod_main");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bb_main = modelPartData.addChild("lmod_main", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.rotation(0.0F, 24.0F, 0.0F));

		ModelPartData cube_r1 = bb_main.addChild("cube_r1", ModelPartBuilder.create().uv(8, 3).cuboid(0.0F, -10.0F, -0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		ModelPartData cube_r2 = bb_main.addChild("cube_r2", ModelPartBuilder.create().uv(8, 0).cuboid(0.0F, -10.0F, -0.5F, 0.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
		return TexturedModelData.of(modelData, 16, 16);
	}

	public void setAngles(float ageInTicks) {
		// rotate in the air
		this.lmod_main.yaw = ageInTicks * 0.0F;
		this.lmod_main.pitch = ageInTicks * 0.2F;
	}

	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		lmod_main.render(matrices, vertexConsumer, light, overlay);
	}
}