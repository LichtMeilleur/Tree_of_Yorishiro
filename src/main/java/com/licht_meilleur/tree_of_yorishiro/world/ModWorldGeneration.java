package com.licht_meilleur.tree_of_yorishiro.world;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

public class ModWorldGeneration {

    public static void generateWorldGen() {
        BiomeModifications.addFeature(
                BiomeSelectors.all(),
                GenerationStep.Feature.TOP_LAYER_MODIFICATION,
                RegistryKey.of(
                        RegistryKeys.PLACED_FEATURE,
                        new Identifier(TreeofYorishiroMod.MOD_ID, "yorishiro_stone_placed")
                )
        );
    }
}