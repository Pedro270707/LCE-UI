package net.kyrptonaught.lceui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.lceui.LCEDrawableHelper;
import net.kyrptonaught.lceui.LCEUIMod;
import net.kyrptonaught.lceui.util.LCESounds;
import net.kyrptonaught.lceui.util.ScalableSlot;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class LCEContainerScreen extends HandledScreen<LCEContainerScreen.LCEGenericContainerScreenHandler> implements ScreenHandlerProvider<LCEContainerScreen.LCEGenericContainerScreenHandler> {
    public static final Identifier TEXTURE = new Identifier(LCEUIMod.MOD_ID, "textures/gui/container/generic_54.png");

    private final int rows;

    public LCEContainerScreen(GenericContainerScreenHandler handler, PlayerEntity player, Text title) {
        super(new LCEGenericContainerScreenHandler(player, handler), player.getInventory(), title);
        this.rows = handler.getRows();
        this.backgroundWidth = 430/3;
        this.backgroundHeight = (289 + this.rows * 42)/3;
        this.playerInventoryTitleY = this.backgroundHeight - 76;
    }

    @Override
    protected void init() {
        super.init();
        this.client.getSoundManager().play(PositionedSoundInstance.master(LCESounds.CLICK_STEREO, 1.0f, 3.0f));
    }

    @Override
    public void removed() {
        super.removed();
        this.client.getSoundManager().play(PositionedSoundInstance.master(LCESounds.UI_BACK, 1.0f, 3.0f));
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        LCEDrawableHelper.drawText(context, this.textRenderer, this.title, 9 + 1.0f/3.0f, 7 + 1.0f/3.0f, 2.0f/3.0f, 0x373737);
        LCEDrawableHelper.drawText(context, this.textRenderer, Text.translatable("container.inventory"), 9 + 1.0f/3.0f, this.playerInventoryTitleY, 2.0f/3.0f, 0x373737);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        LCEDrawableHelper.drawTexture(TEXTURE, context, i, j, 0, 0, 430.0f/3.0f, (this.rows * 42 + 50.0f)/3.0f, 1024.0f/3.0f, 1024.0f/3.0f);
        LCEDrawableHelper.drawTexture(TEXTURE, context, i, j + this.rows * 42.0f/3.0f + 16 + 2.0f/3.0f, 0, 442, 430.0f/3.0f, 239.0f/3.0f, 1024.0f/3.0f, 1024.0f/3.0f);
    }

    @Environment(EnvType.CLIENT)
    public static class LCEGenericContainerScreenHandler extends ScreenHandler {
        private final GenericContainerScreenHandler parent;

        public LCEGenericContainerScreenHandler(PlayerEntity player, GenericContainerScreenHandler parent) {
            super(parent.getType(), parent.syncId);
            this.parent = parent;
            Inventory inventory = parent.getInventory();
            int rows = parent.getRows();

            float slotScale = 19.0f/24.0f;
            float itemScale = 7.0f/8.0f;

            float i = (rows - 4) * (18 * slotScale - 1.0f/4.0f);


            int j;
            int k;
            for(j = 0; j < rows; ++j) {
                for(k = 0; k < 9; ++k) {
                    this.addSlot(new ScalableSlot(inventory, k + j * 9, 9 + k * (18 * slotScale - 1.0f/4.0f), 17 + 1.0f/3.0f + j * (18 * slotScale - 1.0f/4.0f), slotScale, slotScale * itemScale));
                }
            }

            for(j = 0; j < 3; ++j) {
                for(k = 0; k < 9; ++k) {
                    this.addSlot(new ScalableSlot(player.getInventory(), k + j * 9 + 9, 9 + k * (18 * slotScale - 1.0f/4.0f), 85 + 1.0f/3.0f + j * (18 * slotScale - 1.0f/4.0f) + i, slotScale, slotScale * itemScale));
                }
            }

            for(j = 0; j < 9; ++j) {
                this.addSlot(new ScalableSlot(player.getInventory(), j, 9 + j * (18 * slotScale - 1.0f/4.0f), 132 + i, slotScale, slotScale * itemScale));
            }
        }

        @Override
        public ItemStack quickMove(PlayerEntity player, int index) {
            return this.parent.quickMove(player, index);
        }

        @Override
        public boolean canUse(PlayerEntity player) {
            return this.parent.canUse(player);
        }

        @Override
        public ItemStack getCursorStack() {
            return this.parent.getCursorStack();
        }

        @Override
        public void setCursorStack(ItemStack stack) {
            this.parent.setCursorStack(stack);
        }
    }
}
