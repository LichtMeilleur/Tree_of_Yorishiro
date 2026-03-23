package com.licht_meilleur.tree_of_yorishiro;

import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import com.licht_meilleur.tree_of_yorishiro.registry.ModEntities;
import com.licht_meilleur.tree_of_yorishiro.registry.ModItemGroups;
import com.licht_meilleur.tree_of_yorishiro.registry.ModItems;
import com.licht_meilleur.tree_of_yorishiro.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeofYorishiroMod implements ModInitializer {

    public static final String MOD_ID = "tree_of_yorishiro";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("[TreeOfYorishiro] onInitialize start");

        ModBlocks.register();
        ModItems.register();
        ModBlockEntities.register();
        ModEntities.register();
        ModScreenHandlers.register();
        ModItemGroups.register();

        LOGGER.info("[TreeOfYorishiro] onInitialize done");
    }
}