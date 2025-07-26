// Made with Blockbench 4.12.5
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class PillGrenadeModel extends EntityModel<PillGrenadeEntity> {
	private final ModelPart lmod_main;
	public PillGrenadeModel(ModelPart root) {
		this.lmod_main = root.getChild("lmod_main");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData lmod_main = modelPartData.addChild("lmod_main", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}
	@Override
	public void setAngles(PillGrenadeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		lmod_main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}