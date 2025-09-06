package org.goober.linkmod.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.village.raid.Raid;
import org.goober.linkmod.entitystuff.LmodEntityRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public class RaidMixin {
    
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addCustomPillagers(CallbackInfo ci) {
        // your gonna need to modify the enum for ur pillagers btw, pillagr stuff go here
        // example spawn counts array: new int[]{0, 2, 1, 2, 3, 3, 4, 2}
    }
}