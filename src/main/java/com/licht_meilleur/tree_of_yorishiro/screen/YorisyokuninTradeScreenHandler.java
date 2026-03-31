package com.licht_meilleur.tree_of_yorishiro.screen;

import com.licht_meilleur.tree_of_yorishiro.block.entity.SyokuninDeskBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRecipeDef;
import com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRecipeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public class YorisyokuninTradeScreenHandler extends ScreenHandler {

    private final SyokuninDeskBlockEntity be;
    private final Inventory inventory;
    private int selectedRecipe = 0;


    public YorisyokuninTradeScreenHandler(int syncId, PlayerInventory playerInventory, SyokuninDeskBlockEntity be) {
        super(ModScreenHandlers.YORISYOKUNIN_TRADE, syncId);
        this.be = be;
        this.inventory = be.getInventory();
        addSlots(playerInventory);
    }

    public YorisyokuninTradeScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory,
                (SyokuninDeskBlockEntity) playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()));
    }

    private void addSlots(PlayerInventory playerInventory) {
        this.addSlot(new Slot(inventory, 0, 44, 35));
        this.addSlot(new Slot(inventory, 1, 62, 35));
        this.addSlot(new Slot(inventory, 2, 80, 35));

        for (int m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }

        for (int m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    public boolean startWork() {
        var recipes = YorisyokuninRecipeRegistry.getRecipes();
        if (recipes.isEmpty()) return false;

        YorisyokuninRecipeDef recipe = recipes.get(selectedRecipe);

        List<ItemStack> inputs = List.of(
                be.getInventory().getStack(0),
                be.getInventory().getStack(1),
                be.getInventory().getStack(2)
        );

        if (!recipe.matches(inputs.stream().filter(stack -> !stack.isEmpty()).toList())) {
            return false;
        }

        be.tryStartWork(recipe.getOutput());
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
    public int getSelectedRecipe() {
        return selectedRecipe;
    }

    public void setSelectedRecipe(int selectedRecipe) {
        int max = Math.max(0, com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRecipeRegistry.getRecipes().size() - 1);
        this.selectedRecipe = Math.max(0, Math.min(selectedRecipe, max));
    }
}