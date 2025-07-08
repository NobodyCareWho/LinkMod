package org.goober.linkmod.gunstuff.items;

import java.util.HashMap;
import java.util.Map;

public class Guns {
    private static final Map<String, GunType> GUN_TYPES = new HashMap<>();
    
    // define gun types
    static {
        register("rifle", new GunType("Rifle", 8.0f, 4.0f, 20, 1));
        register("shotgun", new GunType("Shotgun", 4.0f, 2.5f, 25, 5));
    }
    
    public static void register(String id, GunType gunType) {
        GUN_TYPES.put(id, gunType);
    }
    
    public static GunType get(String id) {
        return GUN_TYPES.getOrDefault(id, GUN_TYPES.get("rifle"));
    }
    
    public static Map<String, GunType> getAll() {
        return new HashMap<>(GUN_TYPES);
    }
    
    public record GunType(
        String displayName,
        float damage,
        float velocity,
        int fireRate,
        int pelletsPerShot
    ) {}
}