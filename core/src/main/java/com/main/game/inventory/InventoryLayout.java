package com.main.game.inventory;

import com.main.game.crafting.CraftingGrid;

public class InventoryLayout {

    public static final float INVENTORY_TARGET_SCREEN_HEIGHT = 0.82f;
    public static final float INVENTORY_MAX_SCREEN_WIDTH = 0.72f;
    public static final float INV_SLOT_ORIGIN_X = 20f;
    public static final float INV_MAIN_ORIGIN_Y = 78f;
    public static final float INV_HOTBAR_ORIGIN_Y = 24f;
    public static final float INV_SLOT_STEP_PX = 40f;
    public static final float INV_SLOT_SIZE_PX = 32f;
    public static final int CRAFT_INPUT_BASE_SLOT = Inventory.TOTAL_SIZE;
    public static final int CRAFT_MAX_INPUT_SLOTS = CraftingGrid.MAX_SIZE;
    public static final int CRAFT_RESULT_SLOT = CRAFT_INPUT_BASE_SLOT + CRAFT_MAX_INPUT_SLOTS;
    public static final float CRAFT_GRID_ORIGIN_X = 196f;
    public static final float CRAFT_GRID_TOP_Y = 272f;
    public static final float CRAFT_RESULT_X = 316f;
    public static final float CRAFT_RESULT_Y = 252f;

    private static final float TABLE_SLOT_ORIGIN_X = 34f;
    private static final float TABLE_MAIN_TOP_Y = 309f;
    private static final float TABLE_HOTBAR_Y = 48f;
    private static final float TABLE_SLOT_STEP_PX = 80f;
    private static final float TABLE_SLOT_SIZE_PX = 72f;
    private static final float TABLE_CRAFT_GRID_ORIGIN_X = 194f;
    private static final float TABLE_CRAFT_GRID_TOP_Y = 572f;
    private static final float TABLE_CRAFT_RESULT_X = 514f;
    private static final float TABLE_CRAFT_RESULT_Y = 492f;

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

    public static int findInventorySlot(float screenX, float screenY, PanelRect panel, CraftingGrid grid) {
        int craftingSlot = findCraftingSlot(screenX, screenY, panel, grid);
        if (craftingSlot >= 0) {
            return craftingSlot;
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
                int slot = Inventory.HOTBAR_SIZE + row * Inventory.HOTBAR_SIZE + col;
                if (insideSlot(screenX, screenY,
                    inventorySlotX(panel, grid, col),
                    inventoryMainSlotY(panel, grid, row),
                    slotSize(panel, grid))) {
                    return slot;
                }
            }
        }
        for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
            if (insideSlot(screenX, screenY,
                inventorySlotX(panel, grid, col),
                hotbarSlotY(panel, grid),
                slotSize(panel, grid))) {
                return col;
            }
        }
        return -1;
    }

    public static int findInventorySlot(float screenX, float screenY, PanelRect panel) {
        return findInventorySlot(screenX, screenY, panel, null);
    }

    public static boolean isCraftInputSlot(int slot) {
        return slot >= CRAFT_INPUT_BASE_SLOT && slot < CRAFT_INPUT_BASE_SLOT + CRAFT_MAX_INPUT_SLOTS;
    }

    public static boolean isCraftInputSlot(int slot, CraftingGrid grid) {
        return grid != null
            && slot >= CRAFT_INPUT_BASE_SLOT
            && slot < CRAFT_INPUT_BASE_SLOT + grid.getSize();
    }

    public static boolean isCraftResultSlot(int slot) {
        return slot == CRAFT_RESULT_SLOT;
    }

    public static int toCraftInputIndex(int slot) {
        return isCraftInputSlot(slot) ? slot - CRAFT_INPUT_BASE_SLOT : -1;
    }

    public static float inventorySlotX(PanelRect panel, CraftingGrid grid, int col) {
        float originX = isTableGrid(grid) ? TABLE_SLOT_ORIGIN_X : INV_SLOT_ORIGIN_X;
        float step = isTableGrid(grid) ? TABLE_SLOT_STEP_PX : INV_SLOT_STEP_PX;
        return panel.x + (originX + col * step) * panel.scale;
    }

    public static float inventoryMainSlotY(PanelRect panel, CraftingGrid grid, int row) {
        if (isTableGrid(grid)) {
            return panel.y + (TABLE_MAIN_TOP_Y - row * TABLE_SLOT_STEP_PX) * panel.scale;
        }
        return panel.y + (INV_MAIN_ORIGIN_Y + (2 - row) * INV_SLOT_STEP_PX) * panel.scale;
    }

    public static float hotbarSlotY(PanelRect panel, CraftingGrid grid) {
        return panel.y + (isTableGrid(grid) ? TABLE_HOTBAR_Y : INV_HOTBAR_ORIGIN_Y) * panel.scale;
    }

    public static float craftInputSlotX(PanelRect panel, CraftingGrid grid, int index) {
        int width = grid == null ? CraftingGrid.PLAYER_WIDTH : grid.getWidth();
        int col = index % width;
        float originX = isTableGrid(grid) ? TABLE_CRAFT_GRID_ORIGIN_X : CRAFT_GRID_ORIGIN_X;
        float step = isTableGrid(grid) ? TABLE_SLOT_STEP_PX : INV_SLOT_STEP_PX;
        return panel.x + (originX + col * step) * panel.scale;
    }

    public static float craftInputSlotX(PanelRect panel, int index) {
        return craftInputSlotX(panel, null, index);
    }

    public static float craftInputSlotY(PanelRect panel, CraftingGrid grid, int index) {
        int width = grid == null ? CraftingGrid.PLAYER_WIDTH : grid.getWidth();
        int row = index / width;
        float topY = isTableGrid(grid) ? TABLE_CRAFT_GRID_TOP_Y : CRAFT_GRID_TOP_Y;
        float step = isTableGrid(grid) ? TABLE_SLOT_STEP_PX : INV_SLOT_STEP_PX;
        return panel.y + (topY - row * step) * panel.scale;
    }

    public static float craftInputSlotY(PanelRect panel, int index) {
        return craftInputSlotY(panel, null, index);
    }

    public static float craftResultSlotX(PanelRect panel, CraftingGrid grid) {
        return panel.x + (isTableGrid(grid) ? TABLE_CRAFT_RESULT_X : CRAFT_RESULT_X) * panel.scale;
    }

    public static float craftResultSlotX(PanelRect panel) {
        return craftResultSlotX(panel, null);
    }

    public static float craftResultSlotY(PanelRect panel, CraftingGrid grid) {
        return panel.y + (isTableGrid(grid) ? TABLE_CRAFT_RESULT_Y : CRAFT_RESULT_Y) * panel.scale;
    }

    public static float craftResultSlotY(PanelRect panel) {
        return craftResultSlotY(panel, null);
    }

    public static float slotSize(PanelRect panel, CraftingGrid grid) {
        return (isTableGrid(grid) ? TABLE_SLOT_SIZE_PX : INV_SLOT_SIZE_PX) * panel.scale;
    }

    private static int findCraftingSlot(float screenX, float screenY, PanelRect panel, CraftingGrid grid) {
        if (grid == null) {
            return -1;
        }
        for (int i = 0; i < grid.getSize(); i++) {
            if (insideSlot(screenX, screenY,
                craftInputSlotX(panel, grid, i),
                craftInputSlotY(panel, grid, i),
                slotSize(panel, grid))) {
                return CRAFT_INPUT_BASE_SLOT + i;
            }
        }
        if (insideSlot(screenX, screenY,
            craftResultSlotX(panel, grid),
            craftResultSlotY(panel, grid),
            slotSize(panel, grid))) {
            return CRAFT_RESULT_SLOT;
        }
        return -1;
    }

    private static boolean insideSlot(float px, float py, float slotX, float slotY, float slotSize) {
        return px >= slotX && px <= slotX + slotSize && py >= slotY && py <= slotY + slotSize;
    }

    private static boolean isTableGrid(CraftingGrid grid) {
        return grid != null
            && grid.getWidth() == CraftingGrid.TABLE_WIDTH
            && grid.getHeight() == CraftingGrid.TABLE_HEIGHT;
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
