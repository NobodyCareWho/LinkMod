package org.goober.linkmod.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.ArmPosing;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import org.goober.linkmod.entitystuff.AgentPillagerEntity;

// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class Model extends EntityModel<AgentPillagerEntity> {
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart nose;
	private final ModelPart body;
	private final ModelPart left_arm;
	private final ModelPart right_arm;
	private final ModelPart left_leg;
	private final ModelPart right_leg;
	public Model(ModelPart root) {
		this.head = root.getChild("head");
		this.hat = root.getChild("hat");
		this.nose = root.getChild("nose");
		this.body = root.getChild("body");
		this.left_arm = root.getChild("left_arm");
		this.right_arm = root.getChild("right_arm");
		this.left_leg = root.getChild("left_leg");
		this.right_leg = root.getChild("right_leg");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new Dilation(0.0F))
		.uv(32, 14).cuboid(-4.0F, -5.0F, -5.0F, 8.0F, 3.0F, 1.0F, new Dilation(0.0F))
		.uv(28, 0).cuboid(-4.0F, -6.0F, -8.0F, 8.0F, 0.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData hat = modelPartData.addChild("hat", ModelPartBuilder.create(), ModelTransform.rotation(0.0F, 24.0F, 0.0F));

		ModelPartData nose = modelPartData.addChild("nose", ModelPartBuilder.create().uv(24, 0).cuboid(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -2.0F, 0.0F));

		ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 20).cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new Dilation(0.0F))
		.uv(0, 38).cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new Dilation(0.25F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_arm = modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(40, 46).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(5.0F, 2.0F, 0.0F));

		ModelPartData right_arm = modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(40, 46).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, 2.0F, 0.0F));

		ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(2.0F, 12.0F, 0.0F));

		ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 22).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 12.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	public void setAngles(S illagerEntityRenderState) {
		super.setAngles(illagerEntityRenderState);
		this.head.yaw = illagerEntityRenderState.relativeHeadYaw * 0.017453292F;
		this.head.pitch = illagerEntityRenderState.pitch * 0.017453292F;
		if (illagerEntityRenderState.hasVehicle) {
			this.right_arm.pitch = -0.62831855F;
			this.right_arm.yaw = 0.0F;
			this.right_arm.roll = 0.0F;
			this.left_arm.pitch = -0.62831855F;
			this.left_arm.yaw = 0.0F;
			this.left_arm.roll = 0.0F;
			this.right_arm.pitch = -1.4137167F;
			this.right_arm.yaw = 0.31415927F;
			this.right_arm.roll = 0.07853982F;
			this.left_arm.pitch = -1.4137167F;
			this.left_arm.yaw = -0.31415927F;
			this.left_arm.roll = -0.07853982F;
		} else {
			float f = illagerEntityRenderState.limbSwingAmplitude;
			float g = illagerEntityRenderState.limbSwingAnimationProgress;
			this.right_arm.pitch = MathHelper.cos(g * 0.6662F + 3.1415927F) * 2.0F * f * 0.5F;
			this.right_arm.yaw = 0.0F;
			this.right_arm.roll = 0.0F;
			this.left_arm.pitch = MathHelper.cos(g * 0.6662F) * 2.0F * f * 0.5F;
			this.left_arm.yaw = 0.0F;
			this.left_arm.roll = 0.0F;
			this.right_arm.pitch = MathHelper.cos(g * 0.6662F) * 1.4F * f * 0.5F;
			this.right_arm.yaw = 0.0F;
			this.right_arm.roll = 0.0F;
			this.left_arm.pitch = MathHelper.cos(g * 0.6662F + 3.1415927F) * 1.4F * f * 0.5F;
			this.left_arm.yaw = 0.0F;
			this.left_arm.roll = 0.0F;
		}

		IllagerEntity.State state = illagerEntityRenderState.illagerState;
		if (state == IllagerEntity.State.ATTACKING) {
			if (illagerEntityRenderState.getMainHandItemState().isEmpty()) {
				ArmPosing.zombieArms(this.left_arm, this.right_arm, true, illagerEntityRenderState.handSwingProgress, illagerEntityRenderState.age);
			} else {
				ArmPosing.meleeAttack(this.right_arm, this.left_arm, illagerEntityRenderState.illagerMainArm, illagerEntityRenderState.handSwingProgress, illagerEntityRenderState.age);
			}
		} else if (state == IllagerEntity.State.SPELLCASTING) {
			this.right_arm.originZ = 0.0F;
			this.right_arm.originX = -5.0F;
			this.left_arm.originZ = 0.0F;
			this.left_arm.originX = 5.0F;
			this.right_arm.pitch = MathHelper.cos(illagerEntityRenderState.age * 0.6662F) * 0.25F;
			this.left_arm.pitch = MathHelper.cos(illagerEntityRenderState.age * 0.6662F) * 0.25F;
			this.right_arm.roll = 2.3561945F;
			this.left_arm.roll = -2.3561945F;
			this.right_arm.yaw = 0.0F;
			this.left_arm.yaw = 0.0F;
		} else if (state == IllagerEntity.State.BOW_AND_ARROW) {
			this.right_arm.yaw = -0.1F + this.head.yaw;
			this.right_arm.pitch = -1.5707964F + this.head.pitch;
			this.left_arm.pitch = -0.9424779F + this.head.pitch;
			this.left_arm.yaw = this.head.yaw - 0.4F;
			this.left_arm.roll = 1.5707964F;
		} else if (state == IllagerEntity.State.CROSSBOW_HOLD) {
			ArmPosing.hold(this.right_arm, this.left_arm, this.head, true);
		} else if (state == IllagerEntity.State.CROSSBOW_CHARGE) {
			ArmPosing.charge(this.right_arm, this.left_arm, (float)illagerEntityRenderState.crossbowPullTime, illagerEntityRenderState.itemUseTime, true);
		} else if (state == IllagerEntity.State.CELEBRATING) {
			this.right_arm.originZ = 0.0F;
			this.right_arm.originX = -5.0F;
			this.right_arm.pitch = MathHelper.cos(illagerEntityRenderState.age * 0.6662F) * 0.05F;
			this.right_arm.roll = 2.670354F;
			this.right_arm.yaw = 0.0F;
			this.left_arm.originZ = 0.0F;
			this.left_arm.originX = 5.0F;
			this.left_arm.pitch = MathHelper.cos(illagerEntityRenderState.age * 0.6662F) * 0.05F;
			this.left_arm.roll = -2.3561945F;
			this.left_arm.yaw = 0.0F;
		}

	private ModelPart getAttackingArm(Arm arm) {
		return arm == Arm.LEFT ? this.left_arm : this.right_arm;
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