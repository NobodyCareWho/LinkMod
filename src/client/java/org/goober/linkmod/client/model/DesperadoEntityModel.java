package org.goober.linkmod.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.ArmPosing;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;


public class DesperadoEntityModel extends IllagerEntityModel<IllagerEntityRenderState> {
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart nose;
	private final ModelPart body;
	private final ModelPart leftArm;
	private final ModelPart rightArm;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;

	public DesperadoEntityModel(ModelPart root) {
		super(root);
		this.head = root.getChild("head");
		this.hat = root.getChild("hat");
		this.nose = root.getChild("nose");
		this.body = root.getChild("body");
		this.leftArm = root.getChild("leftArm");
		this.rightArm = root.getChild("rightArm");
		this.leftLeg = root.getChild("leftLeg");
		this.rightLeg = root.getChild("rightLeg");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 7).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new Dilation(0.0F))
				.uv(0, 29).cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.rotation(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(-2, -3).cuboid(0.0F, -1.5F, -2.0F, 0.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5F, 6.0F, 0.0F, 0.0F, 0.4363F));

		ModelPartData hat = modelPartData.addChild("hat", ModelPartBuilder.create().uv(28, 0).cuboid(-4.5F, -4.0F, -4.5F, 9.0F, 4.0F, 9.0F, new Dilation(0.0F))
				.uv(38, 26).mirrored().cuboid(-4.5F, -5.0F, -4.5F, 4.0F, 1.0F, 9.0F, new Dilation(0.0F)).mirrored(false)
				.uv(38, 26).cuboid(0.5F, -5.0F, -4.5F, 4.0F, 1.0F, 9.0F, new Dilation(0.0F))
				.uv(29, 13).cuboid(-5.5F, 0.0F, -6.5F, 11.0F, 0.0F, 13.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(-4.5F, 0.0F, -6.75F, 9.0F, 0.0F, 0.0F, new Dilation(0.0F))
				.uv(0, 25).cuboid(-2.5F, -4.0F, -4.75F, 5.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.rotation(0.0F, -6.0F, 0.0F));

		ModelPartData cube_r2 = hat.addChild("cube_r2", ModelPartBuilder.create().uv(31, 51).mirrored().cuboid(0.0F, 0.0F, -2.0F, 2.0F, 0.0F, 13.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(5.5F, 0.0F, -4.5F, 0.0F, 0.0F, -0.3927F));

		ModelPartData cube_r3 = hat.addChild("cube_r3", ModelPartBuilder.create().uv(31, 51).cuboid(-2.0F, 0.0F, -2.0F, 2.0F, 0.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(-5.5F, 0.0F, -4.5F, 0.0F, 0.0F, 0.3927F));

		ModelPartData nose = modelPartData.addChild("nose", ModelPartBuilder.create().uv(24, 0).cuboid(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.rotation(0.0F, -2.0F, 0.0F));

		ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 30).cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new Dilation(0.0F))
				.uv(0, 49).cuboid(-4.0F, 9.0F, -3.0F, 8.0F, 9.0F, 6.0F, new Dilation(0.25F)), ModelTransform.rotation(0.0F, 0.0F, 0.0F));

		ModelPartData leftArm = modelPartData.addChild("leftArm", ModelPartBuilder.create().uv(28, 48).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.rotation(5.0F, 2.0F, 0.0F));

		ModelPartData rightArm = modelPartData.addChild("rightArm", ModelPartBuilder.create().uv(28, 48).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.rotation(-5.0F, 2.0F, 0.0F));

		ModelPartData leftLeg = modelPartData.addChild("leftLeg", ModelPartBuilder.create().uv(0, 33).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.rotation(2.0F, 12.0F, 0.0F));

		ModelPartData rightLeg = modelPartData.addChild("rightLeg", ModelPartBuilder.create().uv(0, 33).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.rotation(-2.0F, 12.0F, 0.0F));
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