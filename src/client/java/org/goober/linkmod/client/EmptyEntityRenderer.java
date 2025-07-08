package org.goober.linkmod.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class EmptyEntityRenderer<T extends Entity> extends EntityRenderer<T, EntityRenderState> {
    private static final Identifier EMPTY_TEXTURE = Identifier.of("minecraft", "textures/misc/white.png");
    
    public EmptyEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }
    
    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
    
    @Override
    public void updateRenderState(T entity, EntityRenderState state, float tickDelta) {
        // do nothing - entity is invisible
    }
    
    @Override
    public void render(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        // do nothing - entity is invisible
    }
}