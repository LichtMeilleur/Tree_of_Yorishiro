package com.licht_meilleur.tree_of_yorishiro.screen;

import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.screen.slot.Slot;
import net.minecraft.inventory.Inventory;

public class TreeOfYorishiroScreenHandler extends ScreenHandler {

    private final BlockPos blockPos;

    // サーバー側
    public TreeOfYorishiroScreenHandler(int syncId, PlayerInventory inventory, BlockPos blockPos) {
        super(ModScreenHandlers.TREE_OF_YORISHIRO, syncId);
        this.blockPos = blockPos;

        TreeOfYorishiroBlockEntity be = getBlockEntity(inventory.player.getWorld());
        Inventory inv = be != null ? be.getTrainingInventory() : new net.minecraft.inventory.SimpleInventory(4);

        // しょくじ用
        this.addSlot(new Slot(inv, 0, 110, 70));

        // Lv1～Lv3
        this.addSlot(new Slot(inv, 1, 96, 66));
        this.addSlot(new Slot(inv, 2, 96, 96));
        this.addSlot(new Slot(inv, 3, 96, 126));

        // プレイヤーインベントリ
        int startX = 48;
        int startY = 170;

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, startX + col * 18, startY + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inventory, col, startX + col * 18, startY + 58));
        }
    }

    // クライアント側（Extended用）
    public TreeOfYorishiroScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, buf.readBlockPos());
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public TreeOfYorishiroBlockEntity getBlockEntity(World world) {
        if (world == null) return null;
        if (!(world.getBlockEntity(blockPos) instanceof TreeOfYorishiroBlockEntity be)) return null;
        return be;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }
}