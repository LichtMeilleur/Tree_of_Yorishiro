package com.licht_meilleur.tree_of_yorishiro.registry;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;

public class ModItemGroups {

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
            entries.add(ModItems.RAINBOW_SEED);
            entries.add(ModItems.TREE_OF_YORISHIRO_ITEM);
        });

        TreeofYorishiroMod.LOGGER.info("[TreeOfYorishiro] Registering item groups");
    }
}