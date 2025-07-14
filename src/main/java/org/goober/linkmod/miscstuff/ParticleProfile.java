package org.goober.linkmod.miscstuff;

import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.goober.linkmod.miscstuff.soundprofiles.BulletSoundProfile;

import java.util.HashMap;
import java.util.Map;

public class ParticleProfile {
    private static final Map<String, ParticleProfile.PP> PARTICLEPROFILE = new HashMap<>();


    // define bullet types with tags
    static {
        register("standard", new ParticleProfile.PP( ParticleTypes.ANGRY_VILLAGER, ParticleTypes.ASH, ParticleTypes.ELECTRIC_SPARK));
        register("goofyahh", new ParticleProfile.PP( ParticleTypes.GUST_EMITTER_LARGE, ParticleTypes.CRIMSON_SPORE, ParticleTypes.SONIC_BOOM));
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
            ParticleType fireparticle,
            ParticleType trailparticle,
            ParticleType bulletparticle
    ) {
    }
}
