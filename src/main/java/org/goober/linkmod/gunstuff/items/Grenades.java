package org.goober.linkmod.gunstuff.items;

import org.goober.linkmod.miscstuff.ParticleProfile;
import org.goober.linkmod.miscstuff.soundprofiles.BulletSoundProfile;
import org.goober.linkmod.projectilestuff.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Grenades {
    private static final Map<String, Grenades.GrenadeType> GRENADE_TYPES = new HashMap<>();


    // define bullet types with tags
    static {
        register("standard", new Grenades.GrenadeType("Grenade",false , false, 4f, 3, 100, 0.6f));
        register("demo", new Grenades.GrenadeType("Demo Grenade",true , false, 3f, 1, 100, 0.6f));
        register("he", new Grenades.GrenadeType("HE Grenade",false , false, 6f, 3, 100, 0.6f));
        register("incendiary", new Grenades.GrenadeType("Incendiary Grenade",false , true, 4f, 3, 100, 0.6f));
        register("bouncy", new Grenades.GrenadeType("Bouncy Grenade",false , false, 3f, 10, 100, 0.9f));
        register("shape", new Grenades.GrenadeType("Shape Grenade",false , false, 1f, 1, 100, 0.6f));

    }

    public static void register(String id, Grenades.GrenadeType grenadeType) {
        GRENADE_TYPES.put(id, grenadeType);
    }

    public static Grenades.GrenadeType get(String id) {
        return GRENADE_TYPES.getOrDefault(id, GRENADE_TYPES.get("standard"));
    }

    public static Map<String, Grenades.GrenadeType> getAll() {
        return new HashMap<>(GRENADE_TYPES);
    }

    public record GrenadeType(
            String displayName,
            boolean destroysTerrain,
            boolean createsFire,
            float explosionSize,
            int bounces,
            float lifetime,
            float bounciness

    ) {
        // check if this bullet has a specific tag

    }
}
