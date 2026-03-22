package com.licht_meilleur.tree_of_yorishiro.registry;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {

    public static final Item RAINBOW_SEED = register("rainbow_seed",
            new AliasedBlockItem(ModBlocks.BUD_OF_YORISHIRO, new FabricItemSettings()));

    public static final Item TREE_OF_YORISHIRO_ITEM = register("tree_of_yorishiro",
            new AliasedBlockItem(ModBlocks.TREE_OF_YORISHIRO, new FabricItemSettings()));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, TreeofYorishiroMod.id(name), item);
    }

    public static void register() {
        TreeofYorishiroMod.LOGGER.info("[TreeOfYorishiro] Registering items");
    }
}