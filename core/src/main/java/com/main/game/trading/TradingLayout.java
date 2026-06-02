package com.main.game.trading;

import com.main.game.inventory.Inventory;
import com.main.game.inventory.InventoryLayout;

final class TradingLayout {

    private static final float OFFER_X = 26f;
    private static final float OFFER_BOTTOM_Y = 407f;
    private static final float OFFER_WIDTH = 247f;
    private static final float OFFER_HEIGHT = 82f;
    private static final float OFFER_STEP = 81f;
    private static final float OFFER_ICON_WIDTH = 184f;
    private static final float OFFER_ICON_HEIGHT = 50f;

    private static final float COST_SLOT_X = 348f;
    private static final float RESULT_SLOT_X = 508f;
    private static final float PREVIEW_SLOT_Y = 485f;
    private static final float PREVIEW_ITEM_OFFSET_X = 18f;
    private static final float PREVIEW_ITEM_OFFSET_Y = 18f;
    private static final float PREVIEW_ITEM_SIZE = 44f;

    private static final float INVENTORY_SLOT_ORIGIN_X = 34f;
    private static final float INVENTORY_MAIN_TOP_Y = 309f;
    private static final float INVENTORY_HOTBAR_Y = 48f;
    private static final float INVENTORY_SLOT_STEP = 80f;
    private static final float INVENTORY_SLOT_SIZE = 72f;

    private TradingLayout() {
    }

    static float offerX(InventoryLayout.PanelRect panel) {
        return panel.x + OFFER_X * panel.scale;
    }

    static float offerY(InventoryLayout.PanelRect panel, int index) {
        return panel.y + (OFFER_BOTTOM_Y + (2 - index) * OFFER_STEP) * panel.scale;
    }

    static float offerWidth(InventoryLayout.PanelRect panel) {
        return OFFER_WIDTH * panel.scale;
    }

    static float offerHeight(InventoryLayout.PanelRect panel) {
        return OFFER_HEIGHT * panel.scale;
    }

    static float offerIconX(InventoryLayout.PanelRect panel) {
        return panel.x + (OFFER_X + (OFFER_WIDTH - OFFER_ICON_WIDTH) / 2f) * panel.scale;
    }

    static float offerIconY(InventoryLayout.PanelRect panel, int index) {
        return offerY(panel, index) + (OFFER_HEIGHT - OFFER_ICON_HEIGHT) * 0.5f * panel.scale;
    }

    static float offerIconWidth(InventoryLayout.PanelRect panel) {
        return OFFER_ICON_WIDTH * panel.scale;
    }

    static float offerIconHeight(InventoryLayout.PanelRect panel) {
        return OFFER_ICON_HEIGHT * panel.scale;
    }

    static float costSlotX(InventoryLayout.PanelRect panel) {
        return panel.x + COST_SLOT_X * panel.scale;
    }

    static float resultSlotX(InventoryLayout.PanelRect panel) {
        return panel.x + RESULT_SLOT_X * panel.scale;
    }

    static float previewSlotY(InventoryLayout.PanelRect panel) {
        return panel.y + PREVIEW_SLOT_Y * panel.scale;
    }

    static float previewItemX(InventoryLayout.PanelRect panel, float slotX) {
        return slotX + PREVIEW_ITEM_OFFSET_X * panel.scale;
    }

    static float previewItemY(InventoryLayout.PanelRect panel) {
        return previewSlotY(panel) + PREVIEW_ITEM_OFFSET_Y * panel.scale;
    }

    static float previewItemSize(InventoryLayout.PanelRect panel) {
        return PREVIEW_ITEM_SIZE * panel.scale;
    }

    static float inventorySlotX(InventoryLayout.PanelRect panel, int col) {
        return panel.x + (INVENTORY_SLOT_ORIGIN_X + col * INVENTORY_SLOT_STEP) * panel.scale;
    }

    static float inventoryMainSlotY(InventoryLayout.PanelRect panel, int row) {
        return panel.y + (INVENTORY_MAIN_TOP_Y - row * INVENTORY_SLOT_STEP) * panel.scale;
    }

    static float hotbarSlotY(InventoryLayout.PanelRect panel) {
        return panel.y + INVENTORY_HOTBAR_Y * panel.scale;
    }

    static float slotSize(InventoryLayout.PanelRect panel) {
        return INVENTORY_SLOT_SIZE * panel.scale;
    }

    static int findOffer(float screenX, float screenY, InventoryLayout.PanelRect panel, int offerCount) {
        int count = Math.min(offerCount, 3);
        for (int i = 0; i < count; i++) {
            if (inside(screenX, screenY, offerX(panel), offerY(panel, i), offerWidth(panel), offerHeight(panel))) {
                return i;
            }
        }
        return -1;
    }

    static int findInventorySlot(float screenX, float screenY, InventoryLayout.PanelRect panel) {
        float slotSize = slotSize(panel);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
                int slot = Inventory.HOTBAR_SIZE + row * Inventory.HOTBAR_SIZE + col;
                if (inside(screenX, screenY,
                    inventorySlotX(panel, col), inventoryMainSlotY(panel, row), slotSize, slotSize)) {
                    return slot;
                }
            }
        }
        for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
            if (inside(screenX, screenY,
                inventorySlotX(panel, col), hotbarSlotY(panel), slotSize, slotSize)) {
                return col;
            }
        }
        return -1;
    }

    private static boolean inside(float px, float py, float x, float y, float width, float height) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }
}
