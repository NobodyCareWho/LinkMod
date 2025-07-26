package org.goober.linkmod.soundstuff;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.goober.linkmod.Linkmod;
import org.goober.linkmod.util.DebugConfig;

public class LmodSoundRegistry {
    private LmodSoundRegistry() {
        // private empty constructor to avoid accidental instantiation
    }

    // ITEM_METAL_WHISTLE is the name of the custom sound event
    // and is called in the mod to use the custom sound
    public static final SoundEvent BOLTACTION = registerSound("bolt_action");
    public static final SoundEvent EJECT1 = registerSound("eject1");
    public static final SoundEvent COCKING1 = registerSound("cocking1");
    public static final SoundEvent COCKING2 = registerSound("cocking2");
    public static final SoundEvent REVOLVERCOCKING1 = registerSound("revolvercocking");
    public static final SoundEvent GRENADELAUNCH1 = registerSound("grenadelaunch1");
    public static final SoundEvent GRENADELAUNCH2 = registerSound("grenadelaunch2");
    public static final SoundEvent JET2HOLIDAY = registerSound("jet2");
    public static final SoundEvent SHOTGUNPUMP = registerSound("shotgunpump");
    public static final SoundEvent GUNLOAD1 = registerSound("gunload1");
    public static final SoundEvent GUNLOAD2 = registerSound("gunload2");
    public static final SoundEvent SHOTGUNEJECT = registerSound("shotguneject");

    // actual registration of all the custom SoundEvents
    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.of(Linkmod.MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    // This static method starts class initialization, which then initializes
    // the static class variables (e.g. ITEM_METAL_WHISTLE).
    public static void initialize() {
        DebugConfig.debug("Registering " + Linkmod.MOD_ID + " Sounds");
        // Technically this method can stay empty, but some developers like to notify
        // the console, that certain parts of the mod have been successfully initialized
    }
}
