package org.goober.linkmod.miscstuff;

import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.apache.commons.lang3.ObjectUtils;
import org.goober.linkmod.miscstuff.soundprofiles.BulletSoundProfile;

import java.util.HashMap;
import java.util.Map;

public class ParticleProfile {
    private static final Map<String, ParticleProfile.PP> PARTICLEPROFILE = new HashMap<>();


    // define bullet types with tags
    static {
        register("standard", new ParticleProfile.PP( null, null, ParticleTypes.ELECTRIC_SPARK, ParticleTypes.CAMPFIRE_COSY_SMOKE, null));
        register("sparkle", new ParticleProfile.PP( null, null, ParticleTypes.FIREWORK, ParticleTypes.CAMPFIRE_COSY_SMOKE, null));
        register("goofyahh", new ParticleProfile.PP( ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.CRIMSON_SPORE, ParticleTypes.SONIC_BOOM, ParticleTypes.CAMPFIRE_COSY_SMOKE, null));
        register("breeze", new ParticleProfile.PP( ParticleTypes.GUST_EMITTER_LARGE, ParticleTypes.CRIMSON_SPORE, ParticleTypes.SONIC_BOOM, ParticleTypes.CAMPFIRE_COSY_SMOKE, null));
        register("blaze", new ParticleProfile.PP( null, ParticleTypes.LAVA, ParticleTypes.ELECTRIC_SPARK, ParticleTypes.CAMPFIRE_COSY_SMOKE, null));
        register("freeze", new ParticleProfile.PP( null, ParticleTypes.SNOWFLAKE, ParticleTypes.ELECTRIC_SPARK, ParticleTypes.CAMPFIRE_COSY_SMOKE, null));
        register("flare", new ParticleProfile.PP( null, ParticleTypes.CAMPFIRE_COSY_SMOKE, ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, ParticleTypes.CAMPFIRE_COSY_SMOKE, null));
        register("slime", new ParticleProfile.PP( null, ParticleTypes.ITEM_SLIME, ParticleTypes.ITEM_SLIME, ParticleTypes.ELECTRIC_SPARK, ParticleTypes.ELECTRIC_SPARK));

    }

    public static void register(String id, ParticleProfile.PP pP) {
        PARTICLEPROFILE.put(id, pP);
    }

    public static ParticleProfile.PP get(String id) {
        return PARTICLEPROFILE.getOrDefault(id, PARTICLEPROFILE.get("standard"));
    }

    public static Map<String, ParticleProfile.PP> getAll() {
        return new HashMap<>(PARTICLEPROFILE);
    }

    public record PP(
            ParticleEffect fireparticle,
            ParticleEffect trailparticle,
            ParticleEffect bulletparticle,
            ParticleEffect impactparticle,
            ParticleEffect entityimpactparticle
    ) {
    }
}
