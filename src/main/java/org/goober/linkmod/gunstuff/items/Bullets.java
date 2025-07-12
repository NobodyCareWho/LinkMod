package org.goober.linkmod.gunstuff.items;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.goober.linkmod.entitystuff.LmodEntityRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Bullets {
    private static final Map<String, BulletType> BULLET_TYPES = new HashMap<>();


    // define bullet types with tags
    static {
        register("standard", new BulletType("Standard Bullet", 1.0f, 0xFF888888, Set.of("rifle_ammo"), 1, 1, LmodEntityRegistry.BULLET));
        register("silver_bullet", new BulletType("Silver Bullet", 1.2f, 0xFF888888, Set.of("rifle_ammo"), 1, 1, LmodEntityRegistry.BULLET));
        register("copper_bullet", new BulletType("Copper Bullet", 0.8f, 0xFF888888, Set.of("rifle_ammo"), 1, 1, LmodEntityRegistry.BULLET));
        register("diamond_bullet", new BulletType("Diamond Bullet", 3.0f, 0xFF888888, Set.of("rifle_ammo"), 1, 1, LmodEntityRegistry.BULLET));
        register("buckshot", new BulletType("Buckshot", 0.6f, 0xFF444444, Set.of("shotgun_shells"), 5, 1, LmodEntityRegistry.BULLET));
        register("slug", new BulletType("Slug", 1.7f, 0xFF444444, Set.of("shotgun_shells"), 1, 0.8f, LmodEntityRegistry.BULLET));
        register("breezeshot", new BulletType("Breezeshot", 1.0f, 0xFF444444, Set.of("shotgun_shells"), 0, 3, LmodEntityRegistry.BULLET));
        register("blazeshot", new BulletType("Blazeshot", 0.2f, 0xFF444444, Set.of("shotgun_shells"), 12, 3, LmodEntityRegistry.SPARKBULLET));
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
        Set<String> tags,
        int pelletsPerShot,
        float sRMultiplier,
        EntityType<? extends PersistentProjectileEntity> projectileType
    ) {
        // check if this bullet has a specific tag
        public boolean hasTag(String tag) {
            return tags.contains(tag);
        }
    }
}