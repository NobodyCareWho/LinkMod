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
        // GunType(name, damage, velocity, cooldown, maxAmmo, baseInaccuracy, bloomMax, bloomLength(unused), bloomSharpness(per shot), bloomDecayRate, ammoTags, shellMode, recoil, sound)
        register("rifle", new GunType("Rifle", 14.0f, 10.0f, 20, 8, 0, 0, 0, 0, 0,2.0f, Set.of("rifle_ammo"),  ShellEjectionMode.TO_WORLD, 0.2f,GunSoundProfile.get("rifle")));
        register("shotgun", new GunType("D.B. Shotgun", 7.5f, 2.5f, 2, 2, 7f, 4f, 1f, 3f, 1.8f,2.0f, Set.of("shotgun_shells"), ShellEjectionMode.TO_BUNDLE, 0.6f,GunSoundProfile.get("dbshotgun")));
        register("revolver", new GunType("Revolver", 9.2f, 8.0f, 3, 6, 0, 9f, 5, 1.2f, 1.4f, 2.0f, Set.of("rifle_ammo"), ShellEjectionMode.TO_BUNDLE, 0,GunSoundProfile.get("revolver")));
        register("ejectorpistol", new GunType("Ejector Pistol", 7.5f, 5.6f, 8, 8, 1.4f, 6, 5, 0.7f,2f, 2.4f, Set.of("rifle_ammo"), ShellEjectionMode.TO_WORLD, 0,GunSoundProfile.get("autopistol")));
        register("boilerpistol", new GunType("Boiler Pistol", 8.5f, 2.3f, 43, 1, 4, 0, 0, 0, 0,2.0f, Set.of("rifle_ammo"),  ShellEjectionMode.TO_BUNDLE, 0,GunSoundProfile.get("revolver")));
        register("pumpsg", new GunType("Pump Shotgun", 6.5f, 4.0f, 14, 5, 5.5f, 3f, 7f, 0.5f, 2f, 2.2f, Set.of("shotgun_shells"),  ShellEjectionMode.TO_WORLD, 0.4f,GunSoundProfile.get("pumpshotgun")));
        register("grenadelauncher", new GunType("Grenade Launcher", 15.5f, 2.0f, 14, 1, 1, 0, 0, 0, 0, 2.0f, Set.of("grenade_shells"), ShellEjectionMode.TO_BUNDLE, 0.4f,GunSoundProfile.get("dbshotgun")));
        register("autorifle", new GunType("Auto Rifle", 12.3f, 5.8f, 4, 30, 1.2f, 13, 12, 0.7f, 1.4f, 2.4f, Set.of("rifle_ammo"), ShellEjectionMode.TO_WORLD, 0.14f,GunSoundProfile.get("rifle")));
        register("bullseye_tome", new GunType("Bull's Eye", 20.0f, 15.0f, 22, 1, 0, 0, 0, 0, 0,2.0f, Set.of("rifle_ammo"),  ShellEjectionMode.NONE, 0,GunSoundProfile.get("rifle")));


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

        float baseInaccuracy, // currently unused
        float bloomMax,
        float bloomLength, // currently unused
        float bloomSharpness,
        float bloomIncreaseRate,
        float bloomDecayRate,

        Set<String> acceptedAmmoTags,  // changed to tags
        ShellEjectionMode shellEjectionMode,
        float spatialRecoil,
        GunSoundProfile.GSP soundprofile
    ) {
        // check if this gun accepts a bullet with specific tags
        public boolean acceptsBullet(BulletItem bulletItem) {
            // special case for grenade launcher - accept grenades and thumpershell
            if (acceptedAmmoTags.contains("grenade_shells")) {
                String typeId = bulletItem.getBulletTypeId();
                // accept if it's a grenade OR thumpershell
                if (Grenades.get(typeId) != null || typeId.equals("thumpershell")) {
                    return true;
                }
                return false; // reject all other bullets/shells
            }
            
            // check regular bullets for non-grenade-launcher guns
            Bullets.BulletType bulletType = Bullets.get(bulletItem.getBulletTypeId());
            if (bulletType != null) {
                // check if bullet has any of the accepted tags
                for (String acceptedTag : acceptedAmmoTags) {
                    if (bulletType.hasTag(acceptedTag)) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        // check if this gun ejects shells
        public boolean ejectsShells() {
            return shellEjectionMode != ShellEjectionMode.NONE;
        }
    }
}