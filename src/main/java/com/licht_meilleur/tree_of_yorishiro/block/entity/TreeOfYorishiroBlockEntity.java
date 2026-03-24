package com.licht_meilleur.tree_of_yorishiro.block.entity;

import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.inventory.SimpleInventory;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TreeOfYorishiroBlockEntity extends BlockEntity implements GeoBlockEntity, ExtendedScreenHandlerFactory {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final List<TreeChibishiroData> chibis = new ArrayList<>();
    private boolean initialized = false;

    private final SimpleInventory trainingInventory = new SimpleInventory(4);
    private int summonCheckCooldown = 0;
    private java.util.UUID treeId = java.util.UUID.randomUUID();

    public java.util.UUID getTreeId() {
        return treeId;
    }

    public TreeOfYorishiroBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TREE_OF_YORISHIRO, pos, state);
    }
    public SimpleInventory getTrainingInventory() {
        return trainingInventory;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void initDefaultChibisIfNeeded() {
        if (initialized) return;
        if (!chibis.isEmpty()) {
            initialized = true;
            markDirty();
            return;
        }

        chibis.clear();
        chibis.add(new TreeChibishiroData(ChibishiroColor.WHITE));
        chibis.add(new TreeChibishiroData(ChibishiroColor.RED));
        chibis.add(new TreeChibishiroData(ChibishiroColor.BLUE));
        chibis.add(new TreeChibishiroData(ChibishiroColor.YELLOW));
        chibis.add(new TreeChibishiroData(ChibishiroColor.PURPLE));

        initialized = true;
        markDirty();
    }

    public List<TreeChibishiroData> getChibis() {
        return chibis;
    }

    public TreeChibishiroData getChibi(int index) {
        if (index < 0 || index >= chibis.size()) return null;
        return chibis.get(index);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        initialized = nbt.getBoolean("Initialized");
        chibis.clear();
        if (nbt.containsUuid("TreeId")) {
            treeId = nbt.getUuid("TreeId");
        }


        NbtList list = nbt.getList("Chibis", 10);
        for (int i = 0; i < list.size(); i++) {
            chibis.add(TreeChibishiroData.fromNbt(list.getCompound(i)));
        }
        for (int i = 0; i < trainingInventory.size(); i++) {
            if (nbt.contains("TrainSlot" + i)) {
                trainingInventory.setStack(i, net.minecraft.item.ItemStack.fromNbt(nbt.getCompound("TrainSlot" + i)));
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putBoolean("Initialized", initialized);

        if (treeId != null) {
            nbt.putUuid("TreeId", treeId);
        }
        NbtList list = new NbtList();
        for (TreeChibishiroData data : chibis) {
            NbtCompound entry = new NbtCompound();
            data.writeNbt(entry);
            list.add(entry);
        }
        nbt.put("Chibis", list);

        for (int i = 0; i < trainingInventory.size(); i++) {
            NbtCompound stackNbt = new NbtCompound();
            trainingInventory.getStack(i).writeNbt(stackNbt);
            nbt.put("TrainSlot" + i, stackNbt);
        }
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("screen.tree_of_yorishiro.title");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        initDefaultChibisIfNeeded();
        return new com.licht_meilleur.tree_of_yorishiro.screen.TreeOfYorishiroScreenHandler(syncId, playerInventory, this.pos);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }
    public static void tick(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos,
                            net.minecraft.block.BlockState state, TreeOfYorishiroBlockEntity be) {
        if (world.isClient) return;

        be.initDefaultChibisIfNeeded();

        if (be.summonCheckCooldown > 0) {
            be.summonCheckCooldown--;
            return;
        }
        be.summonCheckCooldown = 40; // 2秒ごと

        be.ensureChibishiros();
    }

    private void ensureChibishiros() {
        if (!(this.world instanceof net.minecraft.server.world.ServerWorld sw)) return;

        for (TreeChibishiroData data : this.chibis) {
            com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity found = null;

            if (data.getEntityUuid() != null) {
                net.minecraft.entity.Entity e = sw.getEntity(data.getEntityUuid());
                if (e instanceof com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity c && c.isAlive()) {
                    found = c;
                }
            }

            if (found == null) {
                spawnOneChibi(sw, data);
            }
        }

        markDirty();
    }
    private void spawnOneChibi(net.minecraft.server.world.ServerWorld sw, TreeChibishiroData data) {
        com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity chibi =
                new com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity(
                        com.licht_meilleur.tree_of_yorishiro.registry.ModEntities.CHIBISHIRO,
                        sw
                );

        chibi.setColor(data.getColor());
        chibi.setHomeTreePos(this.pos);
        chibi.refreshPositionAndAngles(
                this.pos.getX() + 0.5 + (sw.random.nextDouble() - 0.5) * 2.0,
                this.pos.getY() + 1.0,
                this.pos.getZ() + 0.5 + (sw.random.nextDouble() - 0.5) * 2.0,
                0f,
                0f
        );

        sw.spawnEntity(chibi);
        data.setEntityUuid(chibi.getUuid());
    }



}