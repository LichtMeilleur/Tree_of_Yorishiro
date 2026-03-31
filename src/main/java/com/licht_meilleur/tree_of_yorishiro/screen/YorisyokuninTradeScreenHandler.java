package com.licht_meilleur.tree_of_yorishiro.screen;

import com.licht_meilleur.tree_of_yorishiro.block.entity.SyokuninDeskBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRecipeDef;
import com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRecipeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public class YorisyokuninTradeScreenHandler extends ScreenHandler {

    private static final int BUTTON_CRAFT = 0;
    private static final int BUTTON_RECIPE_BASE = 1000;

    private final SyokuninDeskBlockEntity be;
    private final Inventory inventory;
    private final PropertyDelegate properties;

    public YorisyokuninTradeScreenHandler(int syncId, PlayerInventory playerInventory, SyokuninDeskBlockEntity be) {
        super(ModScreenHandlers.YORISYOKUNIN_TRADE, syncId);
        this.be = be;
        this.inventory = be.getInventory();
        this.properties = new ArrayPropertyDelegate(1); // 0 = selectedRecipe

        this.addProperties(this.properties);
        addSlots(playerInventory);
    }

    public YorisyokuninTradeScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory,
                (SyokuninDeskBlockEntity) playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()));
    }

    private void addSlots(PlayerInventory playerInventory) {
        // 右上3入力
        this.addSlot(new Slot(inventory, 0, 132, 34));
        this.addSlot(new Slot(inventory, 1, 152, 34));
        this.addSlot(new Slot(inventory, 2, 172, 34));

        // プレイヤーインベントリ
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 47 + col * 18, 172 + row * 18));
            }
        }

        // ホットバー
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 47 + col * 18, 230));
        }
    }

    public List<YorisyokuninRecipeDef> getRecipes() {
        return YorisyokuninRecipeRegistry.getRecipes();
    }

    public int getRecipeCount() {
        return getRecipes().size();
    }

    public int getSelectedRecipe() {
        return properties.get(0);
    }

    public void setSelectedRecipe(int selectedRecipe) {
        int max = Math.max(0, getRecipeCount() - 1);
        properties.set(0, Math.max(0, Math.min(selectedRecipe, max)));
    }

    public YorisyokuninRecipeDef getSelectedRecipeDef() {
        List<YorisyokuninRecipeDef> recipes = getRecipes();
        if (recipes.isEmpty()) return null;

        int index = getSelectedRecipe();
        if (index < 0 || index >= recipes.size()) return null;

        return recipes.get(index);
    }

    public boolean canCraftSelectedRecipe() {
        YorisyokuninRecipeDef recipe = getSelectedRecipeDef();
        if (recipe == null) return false;

        List<ItemStack> inputs = List.of(
                be.getInventory().getStack(0),
                be.getInventory().getStack(1),
                be.getInventory().getStack(2)
        );

        return recipe.matches(inputs);
    }

    public boolean startWork() {
        YorisyokuninRecipeDef recipe = getSelectedRecipeDef();
        if (recipe == null) return false;
        if (!canCraftSelectedRecipe()) return false;

        be.tryStartWork(recipe);
        return true;
    }

    public boolean isWorking() {
        return be.isWorking();
    }

    public int getWorkTicks() {
        return be.getWorkTicks();
    }

    public SyokuninDeskBlockEntity getBlockEntity() {
        return be;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id == BUTTON_CRAFT) {
            return startWork();
        }

        if (id >= BUTTON_RECIPE_BASE) {
            int recipeIndex = id - BUTTON_RECIPE_BASE;
            setSelectedRecipe(recipeIndex);
            return true;
        }

        return super.onButtonClick(player, id);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return be != null && player.squaredDistanceTo(
                be.getPos().getX() + 0.5,
                be.getPos().getY() + 0.5,
                be.getPos().getZ() + 0.5
        ) <= 64.0;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    public static int getCraftButtonId() {
        return BUTTON_CRAFT;
    }

    public static int getRecipeButtonId(int recipeIndex) {
        return BUTTON_RECIPE_BASE + recipeIndex;
    }
}