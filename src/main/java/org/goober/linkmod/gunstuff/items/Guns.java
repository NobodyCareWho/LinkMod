package org.goober.linkmod.gunstuff.items;

import org.goober.linkmod.miscstuff.ParticleProfile;
import org.goober.linkmod.miscstuff.soundprofiles.GunSoundProfile;

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
    
    // define gun types using ammo tags
    static {
        register("rifle", new GunType("Rifle", 14.0f, 10.0f, 20, 8, Set.of("rifle_ammo"), "bulletcasing", ShellEjectionMode.TO_WORLD, 0.2f,GunSoundProfile.get("standard"),ParticleProfile.get("standard")));
        register("shotgun", new GunType("D.B. Shotgun", 7.5f, 2.5f, 2, 2, Set.of("shotgun_shells"), "shotgunshellempty", ShellEjectionMode.TO_BUNDLE, 0.6f,GunSoundProfile.get("standard"),ParticleProfile.get("standard")));
        register("revolver", new GunType("Revolver", 9.2f, 8.0f, 3, 6, Set.of("rifle_ammo"), "bulletcasing", ShellEjectionMode.TO_BUNDLE, 0,GunSoundProfile.get("goofyahh"),ParticleProfile.get("goofyahh")));
        register("ejectorpistol", new GunType("Ejector Pistol", 7.5f, 5.6f, 8, 8, Set.of("rifle_ammo"), "bulletcasing", ShellEjectionMode.TO_WORLD, 0,GunSoundProfile.get("standard"),ParticleProfile.get("standard")));
        register("boilerpistol", new GunType("Boiler Pistol", 8.5f, 2.3f, 43, 1, Set.of("rifle_ammo"), "bulletcasing", ShellEjectionMode.TO_BUNDLE, 0,GunSoundProfile.get("goofyahh"),ParticleProfile.get("goofyahh")));
        register("pumpsg", new GunType("Pump Shotgun", 6.5f, 4.0f, 14, 5, Set.of("shotgun_shells"), "shotgunshellempty", ShellEjectionMode.TO_WORLD, 0.4f,GunSoundProfile.get("standard"),ParticleProfile.get("standard")));
        register("grenadelauncher", new GunType("Grenade Launcher", 15.5f, 1.0f, 14, 1, Set.of("grenade_shells"), "grenadeshellempty", ShellEjectionMode.TO_BUNDLE, 0.4f,GunSoundProfile.get("goofyahh"),ParticleProfile.get("goofyahh")));


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
        int cooldownTicks,  // cooldown between shots in ticks
        int maxAmmo,
        Set<String> acceptedAmmoTags,  // changed to tags
        String ejectShellItemId,
        ShellEjectionMode shellEjectionMode,
        float spatialRecoil,
        GunSoundProfile.GSP soundprofile,
        ParticleProfile.PP particleprofile
    ) {
        // check if this gun accepts a bullet with specific tags
        public boolean acceptsBullet(BulletItem bulletItem) {
            Bullets.BulletType bulletType = bulletItem.getBulletType();
            // check if bullet has any of the accepted tags
            for (String acceptedTag : acceptedAmmoTags) {
                if (bulletType.hasTag(acceptedTag)) {
                    return true;
                }
            }
            return false;
        }
        
        // check if this gun ejects shells
        public boolean ejectsShells() {
            return shellEjectionMode != ShellEjectionMode.NONE && ejectShellItemId != null;
        }
    }
}