package org.goober.linkmod;

import net.fabricmc.api.ModInitializer;
import org.goober.linkmod.itemstuff.LmodItemRegistry;

public class Linkmod implements ModInitializer {
    public static final String MOD_ID = "lmod";
    @Override
    public void onInitialize() {
        LmodItemRegistry.initialize();
    }

}
