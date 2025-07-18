package org.goober.linkmod.gunstuff.items;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Items;
import org.goober.linkmod.entitystuff.LmodEntityRegistry;
import org.goober.linkmod.miscstuff.ParticleProfile;
import org.goober.linkmod.miscstuff.soundprofiles.BulletSoundProfile;
import org.goober.linkmod.projectilestuff.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Bullets {
    private static final Map<String, BulletType> BULLET_TYPES = new HashMap<>();


    // define bullet types with tags
    static {
        // standard bullet projectile factory
        ProjectileFactory bulletFactory = BulletEntity::new;
        ProjectileFactory sparkbulletFactory = SparkBulletEntity::new;
        ProjectileFactory icebulletFactory = FreezeBulletEntity::new;
        ProjectileFactory hpbulletFactory = HPBulletEntity::new;
        ProjectileFactory silverbulletFactory = SilverBulletEntity::new;
        ProjectileFactory gyrojetbulletFactory = GyrojetBulletEntity::new;
        // grenade projectile factory
        ProjectileFactory grenadeFactory = PillGrenadeEntity::new;
        
        register("standard", new BulletType("Standard Bullet",false ,  1.0f, 0xFF888888, Set.of("rifle_ammo"), 1, 1, 1, 1,0,1,"bulletcasing", BulletSoundProfile.get("standard"),ParticleProfile.get("standard"), bulletFactory));
        register("silver_bullet", new BulletType("Silver Bullet",false ,  1.2f, 0xFF888888, Set.of("rifle_ammo"), 1, 1, 1, 1,0,1,"bulletcasing", BulletSoundProfile.get("standard"),ParticleProfile.get("sparkle"), silverbulletFactory));
        register("copper_bullet", new BulletType("Copper Bullet",false ,  0.8f, 0xFF888888, Set.of("rifle_ammo"), 1, 1, 1, 1,0,1,"bulletcasing", BulletSoundProfile.get("standard"),ParticleProfile.get("standard"), bulletFactory));
        register("diamond_bullet", new BulletType("Diamond Bullet",false ,  3.0f, 0xFF888888, Set.of("rifle_ammo"), 1, 1, 1, 1,0,1,"bulletcasing", BulletSoundProfile.get("standard"),ParticleProfile.get("sparkle"), bulletFactory));
        register("hollowpointbullet", new BulletType("Hollow Point Bullet",false ,  3.0f, 0xFF888888, Set.of("rifle_ammo"), 1, 1, 1, 1,0,1,"bulletcasing", BulletSoundProfile.get("standard"),ParticleProfile.get("standard"), hpbulletFactory));
        register("ratshot_bullet", new BulletType("Ratshot",false ,  0.15f, 0xFF888888, Set.of("rifle_ammo"), 7, 1.2f, 0.7f, 1.2f,5.2f,1,"bulletcasing", BulletSoundProfile.get("standard"),ParticleProfile.get("standard"), bulletFactory));
        register("subsonic_bullet", new BulletType("Subsonic Round",true ,  0.9f, 0xFF888888, Set.of("rifle_ammo"), 1, 0.6f, 0.7f, 1.1f,0f,0.5f,"bulletcasing", BulletSoundProfile.get("gyrojet"),ParticleProfile.get("standard"), bulletFactory));
        register("gyrojetbullet", new BulletType("Gyrojet Bullet",false ,  0.5f, 0xFF888888, Set.of("rifle_ammo"), 1, 1, 0.1f, 0.4f, 0,1,"none", BulletSoundProfile.get("gyrojet"),ParticleProfile.get("standard"), gyrojetbulletFactory));
        register("buckshot", new BulletType("Buckshot",false ,  0.7f, 0xFF444444, Set.of("shotgun_shells"), 5, 1, 1, 1.1f, 0,1,"copper_cap", BulletSoundProfile.get("standard"),ParticleProfile.get("standard"), bulletFactory));
        register("freezeshot", new BulletType("Freezeshot",true ,  0.4f, 0xFF444444, Set.of("shotgun_shells"), 8, 1, 0.7f, 1.5f,2,1,"shotgunshellempty", BulletSoundProfile.get("standard"),ParticleProfile.get("freeze"), icebulletFactory));
        register("slug", new BulletType("Slug",false ,  3, 0xFF444444, Set.of("shotgun_shells"), 1, 0.8f, 1.7f, 0.2f,0, 1,"copper_cap", BulletSoundProfile.get("standard"),ParticleProfile.get("standard"), bulletFactory));
        register("breezeshot", new BulletType("Breezeshot",false ,  1.0f, 0xFF444444, Set.of("shotgun_shells"), 0, 3, 1, 0, 0,1,"shotgunshellempty", BulletSoundProfile.get("breeze"),ParticleProfile.get("breeze"), bulletFactory));
        register("blazeshot", new BulletType("Blazeshot",false ,  0.2f, 0xFF444444, Set.of("shotgun_shells"), 12, 0.5f, 1, 1.5f, 2,1,"copper_cap", BulletSoundProfile.get("blaze"),ParticleProfile.get("blaze"), sparkbulletFactory));
        register("pillgrenade", new BulletType("Pill Grenade",true ,  1.0f, 0xFF444444, Set.of("grenade_shells"), 1, 1f, 1, 1, 0, 1,"grenadeshellempty", BulletSoundProfile.get("grenadelaunch"),ParticleProfile.get("goofyahh"), grenadeFactory));
        register("thumpershell", new BulletType("Thumper Shell",false ,  0.6f, 0xFF444444, Set.of("grenade_shells"), 8, 3f, 1, 1, 0, 1, "grenadeshellempty", BulletSoundProfile.get("goofyahh"),ParticleProfile.get("goofyahh"), bulletFactory));

    }

    // new helper method to check if an bullet is an empty shell, used for the priority pickup
    static boolean isEmptyShell(String itemId) {
        // list of empty shell item IDs
        return itemId.equals("bulletcasing") ||
                itemId.equals("shotgunshellempty") ||
                itemId.equals("grenadeshellempty") ||
                itemId.equals("copper_cap");
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
        boolean hasGravity,
        float damageMultiplier,
        int color,
        Set<String> tags,
        int pelletsPerShot,
        float sRMultiplier, // spatial recoil multiplier
        float vMultiplier,
        float baseSpreadMultiplier,
        float baseSpreadIncrease,
        float bloomIncrMultiplier,
        String ejectShellItemId,
        BulletSoundProfile.BSP soundprofile,
        ParticleProfile.PP particleprofile,
        ProjectileFactory projectileFactory
    ) {
        // check if this bullet has a specific tag
        public boolean hasTag(String tag) {
            return tags.contains(tag);
        }
    }
}