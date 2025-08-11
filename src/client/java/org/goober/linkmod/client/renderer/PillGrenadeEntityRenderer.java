package org.goober.linkmod.client.renderer;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.goober.linkmod.projectilestuff.PillGrenadeEntity;
import org.goober.linkmod.client.model.PillGrenadeEntityModel;
import java.util.HashMap;
import java.util.Map;

public class PillGrenadeEntityRenderer extends EntityRenderer<PillGrenadeEntity, PillGrenadeEntityRenderer.PillGrenadeRenderState> {
    private static final Identifier DEFAULT_TEXTURE = Identifier.of("lmod", "textures/entity/pillgrenade.png");
    private static final Map<String, Identifier> GRENADE_TEXTURES = new HashMap<>();
    
    static {
        // map each grenade type to its texture
        GRENADE_TEXTURES.put("standard", Identifier.of("lmod", "textures/entity/pillgrenade.png"));
        GRENADE_TEXTURES.put("demo", Identifier.of("lmod", "textures/entity/demopillgrenade.png"));
        GRENADE_TEXTURES.put("he", Identifier.of("lmod", "textures/entity/hepillgrenade.png"));
        GRENADE_TEXTURES.put("incendiary", Identifier.of("lmod", "textures/entity/incendiarypillgrenade.png"));
        GRENADE_TEXTURES.put("bouncy", Identifier.of("lmod", "textures/entity/bouncypillgrenade.png"));
        GRENADE_TEXTURES.put("shape", Identifier.of("lmod", "textures/entity/shapepillgrenade.png"));
    }
    
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Identifier.of("lmod", "pill_grenade"), "main");
    private final PillGrenadeEntityModel model;
    
    public PillGrenadeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new PillGrenadeEntityModel(context.getPart(MODEL_LAYER));
    }
    
    @Override
    public PillGrenadeRenderState createRenderState() {
        return new PillGrenadeRenderState();
    }
    
    @Override
    public void updateRenderState(PillGrenadeEntity entity, PillGrenadeRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.age = entity.age + tickDelta;
        state.grenadeTypeId = entity.getGrenadeTypeId();
    }
    
    @Override
    public void render(PillGrenadeRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0.0, -1.5, 0.0);
        this.model.setAngles(state.age);
        
        // get the appropriate texture based on grenade type
        Identifier texture = GRENADE_TEXTURES.getOrDefault(state.grenadeTypeId, DEFAULT_TEXTURE);
        var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(texture));
        ModelPart root = this.model.getPart();
        root.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        
        matrices.pop();
        super.render(state, matrices, vertexConsumers, light);
    }
    
    public static class PillGrenadeRenderState extends EntityRenderState {
        public float age;
        public String grenadeTypeId = "standard";
    }
}