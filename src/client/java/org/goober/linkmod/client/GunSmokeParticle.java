package org.goober.linkmod.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class GunSmokeParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;

    public GunSmokeParticle(ClientWorld clientWorld, double x, double y, double z,
                            SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z);
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
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
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            int frame = (this.age * 8) / this.maxAge; // Assuming 6 frames in your animation
            this.setSprite(this.spriteProvider.getSprite(frame, 8)); // 6 is the number of frames
        }
    }


    @Environment(EnvType.CLIENT)
    public static class GunSmokeFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider field_50230;

        public GunSmokeFactory(SpriteProvider spriteProvider) {
            this.field_50230 = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Particle particle = new GunSmokeParticle(clientWorld, d, e, f, this.field_50230);
            particle.scale(1.0f);
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
            return new GunSmokeParticle(clientWorld, d, e, f, this.spriteProvider);
        }
    }

}
