package org.goober.linkmod.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class PillGrenadeEntityModel extends EntityModel<EntityRenderState> {
    private final ModelPart lmod_main;
    
    public PillGrenadeEntityModel(ModelPart root) {
        super(root, RenderLayer::getEntityCutoutNoCull);
        this.lmod_main = root.getChild("lmod_main");
    }
    
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData lmod_main = modelPartData.addChild("lmod_main", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }
    
    public void setAngles(float ageInTicks) {
        // Rotate the grenade as it flies
        this.lmod_main.yaw = ageInTicks * 0.3F;
        this.lmod_main.pitch = ageInTicks * 0.2F;
    }
    
    public ModelPart getPart() {
        return this.lmod_main;
    }
}