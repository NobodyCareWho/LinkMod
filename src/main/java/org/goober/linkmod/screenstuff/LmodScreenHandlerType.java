package org.goober.linkmod.screenstuff;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class LmodScreenHandlerType {
    public static final ScreenHandlerType<LatheScreenHandler> LATHE = register("lathe", LatheScreenHandler::new);
    public static final ScreenHandlerType<ExpChestScreenHandler> EXP_CHEST = register("exp_chest", ExpChestScreenHandler::new);

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, Identifier.of("lmod", id), new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }

    public static void initialize() {
        // Force class loading
    }
}