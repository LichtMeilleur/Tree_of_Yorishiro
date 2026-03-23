package com.licht_meilleur.tree_of_yorishiro.screen;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {

    public static final ScreenHandlerType<TreeOfYorishiroScreenHandler> TREE_OF_YORISHIRO =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    TreeofYorishiroMod.id("tree_of_yorishiro"),
                    new ExtendedScreenHandlerType<>(TreeOfYorishiroScreenHandler::new)
            );

    public static void register() {
        TreeofYorishiroMod.LOGGER.info("[TreeOfYorishiro] Registering screen handlers");
    }
}