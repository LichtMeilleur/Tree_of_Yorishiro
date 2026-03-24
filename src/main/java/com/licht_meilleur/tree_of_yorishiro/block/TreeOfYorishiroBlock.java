package com.licht_meilleur.tree_of_yorishiro.block;

import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import com.licht_meilleur.tree_of_yorishiro.registry.ModEntities;
import com.licht_meilleur.tree_of_yorishiro.screen.TreeOfYorishiroScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.minecraft.block.entity.BlockEntityTicker;



public class TreeOfYorishiroBlock extends Block implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = Block.createCuboidShape(
            2.0, 0.0, 2.0,
            14.0, 16.0, 14.0
    );

    public TreeOfYorishiroBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(net.minecraft.item.ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TreeOfYorishiroBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable net.minecraft.entity.LivingEntity placer, net.minecraft.item.ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!(world instanceof ServerWorld sw)) return;
        if (!(sw.getBlockEntity(pos) instanceof TreeOfYorishiroBlockEntity be)) return;

        if (!be.isInitialized()) {
            be.initDefaultChibisIfNeeded();
        }

        if (!hasNearbyChibishiro(sw, pos)) {
            spawnFiveChibis(sw, pos);
        }
    }

    private boolean hasNearbyChibishiro(ServerWorld world, BlockPos pos) {
        Box box = new Box(pos).expand(6.0);
        return !world.getEntitiesByClass(ChibishiroEntity.class, box, e -> e.isAlive()).isEmpty();
    }

    private void spawnFiveChibis(ServerWorld world, BlockPos pos) {
        ChibishiroColor[] colors = new ChibishiroColor[] {
                ChibishiroColor.RED,
                ChibishiroColor.BLUE,
                ChibishiroColor.YELLOW,
                ChibishiroColor.PURPLE,
                ChibishiroColor.WHITE
        };

        double[][] offsets = new double[][] {
                { 1.5,  0.5},
                {-1.5,  0.5},
                { 0.5,  1.5},
                { 0.5, -1.5},
                {-1.2, -1.2}
        };

        for (int i = 0; i < colors.length; i++) {
            ChibishiroEntity entity = new ChibishiroEntity(ModEntities.CHIBISHIRO, world);
            entity.setColor(colors[i]);
            entity.refreshPositionAndAngles(
                    pos.getX() + 0.5 + offsets[i][0],
                    pos.getY() + 1.0,
                    pos.getZ() + 0.5 + offsets[i][1],
                    world.random.nextFloat() * 360.0f,
                    0.0f
            );
            world.spawnEntity(entity);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if (world.getBlockEntity(pos) instanceof com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity be) {
                player.openHandledScreen(be);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world,
            BlockState state,
            BlockEntityType<T> type) {

        return world.isClient ? null : (world1, pos, state1, be) -> {
            if (be instanceof TreeOfYorishiroBlockEntity yorishiro) {
                TreeOfYorishiroBlockEntity.tick(world1, pos, state1, yorishiro);
            }
        };
    }
}