package org.goober.linkmod;

import net.fabricmc.api.ModInitializer;
import org.goober.linkmod.itemstuff.LmodItemRegistry;
import org.goober.linkmod.itemstuff.LmodDataComponentTypes;
import org.goober.linkmod.entitystuff.LmodEntityRegistry;

public class Linkmod implements ModInitializer {
    public static final String MOD_ID = "lmod";
    @Override
    public void onInitialize() {
        LmodDataComponentTypes.initialize();
        LmodItemRegistry.initialize();
        LmodEntityRegistry.initialize();
    }

}
