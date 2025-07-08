package org.goober.linkmod.gunstuff.items;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Bullets {
    private static final Map<String, BulletType> BULLET_TYPES = new HashMap<>();
    
    // define bullet types with tags
    static {
        register("standard", new BulletType("Standard Bullet", 1.0f, 0xFF888888, Set.of("rifle_ammo")));
        register("buckshotgunshell", new BulletType("Buckshot", 0.6f, 0xFF444444, Set.of("shotgun_shells")));
    }
    
    public static void register(String id, BulletType bulletType) {
        BULLET_TYPES.put(id, bulletType);
    }
    
    public static BulletType get(String id) {
        return BULLET_TYPES.getOrDefault(id, BULLET_TYPES.get("standard"));
    }
    
    public static Map<String, BulletType> getAll() {
        return new HashMap<>(BULLET_TYPES);
    }
    
    public record BulletType(
        String displayName,
        float damageMultiplier,
        int color,
        Set<String> tags
    ) {
        // check if this bullet has a specific tag
        public boolean hasTag(String tag) {
            return tags.contains(tag);
        }
    }
}