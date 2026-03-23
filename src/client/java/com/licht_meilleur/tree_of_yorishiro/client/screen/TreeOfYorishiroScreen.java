package com.licht_meilleur.tree_of_yorishiro.client.screen;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.screen.TreeOfYorishiroScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeChibishiroData;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;

public class TreeOfYorishiroScreen extends HandledScreen<TreeOfYorishiroScreenHandler> {

    private static final Identifier RED_UI = TreeofYorishiroMod.id("textures/gui/red_ui.png");
    private static final Identifier BLUE_UI = TreeofYorishiroMod.id("textures/gui/blue_ui.png");
    private static final Identifier YELLOW_UI = TreeofYorishiroMod.id("textures/gui/yellow_ui.png");
    private static final Identifier PURPLE_UI = TreeofYorishiroMod.id("textures/gui/purple_ui.png");
    private static final Identifier WHITE_UI = TreeofYorishiroMod.id("textures/gui/white_ui.png");
    private static final Identifier TREASURE_UI = TreeofYorishiroMod.id("textures/gui/treasure_ui.png");

    private static final Identifier TAB_UI = TreeofYorishiroMod.id("textures/gui/chibishiro_ui_tab.png");
    private static final Identifier BUTTON_UI = TreeofYorishiroMod.id("textures/gui/chibishiro_button.png");

    private static final Identifier WHITE_TEX = TreeofYorishiroMod.id("textures/entity/white.png");
    private static final Identifier RED_TEX = TreeofYorishiroMod.id("textures/entity/red.png");
    private static final Identifier BLUE_TEX = TreeofYorishiroMod.id("textures/entity/blue.png");
    private static final Identifier YELLOW_TEX = TreeofYorishiroMod.id("textures/entity/yellow.png");
    private static final Identifier PURPLE_TEX = TreeofYorishiroMod.id("textures/entity/purple.png");

    // 0=白, 1=赤, 2=青, 3=黄, 4=紫, 5=冒険
    private int selectedTab = 0;


    private com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity previewChibi;
    private int previewColorCache = -1;

    private enum DetailPage {
        MAIN,
        MEAL,
        STUDY,
        EXERCISE,
        PLAY,
        ADVENTURE
    }

    private DetailPage detailPage = DetailPage.MAIN;

    public TreeOfYorishiroScreen(TreeOfYorishiroScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 256;
        this.backgroundHeight = 256;
        this.playerInventoryTitleY = 10000;
    }

    @Override
    protected void init() {
        super.init();

        this.titleX = this.backgroundWidth - this.textRenderer.getWidth(this.title) - 10;
        this.titleY = this.backgroundHeight - 96;
    }

    private Identifier getCurrentBackground() {
        return switch (selectedTab) {
            case 1 -> RED_UI;
            case 2 -> BLUE_UI;
            case 3 -> YELLOW_UI;
            case 4 -> PURPLE_UI;
            case 5 -> TREASURE_UI;
            default -> WHITE_UI;
        };
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 背景
        context.drawTexture(getCurrentBackground(), x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        // タブ
        context.drawTexture(TAB_UI, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        // タブラベル
        context.drawText(this.textRenderer, "W",   x + 0,  y + 5, 0xFFFFFF, true);
        context.drawText(this.textRenderer, "R",   x + 24,  y + 5, 0xFFFFFF, true);
        context.drawText(this.textRenderer, "B",   x + 48,  y + 5, 0xFFFFFF, true);
        context.drawText(this.textRenderer, "Y",   x + 72, y + 5, 0xFFFFFF, true);
        context.drawText(this.textRenderer, "P",   x + 96, y + 5, 0xFFFFFF, true);
        context.drawText(this.textRenderer, "ADV", x + 120, y + 5, 0xFFFFFF, true);

        // 選択中タブのハイライト
        drawSelectedTabHighlight(context, x, y);

        if (selectedTab != 5) {
            if (detailPage == DetailPage.MAIN) {
                drawSingleChibi3D(context, x, y);
                drawStatusTexts(context, x, y);
                drawActionButtons(context, x, y);
            } else {
                drawTrainingDetailPage(context, x, y);
            }
        } else {
            drawAdventureArea(context, x, y);
        }
    }

    private void drawSelectedTabHighlight(DrawContext context, int x, int y) {
        int tabX;
        int tabWidth;

        switch (selectedTab) {
            case 0 -> { tabX = x + 0;  tabWidth = 24; } // 白
            case 1 -> { tabX = x + 24;  tabWidth = 24; } // 赤
            case 2 -> { tabX = x + 48;  tabWidth = 24; } // 青
            case 3 -> { tabX = x + 72; tabWidth = 24; } // 黄
            case 4 -> { tabX = x + 96; tabWidth = 24; } // 紫
            case 5 -> { tabX = x + 120; tabWidth = 68; } // 冒険
            default -> { tabX = x + 0; tabWidth = 24; }
        }

        int tabY = y + 2;
        int tabHeight = 16;

        // 半透明白
        context.fill(tabX, tabY, tabX + tabWidth, tabY + tabHeight, 0x80FFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        // 白
        if (isInside(mouseX, mouseY, x + 0, y + 8, 24, 16)) {
            selectedTab = 0;
            return true;
        }
        // 赤
        if (isInside(mouseX, mouseY, x + 24, y + 8, 24, 16)) {
            selectedTab = 1;
            return true;
        }
        // 青
        if (isInside(mouseX, mouseY, x + 48, y + 8, 24, 16)) {
            selectedTab = 2;
            return true;
        }
        // 黄
        if (isInside(mouseX, mouseY, x + 72, y + 8, 24, 16)) {
            selectedTab = 3;
            return true;
        }
        // 紫
        if (isInside(mouseX, mouseY, x + 96, y + 8, 24, 16)) {
            selectedTab = 4;
            return true;
        }
        // 冒険
        if (isInside(mouseX, mouseY, x + 120, y + 8, 68, 16)) {
            selectedTab = 5;
            return true;
        }

        int bx = x + 180;
        int by = y + 60;

// 通常画面のときだけ
        if (detailPage == DetailPage.MAIN && selectedTab != 5) {
            if (isInside(mouseX, mouseY, bx, by, 64, 16)) {
                detailPage = DetailPage.MEAL;
                return true;
            }
            if (isInside(mouseX, mouseY, bx, by + 24, 64, 16)) {
                detailPage = DetailPage.STUDY;
                return true;
            }
            if (isInside(mouseX, mouseY, bx, by + 48, 64, 16)) {
                detailPage = DetailPage.EXERCISE;
                return true;
            }
            if (isInside(mouseX, mouseY, bx, by + 72, 64, 16)) {
                detailPage = DetailPage.PLAY;
                return true;
            }
        }
        if (selectedTab == 5) {
            detailPage = DetailPage.ADVENTURE;
        } else if (detailPage == DetailPage.ADVENTURE) {
            detailPage = DetailPage.MAIN;
        }
        if (detailPage != DetailPage.MAIN && detailPage != DetailPage.ADVENTURE) {
            if (isInside(mouseX, mouseY, x + 18, y + 18, 40, 12)) {
                detailPage = DetailPage.MAIN;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isInside(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
    private Identifier getTextureForColor(ChibishiroColor color) {
        return switch (color) {
            case RED -> RED_TEX;
            case BLUE -> BLUE_TEX;
            case YELLOW -> YELLOW_TEX;
            case PURPLE -> PURPLE_TEX;
            default -> WHITE_TEX;
        };
    }

    private TreeOfYorishiroBlockEntity getTreeBlockEntity() {
        if (client == null || client.world == null) return null;

        TreeOfYorishiroBlockEntity be = handler.getBlockEntity(client.world);
        if (be != null && !be.isInitialized()) {
            be.initDefaultChibisIfNeeded();
        }
        return be;
    }

    private TreeChibishiroData getSelectedChibiData() {
        TreeOfYorishiroBlockEntity be = getTreeBlockEntity();
        if (be == null) {
            System.out.println("[YorishiroUI] BlockEntity is null");
            return null;
        }

        if (selectedTab < 0 || selectedTab >= 5) {
            System.out.println("[YorishiroUI] selectedTab out of range: " + selectedTab);
            return null;
        }

        TreeChibishiroData data = be.getChibi(selectedTab);
        if (data == null) {
            System.out.println("[YorishiroUI] Chibi data is null at index " + selectedTab);
        }

        return data;
    }

    private void drawSingleChibiArea(DrawContext context, int x, int y) {
        TreeChibishiroData data = getSelectedChibiData();
        if (data == null) return;

        Identifier tex = getTextureForColor(data.getColor());

        int drawX = x + 18;
        int drawY = y + 18;

        // 背景プレート
        context.fill(drawX - 4, drawY - 4, drawX + 84, drawY + 84, 0x66000000);

        // 仮表示
        context.drawTexture(tex, drawX, drawY, 0, 0, 80, 80, 64, 64);
    }

    private void drawStatusTexts(DrawContext context, int x, int y) {
        TreeChibishiroData data = getSelectedChibiData();
        if (data == null) {
            context.drawText(this.textRenderer, "NO DATA", x + 20, y + 90, 0xFF4444, true);
            return;
        }

        int sx = x + 20;
        int sy = y + 110;
        int line = 22;

        context.drawText(this.textRenderer,
                Text.translatable("screen.tree_of_yorishiro.genki").append(" : " + data.getGenki()),
                sx, sy, 0xFFFFFF, true);

        context.drawText(this.textRenderer,
                Text.translatable("screen.tree_of_yorishiro.kashikosa").append(" : " + data.getKashikosa()),
                sx, sy + line, 0xFFFFFF, true);

        context.drawText(this.textRenderer,
                Text.translatable("screen.tree_of_yorishiro.chikara").append(" : " + data.getChikara()),
                sx, sy + line * 2, 0xFFFFFF, true);

        context.drawText(this.textRenderer,
                Text.translatable("screen.tree_of_yorishiro.stress").append(" : " + data.getStress()),
                sx, sy + line * 3, 0xFFFFFF, true);
    }

    private void drawAdventureArea(DrawContext context, int x, int y) {
        TreeOfYorishiroBlockEntity be = getTreeBlockEntity();
        if (be == null) return;

        context.drawText(this.textRenderer,
                Text.translatable("screen.tree_of_yorishiro.adventure_menu"),
                x + 78, y + 22, 0xFFFFFF, true);

        context.drawText(this.textRenderer,
                Text.translatable("screen.tree_of_yorishiro.send_adventure"),
                x + 42, y + 46, 0xFFFFFF, true);

        for (int i = 0; i < 5; i++) {
            TreeChibishiroData data = be.getChibi(i);
            if (data == null) continue;

            Identifier tex = getTextureForColor(data.getColor());
            int drawX = x + 22 + (i * 42);
            int drawY = y + 86;

            context.fill(drawX - 2, drawY - 2, drawX + 38, drawY + 38, 0x66000000);
            context.drawTexture(tex, drawX, drawY, 0, 0, 36, 36, 64, 64);
        }


    }
    private void ensurePreviewChibi() {
        if (this.client == null || this.client.world == null) return;
        if (selectedTab < 0 || selectedTab > 4) return;

        TreeChibishiroData data = getSelectedChibiData();
        if (data == null) return;

        int colorIndex = data.getColor().ordinal();

        if (previewChibi == null || previewColorCache != colorIndex) {
            previewChibi = new com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity(
                    com.licht_meilleur.tree_of_yorishiro.registry.ModEntities.CHIBISHIRO,
                    this.client.world
            );
            previewChibi.setColor(data.getColor());
            previewColorCache = colorIndex;
        }
    }
    private void drawSingleChibi3D(DrawContext context, int x, int y) {
        ensurePreviewChibi();
        if (previewChibi == null) return;

        int drawX = x + 70;
        int drawY = y + 92;
        int scale = 38;

        net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity(
                context,
                drawX,
                drawY,
                scale,
                0,
                0,
                previewChibi
        );
    }
    private void drawActionButtons(DrawContext context, int x, int y) {
        context.drawTexture(BUTTON_UI, x + 180, y + 60,  0, 0, 64, 16, 64, 16);
        context.drawTexture(BUTTON_UI, x + 180, y + 84,  0, 0, 64, 16, 64, 16);
        context.drawTexture(BUTTON_UI, x + 180, y + 108, 0, 0, 64, 16, 64, 16);
        context.drawTexture(BUTTON_UI, x + 180, y + 132, 0, 0, 64, 16, 64, 16);

        context.drawText(this.textRenderer, Text.translatable("screen.tree_of_yorishiro.meal"), x + 200, y + 64, 0x000000, true);
        context.drawText(this.textRenderer, Text.translatable("screen.tree_of_yorishiro.study"), x + 198, y + 88, 0x000000, true);
        context.drawText(this.textRenderer, Text.translatable("screen.tree_of_yorishiro.exercise"), x + 190, y + 112, 0x000000, true);
        context.drawText(this.textRenderer, Text.translatable("screen.tree_of_yorishiro.play"), x + 201, y + 136, 0x000000, true);
    }
    private void drawTrainingDetailPage(DrawContext context, int x, int y) {
        context.drawText(this.textRenderer, Text.translatable("screen.tree_of_yorishiro.back"), x + 18, y + 28, 0xFFFFFF, true);

        switch (detailPage) {
            case MEAL -> {
                context.drawText(this.textRenderer, Text.translatable("screen.tree_of_yorishiro.meal"), x + 90, y + 40, 0xFFFFFF, true);
                context.drawText(this.textRenderer, Text.translatable("screen.tree_of_yorishiro.food_required"), x + 130, y + 72, 0xFFFFFF, true);
                context.drawText(this.textRenderer, Text.translatable("screen.tree_of_yorishiro.slot_meal"), x + 130, y + 92, 0xFFFFFF, true);
            }
            case STUDY, EXERCISE, PLAY -> {
                context.drawText(this.textRenderer, getDetailTitleText(), x + 90, y + 32, 0xFFFFFF, true);

                context.drawText(this.textRenderer, Text.literal("Lv1"), x + 70, y + 68, 0xFFFFFF, true);
                context.drawText(this.textRenderer, Text.literal("Lv2"), x + 70, y + 98, 0xFFFFFF, true);
                context.drawText(this.textRenderer, Text.literal("Lv3"), x + 70, y + 128, 0xFFFFFF, true);

                context.drawText(this.textRenderer, getRequirementText(1), x + 130, y + 68, 0xFFFFFF, true);
                context.drawText(this.textRenderer, getRequirementText(2), x + 130, y + 98, 0xFFFFFF, true);
                context.drawText(this.textRenderer, getRequirementText(3), x + 130, y + 128, 0xFFFFFF, true);
            }
        }
    }
    private Text getDetailTitleText() {
        return switch (detailPage) {
            case MEAL -> Text.translatable("screen.tree_of_yorishiro.meal");
            case STUDY -> Text.translatable("screen.tree_of_yorishiro.study");
            case EXERCISE -> Text.translatable("screen.tree_of_yorishiro.exercise");
            case PLAY -> Text.translatable("screen.tree_of_yorishiro.play");
            default -> Text.empty();
        };
    }

    private Text getRequirementText(int level) {
        return switch (detailPage) {
            case STUDY -> switch (level) {
                case 1 -> Text.translatable("item.tree_of_yorishiro.study_book");
                case 2 -> Text.translatable("item.tree_of_yorishiro.study_set");
                case 3 -> Text.translatable("item.tree_of_yorishiro.hard_study_set");
                default -> Text.empty();
            };
            case EXERCISE -> switch (level) {
                case 1 -> Text.translatable("item.tree_of_yorishiro.headband");
                case 2 -> Text.translatable("item.tree_of_yorishiro.punching_set");
                case 3 -> Text.translatable("item.tree_of_yorishiro.running_set");
                default -> Text.empty();
            };
            case PLAY -> switch (level) {
                case 1 -> Text.translatable("item.tree_of_yorishiro.ball");
                case 2 -> Text.translatable("item.tree_of_yorishiro.bubble_set");
                case 3 -> Text.translatable("item.tree_of_yorishiro.game");
                default -> Text.empty();
            };
            default -> Text.empty();
        };
    }
}