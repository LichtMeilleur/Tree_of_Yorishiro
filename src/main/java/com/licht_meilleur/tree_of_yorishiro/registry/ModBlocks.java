package com.licht_meilleur.tree_of_yorishiro.registry;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.block.BudOfYorishiroBlock;
import com.licht_meilleur.tree_of_yorishiro.block.TreeOfYorishiroBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public class ModBlocks {

    public static final Block BUD_OF_YORISHIRO = register("bud_of_yorishiro",
            new BudOfYorishiroBlock(AbstractBlock.Settings.create()
                    .noCollision()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.GRASS)
                    .nonOpaque()));

    public static final Block TREE_OF_YORISHIRO = register("tree_of_yorishiro",
            new TreeOfYorishiroBlock(AbstractBlock.Settings.create()
                    .strength(2.0f)
                    .sounds(BlockSoundGroup.WOOD)
                    .nonOpaque()));

    private static Block register(String name, Block block) {
        return Registry.register(Registries.BLOCK, TreeofYorishiroMod.id(name), block);
    }

    public static void register() {
        TreeofYorishiroMod.LOGGER.info("[TreeOfYorishiro] Registering blocks");
    }
}