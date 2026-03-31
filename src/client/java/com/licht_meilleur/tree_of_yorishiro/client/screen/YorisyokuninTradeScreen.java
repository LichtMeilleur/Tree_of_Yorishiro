package com.licht_meilleur.tree_of_yorishiro.client.screen;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRecipeDef;
import com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRecipeRegistry;
import com.licht_meilleur.tree_of_yorishiro.recipe.YorisyokuninRequirement;
import com.licht_meilleur.tree_of_yorishiro.screen.YorisyokuninTradeScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class YorisyokuninTradeScreen extends HandledScreen<YorisyokuninTradeScreenHandler> {

    private static final Identifier BG = TreeofYorishiroMod.id("textures/gui/yorisyokunin_menu.png");
    private static final Identifier SLOT = TreeofYorishiroMod.id("textures/gui/yorisyokunin_slot.png");

    private int tickCounter = 0;

    public YorisyokuninTradeScreen(YorisyokuninTradeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        addDrawableChild(ButtonWidget.builder(Text.literal("つくる"), button -> {
            this.handler.startWork();
        }).dimensions(this.x + 118, this.y + 33, 40, 20).build());
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        tickCounter++;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(BG, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);

        // 必要ならスロット装飾
        context.drawTexture(SLOT, this.x + 43, this.y + 34, 0, 0, 18, 18, 16, 16);
        context.drawTexture(SLOT, this.x + 61, this.y + 34, 0, 0, 18, 18, 16, 16);
        context.drawTexture(SLOT, this.x + 79, this.y + 34, 0, 0, 18, 18, 16, 16);
        context.drawTexture(SLOT, this.x + 118, this.y + 34, 0, 0, 18, 18, 16, 16);

        drawGhostRecipe(context);
        drawProgress(context);
    }

    private void drawGhostRecipe(DrawContext context) {
        List<YorisyokuninRecipeDef> recipes = YorisyokuninRecipeRegistry.getRecipes();
        if (recipes.isEmpty()) return;

        YorisyokuninRecipeDef recipe = recipes.get((tickCounter / 40) % recipes.size());

        int[] slotXs = {44, 62, 80};
        int slotY = 35;

        List<YorisyokuninRequirement> inputs = recipe.getInputs();

        for (int i = 0; i < inputs.size() && i < 3; i++) {
            ItemStack current = handler.getBlockEntity().getInventory().getStack(i);
            if (current.isEmpty()) {
                ItemStack ghost = inputs.get(i).getRotatingDisplayStack(tickCounter);
                if (!ghost.isEmpty()) {
                    context.drawItem(ghost, this.x + slotXs[i], this.y + slotY);
                }
            }
        }

        ItemStack output = recipe.getOutput();
        context.drawItem(output, this.x + 118, this.y + 35);
    }

    private void drawProgress(DrawContext context) {
        if (!handler.isWorking()) return;

        int progress = handler.getWorkTicks();
        int width = progress * 24 / 120;

        context.fill(this.x + 106, this.y + 58, this.x + 106 + width, this.y + 62, 0xFF7FD3FF);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}