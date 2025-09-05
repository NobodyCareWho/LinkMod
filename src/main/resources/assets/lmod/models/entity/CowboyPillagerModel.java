// Made with Blockbench 4.12.6
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class model extends EntityModel<CowboyPillagerEntity> {
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart nose;
	private final ModelPart body;
	private final ModelPart left_arm;
	private final ModelPart right_arm;
	private final ModelPart left_leg;
	private final ModelPart right_leg;
	public model(ModelPart root) {
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
		ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 7).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new Dilation(0.0F))
		.uv(0, 29).cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData head_r1 = head.addChild("head_r1", ModelPartBuilder.create().uv(-2, -3).cuboid(0.0F, -1.5F, -2.0F, 0.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -2.5F, 6.0F, 0.0F, 0.0F, 0.4363F));

		ModelPartData hat = modelPartData.addChild("hat", ModelPartBuilder.create().uv(28, 0).cuboid(-4.5F, -4.0F, -4.5F, 9.0F, 4.0F, 9.0F, new Dilation(0.0F))
		.uv(38, 26).mirrored().cuboid(-4.5F, -5.0F, -4.5F, 4.0F, 1.0F, 9.0F, new Dilation(0.0F)).mirrored(false)
		.uv(38, 26).cuboid(0.5F, -5.0F, -4.5F, 4.0F, 1.0F, 9.0F, new Dilation(0.0F))
		.uv(29, 13).cuboid(-5.5F, 0.0F, -6.5F, 11.0F, 0.0F, 13.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-4.5F, 0.0F, -6.75F, 9.0F, 0.0F, 0.0F, new Dilation(0.0F))
		.uv(0, 25).cuboid(-2.5F, -4.0F, -4.75F, 5.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, 0.0F));

		ModelPartData hat_r1 = hat.addChild("hat_r1", ModelPartBuilder.create().uv(31, 51).mirrored().cuboid(0.0F, 0.0F, -2.0F, 2.0F, 0.0F, 13.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(5.5F, 0.0F, -4.5F, 0.0F, 0.0F, -0.3927F));

		ModelPartData hat_r2 = hat.addChild("hat_r2", ModelPartBuilder.create().uv(31, 51).cuboid(-2.0F, 0.0F, -2.0F, 2.0F, 0.0F, 13.0F, new Dilation(0.0F)), ModelTransform.of(-5.5F, 0.0F, -4.5F, 0.0F, 0.0F, 0.3927F));

		ModelPartData nose = modelPartData.addChild("nose", ModelPartBuilder.create().uv(24, 0).cuboid(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -2.0F, 0.0F));

		ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 30).cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new Dilation(0.0F))
		.uv(0, 49).cuboid(-4.0F, 9.0F, -3.0F, 8.0F, 9.0F, 6.0F, new Dilation(0.25F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_arm = modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(28, 48).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(5.0F, 2.0F, 0.0F));

		ModelPartData right_arm = modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(28, 48).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, 2.0F, 0.0F));

		ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 33).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(2.0F, 12.0F, 0.0F));

		ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 33).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 12.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(CowboyPillagerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		hat.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		nose.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		left_arm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		right_arm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		left_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		right_leg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}