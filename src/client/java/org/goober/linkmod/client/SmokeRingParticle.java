package org.goober.linkmod.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class SmokeRingParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;

    public SmokeRingParticle(ClientWorld clientWorld, double x, double y, double z,
                             SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z);
        this.spriteProvider = spriteProvider;
        this.maxAge = 12 + this.random.nextInt(4);
        this.scale = 1.0F;
        this.setBoundingBoxSpacing(1.0F, 1.0F);
        this.red = 1.0F;
        this.green = 1.0F;
        this.blue = 1.0F;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.dead) {
            this.setSpriteForAge(this.spriteProvider);
        }
    }


    @Environment(EnvType.CLIENT)
    public static class SmokeRingFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public SmokeRingFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            SmokeRingParticle particle = new SmokeRingParticle(clientWorld, d, e, f, this.spriteProvider);
            particle.setSprite(this.spriteProvider);
            particle.scale(0.15F);
            return particle;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new SmokeRingParticle(clientWorld, d, e, f, this.spriteProvider);
        }
    }

}
