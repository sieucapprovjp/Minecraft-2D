package com.main.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class InventoryRenderer {

    private static final float HOTBAR_SLOT_STEP_PX = 80f;
    private static final float HOTBAR_ITEM_X_PX = 8f;
    private static final float HOTBAR_ITEM_Y_PX = 12f;
    private static final float HOTBAR_ITEM_SIZE_PX = 64f;
    private static final float INV_ITEM_SIZE_PX = 26f;
    private static final float INV_ITEM_Y_OFFSET_PX = -2f;

    private final BitmapFont font;
    private final Texture inventoryTexture;

    public InventoryRenderer() {
        inventoryTexture = new Texture(Gdx.files.internal("images/gui_invrow/inventory.png"));
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

    public void renderHotbar(SpriteBatch batch, Inventory inventory, InventoryController controller,
                             Texture hotbarTexture, Texture selectorTexture, float sw, float scale) {
        float hbW = hotbarTexture.getWidth() * scale;
        float hbH = hotbarTexture.getHeight() * scale;
        float hbX = (sw - hbW) / 2f;
        float hbY = 10f;
        batch.draw(hotbarTexture, hbX, hbY, hbW, hbH);

        float selW = selectorTexture.getWidth() * scale;
        float selH = selectorTexture.getHeight() * scale;
        float slotOffset = 80f * scale;
        float selX = hbX - (4f * scale) + (controller.getSelectedHotbarSlot() * slotOffset);
        float selY = hbY - (4f * scale);
        batch.draw(selectorTexture, selX, selY, selW, selH);

        for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
            drawSlotItem(batch, inventory.getSlot(i),
                hbX + (HOTBAR_ITEM_X_PX + i * HOTBAR_SLOT_STEP_PX) * scale,
                hbY + HOTBAR_ITEM_Y_PX * scale,
                HOTBAR_ITEM_SIZE_PX * scale);
        }
    }

    public void renderInventory(SpriteBatch batch, Inventory inventory, float sw, float sh, float scale) {
        InventoryLayout.PanelRect panel = InventoryLayout.computePanel(sw, sh, inventoryTexture.getWidth(), inventoryTexture.getHeight());

        batch.setColor(Color.WHITE);
        batch.draw(inventoryTexture, panel.x, panel.y, panel.width, panel.height);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
                int slotIndex = Inventory.HOTBAR_SIZE + row * Inventory.HOTBAR_SIZE + col;
                drawSlotItemInSlot(batch, inventory.getSlot(slotIndex),
                    panel.x + (InventoryLayout.INV_SLOT_ORIGIN_X + col * InventoryLayout.INV_SLOT_STEP_PX) * panel.scale,
                    panel.y + (InventoryLayout.INV_MAIN_ORIGIN_Y + (2 - row) * InventoryLayout.INV_SLOT_STEP_PX) * panel.scale,
                    panel.scale);
            }
        }
        for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
            drawSlotItemInSlot(batch, inventory.getSlot(col),
                panel.x + (InventoryLayout.INV_SLOT_ORIGIN_X + col * InventoryLayout.INV_SLOT_STEP_PX) * panel.scale,
                panel.y + InventoryLayout.INV_HOTBAR_ORIGIN_Y * panel.scale,
                panel.scale);
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
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        InventoryLayout.PanelRect panel = InventoryLayout.computePanel(sw, sh, inventoryTexture.getWidth(), inventoryTexture.getHeight());
        return InventoryLayout.findInventorySlot(screenX, screenY, panel);
    }

    public void dispose() {
        font.dispose();
        inventoryTexture.dispose();
    }

    private void drawSlotItemInSlot(SpriteBatch batch, ItemStack stack, float slotX, float slotY, float panelScale) {
        float slotSize = InventoryLayout.INV_SLOT_SIZE_PX * panelScale;
        float itemSize = INV_ITEM_SIZE_PX * panelScale;
        float itemX = slotX + (slotSize - itemSize) / 2f;
        float itemY = slotY + (slotSize - itemSize) / 2f + INV_ITEM_Y_OFFSET_PX * panelScale;
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
        batch.draw(texture, x, y, size, size);
        if (stack.getCount() > 1) {
            font.setColor(Color.WHITE);
            font.draw(batch, String.valueOf(stack.getCount()), x + size * 0.48f, y + size * 0.36f);
        }
    }
}
