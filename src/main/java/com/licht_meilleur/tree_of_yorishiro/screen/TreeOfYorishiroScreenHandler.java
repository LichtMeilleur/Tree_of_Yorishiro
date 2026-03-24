package com.licht_meilleur.tree_of_yorishiro.screen;

import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.registry.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TreeOfYorishiroScreenHandler extends ScreenHandler {

    public enum DetailPage {
        MAIN,
        MEAL,
        STUDY,
        EXERCISE,
        PLAY,
        ADVENTURE
    }

    private final BlockPos blockPos;
    private DetailPage currentPage = DetailPage.MAIN;

    // サーバー側
    public TreeOfYorishiroScreenHandler(int syncId, PlayerInventory inventory, BlockPos blockPos) {
        super(ModScreenHandlers.TREE_OF_YORISHIRO, syncId);
        this.blockPos = blockPos;

        TreeOfYorishiroBlockEntity be = getBlockEntity(inventory.player.getWorld());
        Inventory inv = be != null ? be.getTrainingInventory() : new SimpleInventory(4);

        // slot0 = 食事
        this.addSlot(new Slot(inv, 0, 110, 70) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return TreeOfYorishiroScreenHandler.this.currentPage == DetailPage.MEAL
                        && stack.isFood();
            }
        });

        // slot1 = Lv1
        this.addSlot(new Slot(inv, 1, 96, 66) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return switch (TreeOfYorishiroScreenHandler.this.currentPage) {
                    case STUDY -> stack.isOf(ModItems.STUDY_BOOK);
                    case EXERCISE -> stack.isOf(ModItems.HEADBAND);
                    case PLAY -> stack.isOf(ModItems.BALL);
                    default -> false;
                };
            }
        });

        // slot2 = Lv2
        this.addSlot(new Slot(inv, 2, 96, 96) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return switch (TreeOfYorishiroScreenHandler.this.currentPage) {
                    case STUDY -> stack.isOf(ModItems.STUDY_SET);
                    case EXERCISE -> stack.isOf(ModItems.PUNCHING_SET);
                    case PLAY -> stack.isOf(ModItems.BUBBLE_SET);
                    default -> false;
                };
            }
        });

        // slot3 = Lv3
        this.addSlot(new Slot(inv, 3, 96, 126) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return switch (TreeOfYorishiroScreenHandler.this.currentPage) {
                    case STUDY -> stack.isOf(ModItems.HARD_STUDY_SET);
                    case EXERCISE -> stack.isOf(ModItems.RUNNING_SET);
                    case PLAY -> stack.isOf(ModItems.GAME);
                    default -> false;
                };
            }
        });

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

    // クライアント側
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

    public DetailPage getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(DetailPage page) {
        this.currentPage = page;
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