package com.licht_meilleur.tree_of_yorishiro.block;

import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BudOfYorishiroBlock extends Block {

    private static final VoxelShape SHAPE = Block.createCuboidShape(
            4.0, 0.0, 4.0,
            12.0, 10.0, 12.0
    );

    public BudOfYorishiroBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // 仮：低確率で木に成長
        if (random.nextInt(20) == 0) {
            world.setBlockState(pos, ModBlocks.TREE_OF_YORISHIRO.getDefaultState());
        }
    }
}