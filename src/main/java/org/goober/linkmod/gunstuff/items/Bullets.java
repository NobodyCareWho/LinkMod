package org.goober.linkmod.gunstuff.items;

import java.util.HashMap;
import java.util.Map;

public class Bullets {
    private static final Map<String, BulletType> BULLET_TYPES = new HashMap<>();
    
    // define bullet types
    static {
        register("standard", new BulletType("Standard Bullet", 1.0f, 0xFF888888));
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
        int color
    ) {}
}