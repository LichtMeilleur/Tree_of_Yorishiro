package com.licht_meilleur.tree_of_yorishiro.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ChibishiroEntity extends PathAwareEntity implements GeoEntity {

    private static final TrackedData<Integer> COLOR =
            DataTracker.registerData(ChibishiroEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final String ANIM_IDLE   = "animation.model.idle";
    public static final String ANIM_WALK    = "animation.model.walk";
    //待機中に行う暇つぶしアニメ
    public static final String ANIM_PLAY   = "animation.model.play";
    public static final String ANIM_PLAY2 = "animation.model.play2";
    public static final String ANIM_PLAY3  = "animation.model.play3";
    public static final String ANIM_PLAY4   = "animation.model.play4";
    public static final String ANIM_PLAY5  = "animation.model.play5";
    //うんどうアニメ
    public static final String ANIM_TRAINING1START   = "animation.model.training1start";
    public static final String ANIM_TRAINING1    = "animation.model.training1";
    public static final String ANIM_TRAINING2START   = "animation.model.training2start";
    public static final String ANIM_TRAINING2   = "animation.model.training2";
    public static final String ANIM_TRAINING3START   = "animation.model.training3start";
    public static final String ANIM_TRAINING3   = "animation.model.training3";
    //しょくじアニメ
    public static final String ANIM_MEAL_START   = "animation.model.meal_start";
    public static final String ANIM_MEALING    = "animation.model.mealing";
    //べんきょうアニメ
    public static final String ANIM_STUDY1START   = "animation.model.study1start";
    public static final String ANIM_STUDY1    = "animation.model.study1";
    public static final String ANIM_STUDY2START   = "animation.model.study2start";
    public static final String ANIM_STUDY2   = "animation.model.study2";
    public static final String ANIM_STUDY3START   = "animation.model.study3start";
    public static final String ANIM_STUDY3   = "animation.model.study3";
    //ねむるアニメ
    public static final String ANIM_SLEEP_START   = "animation.model.sleep_start";
    public static final String ANIM_SLEEP    = "animation.model.sleep";
   //あそびアニメ
   public static final String ANIM_GAME1START   = "animation.model.game1start";
   public static final String ANIM_GAME1    = "animation.model.game1";
   public static final String ANIM_GAME2START   = "animation.model.game2start";
   public static final String ANIM_GAME2   = "animation.model.game2";
   public static final String ANIM_GAME3START   = "animation.model.game3start";
   public static final String ANIM_GAME3   = "animation.model.game3";
   //ぼうけん開始アニメ
   public static final String ANIM_TREASURE_START   = "animation.model.treasure_start";
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ChibishiroEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(COLOR, 0);
    }

    public void setColor(ChibishiroColor color) {
        this.dataTracker.set(COLOR, color.ordinal());
    }

    public ChibishiroColor getColor() {
        return ChibishiroColor.byIndex(this.dataTracker.get(COLOR));
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.20);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Color", this.dataTracker.get(COLOR));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(COLOR, nbt.getInt("Color"));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            boolean moving = state.isMoving();
            state.setAndContinue(moving
                    ? RawAnimation.begin().thenLoop("animation.chibishiro.walk")
                    : RawAnimation.begin().thenLoop("animation.chibishiro.idle"));
            return PlayState.CONTINUE;
        }));
    }
}