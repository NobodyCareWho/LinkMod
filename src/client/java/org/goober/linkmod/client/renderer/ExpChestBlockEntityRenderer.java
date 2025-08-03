package org.goober.linkmod.client.renderer;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.goober.linkmod.blockstuff.blockentities.ExpChestBlockEntity;
import org.goober.linkmod.blockstuff.blocks.ExpChestBlock;

public class ExpChestBlockEntityRenderer implements BlockEntityRenderer<ExpChestBlockEntity> {
    private static final Identifier TEXTURE = Identifier.of("lmod", "textures/block/exp_chest.png");
    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;

    public ExpChestBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        ModelPart modelPart = ctx.getLayerModelPart(EntityModelLayers.CHEST);
        this.bottom = modelPart.getChild("bottom");
        this.lid = modelPart.getChild("lid");
        this.lock = modelPart.getChild("lock");
    }

    @Override
    public void render(ExpChestBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d offset) {
        matrices.push();
        
        // center the model
        matrices.translate(0.5, 0.5, 0.5);

        Direction facing = entity.getCachedState().get(ExpChestBlock.FACING);
        float rotation = switch (facing) {
            case NORTH -> 0;
            case SOUTH -> 180;
            case WEST -> 270;
            case EAST -> 90;
            default -> 0;
        };
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));

        matrices.translate(-0.5, -0.5, -0.5);
        
        // get animation progress
        float animationProgress = entity.getAnimationProgress(tickDelta);
        animationProgress = 1.0F - animationProgress;
        animationProgress = 1.0F - animationProgress * animationProgress * animationProgress;
        
        // render the model
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));

        this.lid.pitch = -(animationProgress * ((float)Math.PI / 2F));
        this.lock.pitch = this.lid.pitch;
        
        this.lid.render(matrices, vertexConsumer, light, overlay);
        this.lock.render(matrices, vertexConsumer, light, overlay);
        this.bottom.render(matrices, vertexConsumer, light, overlay);
        
        matrices.pop();
    }
}