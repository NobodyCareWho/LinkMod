package org.goober.linkmod.miscstuff.soundprofiles;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.goober.linkmod.gunstuff.items.Bullets;
import org.goober.linkmod.soundstuff.LmodSoundRegistry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BulletSoundProfile {
    private static final Map<String, BulletSoundProfile.BSP> BULLET_SOUNDPROFILE = new HashMap<>();


    // define bullet types with tags
    static {
        register("standard", new BSP( SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR));
        register("goofyahh", new BSP( SoundEvents.ENCHANT_THORNS_HIT, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundEvents.BLOCK_ANVIL_LAND));
        register("gyrojet", new BSP( SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundEvents.BLOCK_ANVIL_LAND));
        register("grenadelaunch", new BSP(LmodSoundRegistry.GRENADELAUNCH2, SoundEvents.BLOCK_ANVIL_LAND, SoundEvents.BLOCK_ANVIL_LAND));
        register("breeze", new BSP( SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST.value(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundEvents.BLOCK_ANVIL_LAND));
        register("blaze", new BSP( SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundEvents.BLOCK_ANVIL_LAND));
    }

    public static void register(String id, BulletSoundProfile.BSP bSP) {
        BULLET_SOUNDPROFILE.put(id, bSP);
    }

    public static BulletSoundProfile.BSP get(String id) {
        return BULLET_SOUNDPROFILE.getOrDefault(id, BULLET_SOUNDPROFILE.get("standard"));
    }

    public static Map<String, BulletSoundProfile.BSP> getAll() {
        return new HashMap<>(BULLET_SOUNDPROFILE);
    }

    public record BSP(
            SoundEvent firesound,
            SoundEvent entityhitsound,
            SoundEvent groundhitsound
    ) {
    }
}
