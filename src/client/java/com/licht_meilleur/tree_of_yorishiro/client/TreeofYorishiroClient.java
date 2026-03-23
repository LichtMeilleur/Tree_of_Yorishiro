package com.licht_meilleur.tree_of_yorishiro.client;

import com.licht_meilleur.tree_of_yorishiro.client.block.TreeOfYorishiroRenderer;
import com.licht_meilleur.tree_of_yorishiro.client.entity.ChibishiroRenderer;
import com.licht_meilleur.tree_of_yorishiro.client.screen.TreeOfYorishiroScreen;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import com.licht_meilleur.tree_of_yorishiro.registry.ModEntities;
import com.licht_meilleur.tree_of_yorishiro.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class TreeofYorishiroClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.TREE_OF_YORISHIRO, ctx -> new TreeOfYorishiroRenderer());
        EntityRendererRegistry.register(ModEntities.CHIBISHIRO, ChibishiroRenderer::new);
        HandledScreens.register(ModScreenHandlers.TREE_OF_YORISHIRO, TreeOfYorishiroScreen::new);
    }
}