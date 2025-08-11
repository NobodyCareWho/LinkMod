package org.goober.linkmod.gunstuff.items;

import net.minecraft.item.ItemStack;
import org.goober.linkmod.miscstuff.ParticleProfile;
import org.goober.linkmod.miscstuff.soundprofiles.BulletSoundProfile;
import org.goober.linkmod.projectilestuff.*;
import org.goober.linkmod.gunstuff.items.ProjectileFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Grenades {
    private static final Map<String, Grenades.GrenadeType> GRENADE_TYPES = new HashMap<>();


    // define grenade types
    static {
        ProjectileFactory grenadeFactory = PillGrenadeEntity::new;
        
        register("standardgrenade", new Grenades.GrenadeType("Grenade", false, false, 4f, 3, 5, 0.6f, 1.0f, "none", grenadeFactory, null));
        register("demo", new Grenades.GrenadeType("Demo Grenade", true, false, 4f, 1, 10, 0.6f, 1.3f, "none", grenadeFactory, null));
        register("he", new Grenades.GrenadeType("HE Grenade", false, false, 6f, 3, 5, 0.6f, 1.5f, "none", grenadeFactory, null));
        register("incendiary", new Grenades.GrenadeType("Incendiary Grenade", false, true, 3.5f, 3, 5, 0.6f, 1.0f, "none", grenadeFactory, null));
        register("bouncy", new Grenades.GrenadeType("Bouncy Grenade", false, false, 3f, 10, 6, 0.9f, 1.0f, "none", grenadeFactory, null));
        register("shape", new Grenades.GrenadeType("Shape Grenade", false, false, 1f, 1, 10, 0.6f, 4.5f, "none", grenadeFactory, "Deals large direct damage"));
    }

    public static void register(String id, Grenades.GrenadeType grenadeType) {
        GRENADE_TYPES.put(id, grenadeType);
    }

    public static Grenades.GrenadeType get(String id) {
        return GRENADE_TYPES.get(id);
    }

    public static Map<String, Grenades.GrenadeType> getAll() {
        return new HashMap<>(GRENADE_TYPES);
    }
    
    // get grenade type from an item stack (assumes it's a BulletItem with grenade type ID)
    public static GrenadeType getFromItemStack(ItemStack stack) {
        if (stack.getItem() instanceof BulletItem bulletItem) {
            String typeId = bulletItem.getBulletTypeId();
            
            // check if it's a grenade type ID
            if (GRENADE_TYPES.containsKey(typeId)) {
                return GRENADE_TYPES.get(typeId);
            }
        }
        return null; // return null if not a grenade
    }
    
    // check if an item is a grenade that can be fired from grenade launcher
    public static boolean isGrenade(ItemStack stack) {
        if (stack.getItem() instanceof BulletItem bulletItem) {
            String typeId = bulletItem.getBulletTypeId();
            return GRENADE_TYPES.containsKey(typeId);
        }
        return false;
    }

    public record GrenadeType(
            String displayName,
            boolean destroysTerrain,
            boolean createsFire,
            float explosionSize,
            int bounces,
            float lifetime,
            float bounciness,
            float impactDamageMultiplier,
            String ejectItemId,
            ProjectileFactory projectileFactory,
            String info
    ) {
        // get the projectile factory for this grenade type
        public ProjectileFactory getProjectileFactory() {
            return projectileFactory;
        }
    }
}
