package com.licht_meilleur.tree_of_yorishiro.client;

import com.licht_meilleur.tree_of_yorishiro.client.block.TreeOfYorishiroRenderer;
import com.licht_meilleur.tree_of_yorishiro.client.entity.ChibishiroRenderer;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import com.licht_meilleur.tree_of_yorishiro.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class TreeofYorishiroClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.TREE_OF_YORISHIRO, ctx -> new TreeOfYorishiroRenderer());
        EntityRendererRegistry.register(ModEntities.CHIBISHIRO, ChibishiroRenderer::new);
    }
}