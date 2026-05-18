package com.main.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InventoryController {

    private int selectedHotbarSlot = 0;
    private boolean inventoryOpen = false;
    private boolean justClosed = false;

    public void update() {
        justClosed = false;
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            boolean wasOpen = inventoryOpen;
            inventoryOpen = !inventoryOpen;
            justClosed = wasOpen && !inventoryOpen;
        }
        for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                selectedHotbarSlot = i;
            }
        }
    }

    public int getSelectedHotbarSlot() {
        return selectedHotbarSlot;
    }

    public boolean isInventoryOpen() {
        return inventoryOpen;
    }

    public boolean wasJustClosed() {
        return justClosed;
    }
}
