package org.goober.linkmod.miscstuff.soundprofiles;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.goober.linkmod.soundstuff.LmodSoundRegistry;

import java.util.HashMap;
import java.util.Map;

public class GunSoundProfile {
    private static final Map<String, GunSoundProfile.GSP> GUN_SOUNDPROFILE = new HashMap<>();


    // define bullet types with tags
    static {
        register("standard", new GunSoundProfile.GSP( SoundEvents.BLOCK_DISPENSER_FAIL, SoundEvents.ITEM_BUNDLE_INSERT, SoundEvents.BLOCK_BELL_USE));
        register("goofyahh", new GunSoundProfile.GSP( SoundEvents.BLOCK_LANTERN_BREAK, SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH));
        register("revolver", new GunSoundProfile.GSP(LmodSoundRegistry.REVOLVERCOCKING1, LmodSoundRegistry.GUNLOAD2, LmodSoundRegistry.EJECT1 ));
        register("dbshotgun", new GunSoundProfile.GSP(LmodSoundRegistry.REVOLVERCOCKING1, LmodSoundRegistry.GUNLOAD2, LmodSoundRegistry.SHOTGUNEJECT ));
        register("pumpshotgun", new GunSoundProfile.GSP(LmodSoundRegistry.SHOTGUNPUMP, LmodSoundRegistry.GUNLOAD1, LmodSoundRegistry.SHOTGUNEJECT ));
        register("rifle", new GunSoundProfile.GSP(LmodSoundRegistry.BOLTACTION, LmodSoundRegistry.GUNLOAD2, LmodSoundRegistry.EJECT1 ));
        register("autopistol", new GunSoundProfile.GSP(LmodSoundRegistry.COCKING1, LmodSoundRegistry.GUNLOAD2, LmodSoundRegistry.EJECT1 ));


    }

    public static void register(String id, GunSoundProfile.GSP gSP) {
        GUN_SOUNDPROFILE.put(id, gSP);
    }

    public static GunSoundProfile.GSP get(String id) {
        return GUN_SOUNDPROFILE.getOrDefault(id, GUN_SOUNDPROFILE.get("standard"));
    }

    public static Map<String, GunSoundProfile.GSP> getAll() {
        return new HashMap<>(GUN_SOUNDPROFILE);
    }

    public record GSP(
            SoundEvent primesound,
            SoundEvent loadsound,
            SoundEvent unloadsound
    ) {
    }
}
