package com.licht_meilleur.tree_of_yorishiro.entity;

import com.licht_meilleur.tree_of_yorishiro.block.entity.SyokuninDeskBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class YorisyokuninEntity extends PathAwareEntity implements GeoEntity {

    public static final String ANIM_IDLE = "animation.model.idle";
    public static final String ANIM_WORK = "animation.model.work";
    public static final String ANIM_SLEEP = "animation.model.sleep";
    public static final String ANIM_SLEEP_END = "animation.model.sleep_end";

    private static final TrackedData<Integer> ANIM_STATE =
            DataTracker.registerData(YorisyokuninEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final TrackedData<ItemStack> HELD_WORK_ITEM =
            DataTracker.registerData(YorisyokuninEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private BlockPos deskPos;

    public YorisyokuninEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    public enum AnimState {
        IDLE,
        WORK
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANIM_STATE, AnimState.IDLE.ordinal());
        this.dataTracker.startTracking(HELD_WORK_ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0);
    }

    public void setDeskPos(BlockPos pos) {
        this.deskPos = pos;
    }

    public BlockPos getDeskPosValue() {
        return deskPos;
    }

    public void startWork() {
        this.dataTracker.set(ANIM_STATE, AnimState.WORK.ordinal());
    }

    public void setIdle() {
        this.dataTracker.set(ANIM_STATE, AnimState.IDLE.ordinal());
    }

    public void setHeldWorkItem(ItemStack stack) {
        ItemStack copy = stack == null ? ItemStack.EMPTY : stack.copy();
        if (!copy.isEmpty()) copy.setCount(1);
        this.dataTracker.set(HELD_WORK_ITEM, copy);
    }

    public ItemStack getHeldWorkItem() {
        return this.dataTracker.get(HELD_WORK_ITEM);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient && deskPos != null) {
            double x = deskPos.getX() + 0.5;
            double y = deskPos.getY() + 1.0;
            double z = deskPos.getZ() + 0.5;

            if (this.squaredDistanceTo(x, y, z) > 1.0) {
                this.refreshPositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("AnimState", this.dataTracker.get(ANIM_STATE));

        if (deskPos != null) {
            nbt.putInt("DeskX", deskPos.getX());
            nbt.putInt("DeskY", deskPos.getY());
            nbt.putInt("DeskZ", deskPos.getZ());
        }

        if (!getHeldWorkItem().isEmpty()) {
            nbt.put("HeldWorkItem", getHeldWorkItem().writeNbt(new NbtCompound()));
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(ANIM_STATE, nbt.getInt("AnimState"));

        if (nbt.contains("DeskX")) {
            deskPos = new BlockPos(nbt.getInt("DeskX"), nbt.getInt("DeskY"), nbt.getInt("DeskZ"));
        }

        if (nbt.contains("HeldWorkItem")) {
            setHeldWorkItem(ItemStack.fromNbt(nbt.getCompound("HeldWorkItem")));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            int anim = this.dataTracker.get(ANIM_STATE);

            if (anim == AnimState.WORK.ordinal()) {
                state.setAndContinue(RawAnimation.begin().thenLoop(ANIM_WORK));
            } else {
                state.setAndContinue(RawAnimation.begin().thenLoop(ANIM_IDLE));
            }

            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean damage(net.minecraft.entity.damage.DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getWorld().isClient) {
            return ActionResult.SUCCESS;
        }

        if (this.deskPos == null) {
            return ActionResult.PASS;
        }

        if (this.getWorld().getBlockEntity(this.deskPos) instanceof SyokuninDeskBlockEntity be) {
            player.openHandledScreen(be);
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }
}