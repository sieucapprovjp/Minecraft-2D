package com.main.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InventoryInteractionHandler {

    private ItemStack carriedStack;

    public void update(Inventory inventory, InventoryRenderer renderer) {
        if (inventory == null || renderer == null) {
            return;
        }

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        int slot = renderer.findHoveredSlot(mouseX, mouseY);
        if (slot < 0) {
            return;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            onLeftClick(inventory, slot);
        } else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            onRightClick(inventory, slot);
        }
    }

    public void onCloseInventory(Inventory inventory) {
        if (inventory == null || carriedStack == null || carriedStack.getCount() <= 0) {
            carriedStack = null;
            return;
        }
        int remaining = inventory.add(carriedStack.getItemId(), carriedStack.getCount());
        if (remaining <= 0) {
            carriedStack = null;
            return;
        }
        carriedStack.setCount(remaining);
    }

    public ItemStack getCarriedStack() {
        return carriedStack;
    }

    private void onLeftClick(Inventory inventory, int slotIndex) {
        ItemStack slotStack = inventory.getSlot(slotIndex);
        if (carriedStack == null) {
            if (slotStack != null && slotStack.getCount() > 0) {
                carriedStack = slotStack;
                inventory.setSlot(slotIndex, null);
            }
            return;
        }

        if (slotStack == null || slotStack.getCount() <= 0) {
            inventory.setSlot(slotIndex, carriedStack);
            carriedStack = null;
            return;
        }

        if (!slotStack.getItemId().equals(carriedStack.getItemId())) {
            inventory.setSlot(slotIndex, carriedStack);
            carriedStack = slotStack;
            return;
        }

        int maxStack = ItemRegistry.getMaxStack(carriedStack.getItemId());
        int room = Math.max(0, maxStack - slotStack.getCount());
        if (room <= 0) {
            return;
        }
        int moved = Math.min(room, carriedStack.getCount());
        slotStack.add(moved);
        carriedStack.subtract(moved);
        if (carriedStack.getCount() <= 0) {
            carriedStack = null;
        }
    }

    private void onRightClick(Inventory inventory, int slotIndex) {
        ItemStack slotStack = inventory.getSlot(slotIndex);
        if (carriedStack == null) {
            if (slotStack == null || slotStack.getCount() <= 0) {
                return;
            }
            int take = (slotStack.getCount() + 1) / 2;
            carriedStack = new ItemStack(slotStack.getItemId(), take);
            slotStack.subtract(take);
            if (slotStack.getCount() <= 0) {
                inventory.setSlot(slotIndex, null);
            }
            return;
        }

        if (slotStack == null || slotStack.getCount() <= 0) {
            inventory.setSlot(slotIndex, new ItemStack(carriedStack.getItemId(), 1));
            carriedStack.subtract(1);
            if (carriedStack.getCount() <= 0) {
                carriedStack = null;
            }
            return;
        }

        if (!slotStack.getItemId().equals(carriedStack.getItemId())) {
            return;
        }
        int maxStack = ItemRegistry.getMaxStack(carriedStack.getItemId());
        if (slotStack.getCount() >= maxStack) {
            return;
        }
        slotStack.add(1);
        carriedStack.subtract(1);
        if (carriedStack.getCount() <= 0) {
            carriedStack = null;
        }
    }
}
