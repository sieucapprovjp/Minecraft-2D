package com.main.game.inventory;

public class InventoryLayout {

    public static final float INVENTORY_TARGET_SCREEN_HEIGHT = 0.82f;
    public static final float INVENTORY_MAX_SCREEN_WIDTH = 0.72f;
    public static final float INV_SLOT_ORIGIN_X = 20f;
    public static final float INV_MAIN_ORIGIN_Y = 78f;
    public static final float INV_HOTBAR_ORIGIN_Y = 24f;
    public static final float INV_SLOT_STEP_PX = 40f;
    public static final float INV_SLOT_SIZE_PX = 32f;

    public static PanelRect computePanel(float screenWidth, float screenHeight, float textureWidth, float textureHeight) {
        float panelScale = Math.min(
            (screenHeight * INVENTORY_TARGET_SCREEN_HEIGHT) / textureHeight,
            (screenWidth * INVENTORY_MAX_SCREEN_WIDTH) / textureWidth
        );
        float panelW = textureWidth * panelScale;
        float panelH = textureHeight * panelScale;
        float panelX = (screenWidth - panelW) / 2f;
        float panelY = (screenHeight - panelH) / 2f;
        return new PanelRect(panelX, panelY, panelW, panelH, panelScale);
    }

    public static int findInventorySlot(float screenX, float screenY, PanelRect panel) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
                int slot = Inventory.HOTBAR_SIZE + row * Inventory.HOTBAR_SIZE + col;
                float slotX = panel.x + (INV_SLOT_ORIGIN_X + col * INV_SLOT_STEP_PX) * panel.scale;
                float slotY = panel.y + (INV_MAIN_ORIGIN_Y + (2 - row) * INV_SLOT_STEP_PX) * panel.scale;
                if (insideSlot(screenX, screenY, slotX, slotY, panel.scale)) {
                    return slot;
                }
            }
        }
        for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
            float slotX = panel.x + (INV_SLOT_ORIGIN_X + col * INV_SLOT_STEP_PX) * panel.scale;
            float slotY = panel.y + INV_HOTBAR_ORIGIN_Y * panel.scale;
            if (insideSlot(screenX, screenY, slotX, slotY, panel.scale)) {
                return col;
            }
        }
        return -1;
    }

    private static boolean insideSlot(float px, float py, float slotX, float slotY, float scale) {
        float slotSize = INV_SLOT_SIZE_PX * scale;
        return px >= slotX && px <= slotX + slotSize && py >= slotY && py <= slotY + slotSize;
    }

    public static class PanelRect {
        public final float x;
        public final float y;
        public final float width;
        public final float height;
        public final float scale;

        public PanelRect(float x, float y, float width, float height, float scale) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.scale = scale;
        }
    }
}
