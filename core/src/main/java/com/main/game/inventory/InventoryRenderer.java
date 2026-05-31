package com.main.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.main.game.crafting.CraftingController;
import com.main.game.crafting.CraftingGrid;

public class InventoryRenderer {

    private static final float HOTBAR_SLOT_STEP_PX = 80f;
    private static final float HOTBAR_ITEM_X_PX = 8f;
    private static final float HOTBAR_ITEM_Y_PX = 12f;
    private static final float HOTBAR_ITEM_SIZE_PX = 64f;
    private static final float INV_ITEM_SIZE_PX = 26f;
    private static final float INV_ITEM_Y_OFFSET_PX = -2f;
    private static final float CRAFT_ITEM_X_OFFSET_PX = 4f;
    private static final float CRAFT_ITEM_Y_OFFSET_PX = -4f;
    private static final float TABLE_CRAFT_ITEM_Y_OFFSET_PX = 2f;

    private final BitmapFont font;
    private final Texture inventoryTexture;
    private final Texture craftingTableTexture;
    private final Texture durabilityTexture;

    public InventoryRenderer() {
        inventoryTexture = new Texture(Gdx.files.internal("images/gui_invrow/inventory.png"));
        craftingTableTexture = new Texture(Gdx.files.internal("images/gui_invrow/crafting_table.png"));
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

    public void renderInventory(SpriteBatch batch, Inventory inventory, CraftingController craftingController,
                                float sw, float sh, float scale) {
        CraftingGrid grid = craftingController == null ? null : craftingController.getGrid();
        Texture panelTexture = craftingController != null && craftingController.isTableCrafting()
            ? craftingTableTexture
            : inventoryTexture;
        float craftItemYOffset = craftingController != null && craftingController.isTableCrafting()
            ? TABLE_CRAFT_ITEM_Y_OFFSET_PX
            : CRAFT_ITEM_Y_OFFSET_PX;
        InventoryLayout.PanelRect panel = InventoryLayout.computePanel(sw, sh, panelTexture.getWidth(), panelTexture.getHeight());

        batch.setColor(Color.WHITE);
        batch.draw(panelTexture, panel.x, panel.y, panel.width, panel.height);

        if (craftingController != null && grid != null) {
            for (int i = 0; i < grid.getSize(); i++) {
                drawSlotItemInSlot(batch, grid.getSlot(i),
                    InventoryLayout.craftInputSlotX(panel, grid, i) + CRAFT_ITEM_X_OFFSET_PX * panel.scale,
                    InventoryLayout.craftInputSlotY(panel, grid, i) + craftItemYOffset * panel.scale,
                    InventoryLayout.slotSize(panel, grid));
            }
            drawSlotItemInSlot(batch, craftingController.getResult(),
                InventoryLayout.craftResultSlotX(panel, grid) + CRAFT_ITEM_X_OFFSET_PX * panel.scale,
                InventoryLayout.craftResultSlotY(panel, grid) + craftItemYOffset * panel.scale,
                InventoryLayout.slotSize(panel, grid));
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
                int slotIndex = Inventory.HOTBAR_SIZE + row * Inventory.HOTBAR_SIZE + col;
                drawSlotItemInSlot(batch, inventory.getSlot(slotIndex),
                    InventoryLayout.inventorySlotX(panel, grid, col),
                    InventoryLayout.inventoryMainSlotY(panel, grid, row),
                    InventoryLayout.slotSize(panel, grid));
            }
        }
        for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
            drawSlotItemInSlot(batch, inventory.getSlot(col),
                InventoryLayout.inventorySlotX(panel, grid, col),
                InventoryLayout.hotbarSlotY(panel, grid),
                InventoryLayout.slotSize(panel, grid));
        }
    }

    public void renderInventory(SpriteBatch batch, Inventory inventory, float sw, float sh, float scale) {
        renderInventory(batch, inventory, null, sw, sh, scale);
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
        return findHoveredSlot(screenX, screenY, null);
    }

    public int findHoveredSlot(float screenX, float screenY, CraftingController craftingController) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        CraftingGrid grid = craftingController == null ? null : craftingController.getGrid();
        Texture panelTexture = craftingController != null && craftingController.isTableCrafting()
            ? craftingTableTexture
            : inventoryTexture;
        InventoryLayout.PanelRect panel = InventoryLayout.computePanel(sw, sh, panelTexture.getWidth(), panelTexture.getHeight());
        return InventoryLayout.findInventorySlot(screenX, screenY, panel, grid);
    }

    public void dispose() {
        font.dispose();
        inventoryTexture.dispose();
        craftingTableTexture.dispose();
        durabilityTexture.dispose();
    }

    private void drawSlotItemInSlot(SpriteBatch batch, ItemStack stack, float slotX, float slotY, float slotSize) {
        float itemSize = slotSize * (INV_ITEM_SIZE_PX / InventoryLayout.INV_SLOT_SIZE_PX);
        float itemX = slotX + (slotSize - itemSize) / 2f;
        float itemY = slotY + (slotSize - itemSize) / 2f + INV_ITEM_Y_OFFSET_PX * (slotSize / InventoryLayout.INV_SLOT_SIZE_PX);
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
        drawDurabilityBar(batch, stack, x, y - size * 0.08f, size);
        if (stack.getCount() > 1) {
            font.setColor(Color.WHITE);
            font.draw(batch, String.valueOf(stack.getCount()), x + size * 0.48f, y + size * 0.36f);
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
