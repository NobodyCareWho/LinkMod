package org.goober.linkmod.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.ArmPosing;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

public class AgentPillagerEntityModel extends IllagerEntityModel<IllagerEntityRenderState> {
    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart nose;
    private final ModelPart body;
    private final ModelPart arms;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public AgentPillagerEntityModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.hat = head.getChild("hat");
        this.nose = head.getChild("nose");
        this.body = root.getChild("body");
        this.arms = root.getChild("arms");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        ModelPartData head = root.addChild("head", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4.0F, -10.0F, -4.0F, 8, 10, 8)
                .uv(32, 14).cuboid(-4.0F, -5.0F, -5.0F, 8, 3, 1)
                .uv(28, 0).cuboid(-4.0F, -6.0F, -8.0F, 8, 0, 4), ModelTransform.NONE);

        head.addChild("hat", ModelPartBuilder.create(), ModelTransform.NONE);

        head.addChild("nose", ModelPartBuilder.create()
                .uv(24, 0).cuboid(-1.0F, -1.0F, -6.0F, 2, 4, 2), 
                ModelTransform.of(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        root.addChild("body", ModelPartBuilder.create()
                .uv(16, 20).cuboid(-4.0F, 0.0F, -3.0F, 8, 12, 6)
                .uv(0, 38).cuboid(-4.0F, 0.0F, -3.0F, 8, 18, 6, new Dilation(0.25F)), ModelTransform.NONE);

        root.addChild("arms", ModelPartBuilder.create()
                .uv(40, 38).cuboid(-8.0F, -2.0F, -2.0F, 8, 4, 4), 
                ModelTransform.of(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        root.addChild("left_arm", ModelPartBuilder.create()
                .uv(40, 46).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 4, 12, 4), 
                ModelTransform.of(5.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        root.addChild("right_arm", ModelPartBuilder.create()
                .uv(40, 46).cuboid(-3.0F, -2.0F, -2.0F, 4, 12, 4), 
                ModelTransform.of(-5.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        root.addChild("left_leg", ModelPartBuilder.create()
                .uv(0, 22).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4), 
                ModelTransform.of(2.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        root.addChild("right_leg", ModelPartBuilder.create()
                .uv(0, 22).cuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4), 
                ModelTransform.of(-2.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(IllagerEntityRenderState state) {
        super.setAngles(state);

        this.head.yaw = state.relativeHeadYaw * 0.017453292F;
        this.head.pitch = state.pitch * 0.017453292F;

        float swingAmp = state.limbSwingAmplitude;
        float swing = state.limbSwingAnimationProgress;

        if (!state.hasVehicle) {
            this.rightArm.pitch = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 2.0F * swingAmp * 0.5F;
            this.leftArm.pitch = MathHelper.cos(swing * 0.6662F) * 2.0F * swingAmp * 0.5F;
        }

        switch (state.illagerState) {
            case ATTACKING -> {
                if (state.getMainHandItemState().isEmpty()) {
                    ArmPosing.zombieArms(this.leftArm, this.rightArm, true, state.handSwingProgress, state.age);
                } else {
                    ArmPosing.meleeAttack(this.rightArm, this.leftArm, state.illagerMainArm, state.handSwingProgress, state.age);
                }
            }
            case SPELLCASTING -> {
                this.rightArm.originX = -5.0F;
                this.leftArm.originX = 5.0F;
                this.rightArm.pitch = MathHelper.cos(state.age * 0.6662F) * 0.25F;
                this.leftArm.pitch = MathHelper.cos(state.age * 0.6662F) * 0.25F;
                this.rightArm.roll = 2.3561945F;
                this.leftArm.roll = -2.3561945F;
            }
            case BOW_AND_ARROW -> {
                this.rightArm.yaw = -0.1F + this.head.yaw;
                this.rightArm.pitch = -1.5707964F + this.head.pitch;
                this.leftArm.pitch = -0.9424779F + this.head.pitch;
                this.leftArm.yaw = this.head.yaw - 0.4F;
                this.leftArm.roll = 1.5707964F;
            }
            case CROSSBOW_HOLD -> ArmPosing.hold(this.rightArm, this.leftArm, this.head, true);
            case CROSSBOW_CHARGE -> ArmPosing.charge(this.rightArm, this.leftArm, (float)state.crossbowPullTime, state.itemUseTime, true);
            case CELEBRATING -> {
                this.rightArm.originX = -5.0F;
                this.leftArm.originX = 5.0F;
                this.rightArm.pitch = MathHelper.cos(state.age * 0.6662F) * 0.05F;
                this.leftArm.pitch = MathHelper.cos(state.age * 0.6662F) * 0.05F;
                this.rightArm.roll = 2.670354F;
                this.leftArm.roll = -2.3561945F;
            }
        }
    }

    private ModelPart getAttackingArm(Arm arm) {
        return arm == Arm.LEFT ? this.leftArm : this.rightArm;
    }

    public ModelPart getHat() { 
        return this.hat; 
    }
    
    public ModelPart getHead() { 
        return this.head; 
    }

    public void setArmAngle(Arm arm, MatrixStack matrices) {
        this.root.applyTransform(matrices);
        this.getAttackingArm(arm).applyTransform(matrices);
    }
}