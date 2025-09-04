//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.goober.linkmod.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.IllagerEntity.State;
import net.minecraft.item.CrossbowItem;
import org.goober.linkmod.client.model.DesperadoEntityModel;
import org.goober.linkmod.entitystuff.DesperadoEntity;

@Environment(EnvType.CLIENT)
public abstract class LinksIllagerEntityRenderer<T extends IllagerEntity, S extends IllagerEntityRenderState> extends MobEntityRenderer<T, S, IllagerEntityModel<S>> {
    protected LinksIllagerEntityRenderer(EntityRendererFactory.Context ctx, DesperadoEntityModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
        this.addFeature(new HeadFeatureRenderer(this, ctx.getEntityModels()));
    }

    public void updateRenderState(T desperadoEntity, S linksIllagerEntityRenderState, float f) {
        super.updateRenderState(desperadoEntity, linksIllagerEntityRenderState, f);

        ArmedEntityRenderState.updateRenderState(desperadoEntity, linksIllagerEntityRenderState, this.itemModelResolver);
        linksIllagerEntityRenderState.hasVehicle = desperadoEntity.hasVehicle();
        linksIllagerEntityRenderState.illagerMainArm = desperadoEntity.getMainArm();
        linksIllagerEntityRenderState.illagerState = desperadoEntity.getState();
        linksIllagerEntityRenderState.crossbowPullTime = linksIllagerEntityRenderState.illagerState == State.CROSSBOW_CHARGE ? CrossbowItem.getPullTime(desperadoEntity.getActiveItem(), desperadoEntity) : 0;
        linksIllagerEntityRenderState.itemUseTime = desperadoEntity.getItemUseTime();
        linksIllagerEntityRenderState.handSwingProgress = desperadoEntity.getHandSwingProgress(f);
        linksIllagerEntityRenderState.attacking = desperadoEntity.isAttacking();
        linksIllagerEntityRenderState.type = DesperadoEntity.Variant.RED;
    }
}
