//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.goober.linkmod.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.IllagerEntity.State;
import net.minecraft.util.Arm;
import org.goober.linkmod.entitystuff.DesperadoEntity;

@Environment(EnvType.CLIENT)
public class LinksIllagerEntityRenderState extends ArmedEntityRenderState {
    public boolean hasVehicle;
    public boolean attacking;
    public Arm illagerMainArm;
    public IllagerEntity.State illagerState;
    public int crossbowPullTime;
    public int itemUseTime;
    public float handSwingProgress;
    public DesperadoEntity.Variant type;

    public LinksIllagerEntityRenderState() {
        this.illagerMainArm = Arm.RIGHT;
        this.illagerState = State.NEUTRAL;
        this.type = DesperadoEntity.Variant.RED;
    }
}
