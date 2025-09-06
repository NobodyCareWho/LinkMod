package org.goober.linkmod.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;
import org.goober.linkmod.entitystuff.AgentPillagerEntity;
import org.goober.linkmod.entitystuff.DesperadoEntity;
import org.goober.linkmod.entitystuff.LmodEntityRegistry;
import org.goober.linkmod.entitystuff.StalkerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.command.EntitySelectorReader.RANDOM;

@Mixin(Raid.class)
public class RaidMixin {
    @Inject(method = "addRaider", at = @At("HEAD"), cancellable = true)
    private void injectDesperado(ServerWorld world, int wave, RaiderEntity raider, BlockPos pos, boolean existing, CallbackInfo ci) {
        if (raider.getType() == EntityType.PILLAGER) {
            float temp = world.getBiome(pos).value().getTemperature();
            RaiderEntity replacement;
            if (raider.getRandom().nextFloat() < 0.3F) {
                if (temp < 0.3F) {
                    // Cold biome replacement
                    replacement = new StalkerEntity(LmodEntityRegistry.STALKER, world);
                } else if (temp < 1.0F) {
                    // Normal biome replacement
                    replacement = new AgentPillagerEntity(LmodEntityRegistry.AGENTPILLAGER, world);
                } else {
                    // Hot biome replacement
                    replacement = new DesperadoEntity(LmodEntityRegistry.DESPERADO, world);
                }

                replacement.refreshPositionAndAngles(
                        raider.getX(),
                        raider.getY(),
                        raider.getZ(),
                        raider.getYaw(),
                        raider.getPitch()
                );

            ((Raid)(Object)this).addRaider(world, wave, replacement, pos, existing);

            raider.discard(); // remove vanilla pillager
            ci.cancel();
            }
        }
    }
}