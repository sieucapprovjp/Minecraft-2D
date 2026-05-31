package com.main.game.utilityblock.chest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.main.game.inventory.Inventory;
import com.main.game.inventory.InventoryLayout;
import com.main.game.inventory.ItemRegistry;
import com.main.game.inventory.ItemRenderOffset;
import com.main.game.inventory.ItemStack;

public class ChestRenderer {

    private static final float ITEM_SIZE_RATIO = 0.8125f;
    private static final float ITEM_Y_OFFSET_PX = -2f;

    private final BitmapFont font;
    private final Texture chestTexture;
    private final Texture durabilityTexture;

    public ChestRenderer() {
        chestTexture = new Texture(Gdx.files.internal("util_block/gui/chest.png"));
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        durabilityTexture = new Texture(pixmap);
        pixmap.dispose();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/2c90030680a2fafd21f53fd39a0862e7.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 18;
        params.color = Color.WHITE;
        params.borderWidth = 2f;
        params.borderColor = new Color(0f, 0f, 0f, 0.85f);
        font = generator.generateFont(params);
        generator.dispose();
    }

    public void renderChest(SpriteBatch batch, Inventory inventory, ChestState chestState, float sw, float sh) {
        InventoryLayout.PanelRect panel = panel(sw, sh);

        batch.setColor(Color.WHITE);
        batch.draw(chestTexture, panel.x, panel.y, panel.width, panel.height);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
                int slotIndex = row * Inventory.HOTBAR_SIZE + col;
                drawSlotItemInSlot(batch, chestState.getSlot(slotIndex),
                    ChestLayout.chestSlotX(panel, col),
                    ChestLayout.chestSlotY(panel, row),
                    ChestLayout.slotSize(panel));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
                int slotIndex = Inventory.HOTBAR_SIZE + row * Inventory.HOTBAR_SIZE + col;
                drawSlotItemInSlot(batch, inventory.getSlot(slotIndex),
                    ChestLayout.inventorySlotX(panel, col),
                    ChestLayout.inventoryMainSlotY(panel, row),
                    ChestLayout.slotSize(panel));
            }
        }
        for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
            drawSlotItemInSlot(batch, inventory.getSlot(col),
                ChestLayout.inventorySlotX(panel, col),
                ChestLayout.hotbarSlotY(panel),
                ChestLayout.slotSize(panel));
        }
    }

    public void renderCarriedStack(SpriteBatch batch, ItemStack stack) {
        if (stack == null || stack.getCount() <= 0) {
            return;
        }
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        float size = 42f;
        drawSlotItem(batch, stack, mouseX - size * 0.5f, mouseY - size * 0.5f, size);
    }

    public int findHoveredSlot(float screenX, float screenY) {
        return ChestLayout.findSlot(screenX, screenY, panel(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    public void dispose() {
        font.dispose();
        chestTexture.dispose();
        durabilityTexture.dispose();
    }

    private InventoryLayout.PanelRect panel(float sw, float sh) {
        return InventoryLayout.computePanel(sw, sh, chestTexture.getWidth(), chestTexture.getHeight());
    }

    private void drawSlotItemInSlot(SpriteBatch batch, ItemStack stack, float slotX, float slotY, float slotSize) {
        float itemSize = slotSize * ITEM_SIZE_RATIO;
        float itemX = slotX + (slotSize - itemSize) / 2f;
        float itemY = slotY + (slotSize - itemSize) / 2f + ITEM_Y_OFFSET_PX * (slotSize / InventoryLayout.INV_SLOT_SIZE_PX);
        drawSlotItem(batch, stack, itemX, itemY, itemSize);
    }

    private void drawSlotItem(SpriteBatch batch, ItemStack stack, float x, float y, float size) {
        if (stack == null || stack.getCount() <= 0) {
            return;
        }
        TextureRegion texture = ItemRegistry.getTexture(stack.getItemId());
        if (texture == null) {
            return;
        }
        float renderX = x + ItemRenderOffset.xOffset(stack.getItemId(), size);
        batch.draw(texture, renderX, y, size, size);
        drawDurabilityBar(batch, stack, renderX, y - size * 0.08f, size);
        if (stack.getCount() > 1) {
            font.setColor(Color.WHITE);
            font.draw(batch, String.valueOf(stack.getCount()), renderX + size * 0.48f, y + size * 0.36f);
        }
    }

    private void drawDurabilityBar(SpriteBatch batch, ItemStack stack, float x, float y, float size) {
        if (!stack.hasDurability()) {
            return;
        }
        float ratio = stack.getDurabilityRatio();
        float barWidth = size * 0.82f;
        float barHeight = Math.max(2f, size * 0.07f);
        float barX = x + (size - barWidth) / 2f;
        float barY = y;

        batch.setColor(0f, 0f, 0f, 0.85f);
        batch.draw(durabilityTexture, barX - 1f, barY - 1f, barWidth + 2f, barHeight + 2f);

        if (ratio > 0.55f) {
            batch.setColor(0.15f, 0.9f, 0.25f, 1f);
        } else if (ratio > 0.25f) {
            batch.setColor(1f, 0.82f, 0.12f, 1f);
        } else {
            batch.setColor(0.95f, 0.16f, 0.12f, 1f);
        }
        batch.draw(durabilityTexture, barX, barY, barWidth * ratio, barHeight);
        batch.setColor(Color.WHITE);
    }
}
