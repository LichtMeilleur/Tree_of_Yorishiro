package com.licht_meilleur.tree_of_yorishiro.block.entity;

import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroAnimState;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import com.licht_meilleur.tree_of_yorishiro.registry.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
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
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;



import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;


import java.util.ArrayList;
import java.util.List;

public class TreeOfYorishiroBlockEntity extends BlockEntity implements GeoBlockEntity, ExtendedScreenHandlerFactory {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final List<TreeChibishiroData> chibis = new ArrayList<>();
    private boolean initialized = false;

    private final SimpleInventory trainingInventory = new SimpleInventory(4);
    private int summonCheckCooldown = 0;
    private java.util.UUID treeId = java.util.UUID.randomUUID();

    private ChibishiroColor selectedColor = ChibishiroColor.WHITE;

    private static final RawAnimation TREE_LOOP = RawAnimation.begin().thenLoop("animation.model.loop");
    private static final RawAnimation TREE_GROW = RawAnimation.begin().thenPlay("animation.model.grow");

    private boolean growing = false;
    private int growTicks = 0;
    private static final int GROW_ANIM_TICKS = 30;


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

    public static void tick(World world, BlockPos pos, BlockState state, TreeOfYorishiroBlockEntity be) {
        if (world.isClient) return;

        be.initDefaultChibisIfNeeded();
        tickGrowAnimation(be);
        tickTraining(be);

        if (be.summonCheckCooldown > 0) {
            be.summonCheckCooldown--;
            return;
        }

        be.summonCheckCooldown = 40;
        be.ensureChibishiros();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "tree_controller", 0, state -> {
            if (this.growing) {
                state.setAndContinue(TREE_GROW);
                return PlayState.CONTINUE;
            }

            state.setAndContinue(TREE_LOOP);
            return PlayState.CONTINUE;
        }));
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

        String selectedColorId = nbt.getString("SelectedColor");
        this.selectedColor = switch (selectedColorId) {
            case "blue" -> ChibishiroColor.BLUE;
            case "yellow" -> ChibishiroColor.YELLOW;
            case "purple" -> ChibishiroColor.PURPLE;
            case "red" -> ChibishiroColor.RED;
            default -> ChibishiroColor.WHITE;
        };
        this.growing = nbt.getBoolean("Growing");
        this.growTicks = nbt.getInt("GrowTicks");
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
        nbt.putBoolean("Growing", this.growing);
        nbt.putInt("GrowTicks", this.growTicks);

        nbt.putString("SelectedColor", this.selectedColor.getId());
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
    private void spawnOneChibi(ServerWorld sw, TreeChibishiroData data) {
        ChibishiroEntity chibi = new ChibishiroEntity(ModEntities.CHIBISHIRO, sw);

        chibi.setColor(data.getColor());
        chibi.setHomeTreePos(this.pos);
        chibi.setHomeTreeUuid(this.treeId);

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
    public void discardAllChildren() {
        if (!(this.world instanceof ServerWorld sw)) return;

        for (TreeChibishiroData data : this.chibis) {
            if (data.getEntityUuid() == null) continue;

            Entity e = sw.getEntity(data.getEntityUuid());
            if (e instanceof ChibishiroEntity chibi) {
                chibi.discard();
            }
            data.setEntityUuid(null);
        }

        markDirty();
    }
    public void startTrainingFromScreen(String pageName, int slotIndex, ItemStack consumedStack) {
        if (this.world == null || this.world.isClient()) {
            return;
        }


        TreeChibishiroData data = getCurrentChibiData();
        if (data == null) {
            return;
        }


        int level = switch (pageName) {
            case "MEAL" -> 1;
            case "STUDY", "EXERCISE", "PLAY" -> switch (slotIndex) {
                case 1 -> 1;
                case 2 -> 2;
                case 3 -> 3;
                default -> 0;
            };
            default -> 0;
        };


        data.setTraining(true);
        data.setTrainingCompleted(false);
        data.setTrainingType(pageName);
        data.setTrainingLevel(level);

        long now = this.world.getTime();
        long duration = switch (level) {
            case 1 -> 20L * 10L;
            case 2 -> 20L * 20L;
            case 3 -> 20L * 30L;
            default -> 20L * 10L;
        };
        data.setTrainingEndTick(now + duration);



        switch (pageName) {
            case "MEAL" -> data.setAnimState(ChibishiroAnimState.MEAL_TASK);
            case "STUDY" -> data.setAnimState(switch (level) {
                case 1 -> ChibishiroAnimState.STUDY1_TASK;
                case 2 -> ChibishiroAnimState.STUDY2_TASK;
                case 3 -> ChibishiroAnimState.STUDY3_TASK;
                default -> ChibishiroAnimState.IDLE;
            });
            case "EXERCISE" -> data.setAnimState(switch (level) {
                case 1 -> ChibishiroAnimState.TRAINING1_TASK;
                case 2 -> ChibishiroAnimState.TRAINING2_TASK;
                case 3 -> ChibishiroAnimState.TRAINING3_TASK;
                default -> ChibishiroAnimState.IDLE;
            });
            case "PLAY" -> data.setAnimState(switch (level) {
                case 1 -> ChibishiroAnimState.GAME1_TASK;
                case 2 -> ChibishiroAnimState.GAME2_TASK;
                case 3 -> ChibishiroAnimState.GAME3_TASK;
                default -> ChibishiroAnimState.IDLE;
            });
            default -> data.setAnimState(ChibishiroAnimState.IDLE);
        }


        if (data.getEntityUuid() != null && this.world instanceof net.minecraft.server.world.ServerWorld sw) {
            net.minecraft.entity.Entity e = sw.getEntity(data.getEntityUuid());


            if (e instanceof com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity chibi) {

                if ("MEAL".equals(pageName)) {
                    chibi.setDisplayFoodStack(consumedStack);
                }

            }
        }



        markDirty();
        this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
    }
    public TreeChibishiroData getChibiDataByColor(ChibishiroColor color) {
        for (TreeChibishiroData data : this.chibis) {
            if (data.getColor() == color) {
                return data;
            }
        }
        return null;
    }
    private TreeChibishiroData getCurrentChibiData() {
        return getChibiDataByColor(this.selectedColor);
    }


    private static void tickTraining(TreeOfYorishiroBlockEntity be) {
        if (be.world == null || be.world.isClient()) return;

        long now = be.world.getTime();
        boolean changed = false;

        for (TreeChibishiroData data : be.chibis) {
            if (!data.isTraining()) continue;
            if (data.isTrainingCompleted()) continue;

            if (now >= data.getTrainingEndTick()) {
                switch (data.getTrainingType()) {
                    case "MEAL" -> {
                        data.setGenki(Math.min(100, data.getGenki() + 10 * data.getTrainingLevel()));
                        data.setStress(Math.max(0, data.getStress() - 8 * data.getTrainingLevel()));
                    }
                    case "STUDY" -> {
                        data.setKashikosa(data.getKashikosa() + data.getTrainingLevel());
                        data.setStress(Math.min(100, data.getStress() + 3 * data.getTrainingLevel()));
                        data.setGenki(Math.max(0, data.getGenki() - 2 * data.getTrainingLevel()));
                    }
                    case "EXERCISE" -> {
                        data.setChikara(data.getChikara() + data.getTrainingLevel());
                        data.setStress(Math.min(100, data.getStress() + 4 * data.getTrainingLevel()));
                        data.setGenki(Math.max(0, data.getGenki() - 3 * data.getTrainingLevel()));
                    }
                    case "PLAY" -> {
                        data.setStress(Math.max(0, data.getStress() - 10 * data.getTrainingLevel()));
                        data.setGenki(Math.min(100, data.getGenki() + 4 * data.getTrainingLevel()));
                    }
                }

                data.setTraining(false);
                data.setTrainingCompleted(true);
                data.setAnimState(ChibishiroAnimState.IDLE);

                // ← ここでだけリセット
                data.setTrainingType("");
                data.setTrainingLevel(0);
                data.setTrainingEndTick(0L);

                if (data.getEntityUuid() != null && be.world instanceof ServerWorld sw) {
                    Entity e = sw.getEntity(data.getEntityUuid());
                    if (e instanceof ChibishiroEntity chibi) {
                        chibi.setDisplayFoodStack(ItemStack.EMPTY);
                    }
                }

                changed = true;
            }
        }

        if (changed) {
            be.markDirty();
            be.world.updateListeners(be.pos, be.getCachedState(), be.getCachedState(), 3);
        }
    }
    public ChibishiroColor getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(ChibishiroColor selectedColor) {
        this.selectedColor = selectedColor != null ? selectedColor : ChibishiroColor.WHITE;
        markDirty();

        if (this.world != null) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
        }
    }
    private ChibishiroEntity getCurrentChibiEntity() {
        if (!(this.world instanceof ServerWorld sw)) return null;

        TreeChibishiroData data = getCurrentChibiData();
        if (data == null || data.getEntityUuid() == null) return null;

        Entity e = sw.getEntity(data.getEntityUuid());
        if (e instanceof ChibishiroEntity chibi) {
            return chibi;
        }
        return null;
    }
    private static void tickGrowAnimation(TreeOfYorishiroBlockEntity be) {
        if (be.world == null || be.world.isClient()) return;

        if (be.growing) {
            be.growTicks--;

            if (be.growTicks <= 0) {
                be.growing = false;
                be.growTicks = 0;
                be.markDirty();
                be.world.updateListeners(be.pos, be.getCachedState(), be.getCachedState(), 3);
            }
        }
    }

    public void startGrowAnimation() {
        this.growing = true;
        this.growTicks = GROW_ANIM_TICKS;
        System.out.println("[TreeOfYorishiro] startGrowAnimation called");
        markDirty();

        if (this.world != null) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
        }
    }
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt);
        return nbt;
    }
    @Nullable
    public ChibishiroEntity getChibiEntityByColor(World world, ChibishiroColor color) {
        if (!(world instanceof net.minecraft.server.world.ServerWorld sw) && !world.isClient) {
            return null;
        }

        TreeChibishiroData data = getChibiDataByColor(color);
        if (data == null || data.getEntityUuid() == null) {
            return null;
        }

        if (world instanceof net.minecraft.server.world.ServerWorld sw) {
            net.minecraft.entity.Entity e = sw.getEntity(data.getEntityUuid());
            return e instanceof ChibishiroEntity chibi ? chibi : null;
        }

        // クライアント側
        for (var e : world.getEntitiesByClass(
                ChibishiroEntity.class,
                new net.minecraft.util.math.Box(this.pos).expand(32),
                entity -> entity.getUuid().equals(data.getEntityUuid())
        )) {
            return e;
        }

        return null;
    }
}