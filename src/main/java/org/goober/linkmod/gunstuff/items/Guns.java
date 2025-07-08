package org.goober.linkmod.gunstuff.items;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class Guns {
    private static final Map<String, GunType> GUN_TYPES = new HashMap<>();
    
    public enum ShellEjectionMode {
        NONE,      // no shell ejection
        TO_BUNDLE, // eject into gun's bundle
        TO_WORLD   // drop as item in world
    }
    
    // define gun types
    static {
        register("rifle", new GunType("Rifle", 8.0f, 4.0f, 20, 1, 30, Set.of("standard"), "bulletcasing", ShellEjectionMode.TO_WORLD));
        register("shotgun", new GunType("Shotgun", 4.0f, 2.5f, 25, 5, 2, Set.of("buckshotgunshell"), "shotgunshellempty", ShellEjectionMode.TO_BUNDLE));
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
        int pelletsPerShot,
        int maxAmmo,
        Set<String> acceptedAmmoTypes,
        String ejectShellItemId,
        ShellEjectionMode shellEjectionMode
    ) {
        // check if this gun accepts a specific ammo type
        public boolean acceptsAmmo(String ammoType) {
            return acceptedAmmoTypes.contains(ammoType);
        }
        
        // check if this gun ejects shells
        public boolean ejectsShells() {
            return shellEjectionMode != ShellEjectionMode.NONE && ejectShellItemId != null;
        }
    }
}