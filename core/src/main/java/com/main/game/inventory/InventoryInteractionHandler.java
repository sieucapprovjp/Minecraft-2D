package com.main.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.main.game.crafting.CraftingController;

public class InventoryInteractionHandler {

    private ItemStack carriedStack;

    public void update(Inventory inventory, InventoryRenderer renderer) {
        update(inventory, renderer, null);
    }

    public void update(Inventory inventory, InventoryRenderer renderer, CraftingController craftingController) {
        if (inventory == null || renderer == null) {
            return;
        }

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        int slot = renderer.findHoveredSlot(mouseX, mouseY, craftingController);
        if (slot < 0) {
            return;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            onLeftClick(inventory, craftingController, slot);
        } else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            onRightClick(inventory, craftingController, slot);
        }
    }

    public void onCloseInventory(Inventory inventory) {
        onCloseInventory(inventory, null);
    }

    public void onCloseInventory(Inventory inventory, CraftingController craftingController) {
        if (inventory == null) {
            carriedStack = null;
            return;
        }

        carriedStack = returnStackToInventory(inventory, carriedStack);
        if (craftingController != null) {
            craftingController.closeCrafting(inventory);
        }
    }

    public ItemStack getCarriedStack() {
        return carriedStack;
    }

    private void onLeftClick(Inventory inventory, CraftingController craftingController, int slotIndex) {
        if (InventoryLayout.isCraftResultSlot(slotIndex)) {
            takeCraftingResult(craftingController);
            return;
        }
        if (!isWritableSlot(inventory, craftingController, slotIndex)) {
            return;
        }

        ItemStack slotStack = getSlot(inventory, craftingController, slotIndex);
        if (carriedStack == null) {
            if (slotStack != null && slotStack.getCount() > 0) {
                carriedStack = slotStack;
                setSlot(inventory, craftingController, slotIndex, null);
            }
            return;
        }

        if (slotStack == null || slotStack.getCount() <= 0) {
            setSlot(inventory, craftingController, slotIndex, carriedStack);
            carriedStack = null;
            return;
        }

        if (!slotStack.getItemId().equals(carriedStack.getItemId())) {
            setSlot(inventory, craftingController, slotIndex, carriedStack);
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

    private void onRightClick(Inventory inventory, CraftingController craftingController, int slotIndex) {
        if (InventoryLayout.isCraftResultSlot(slotIndex)) {
            takeCraftingResult(craftingController);
            return;
        }
        if (!isWritableSlot(inventory, craftingController, slotIndex)) {
            return;
        }

        ItemStack slotStack = getSlot(inventory, craftingController, slotIndex);
        if (carriedStack == null) {
            if (slotStack == null || slotStack.getCount() <= 0) {
                return;
            }
            int take = (slotStack.getCount() + 1) / 2;
            carriedStack = slotStack.copy();
            carriedStack.setCount(take);
            slotStack.subtract(take);
            if (slotStack.getCount() <= 0) {
                setSlot(inventory, craftingController, slotIndex, null);
            }
            return;
        }

        if (slotStack == null || slotStack.getCount() <= 0) {
            ItemStack placed = carriedStack.copy();
            placed.setCount(1);
            setSlot(inventory, craftingController, slotIndex, placed);
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

    private void takeCraftingResult(CraftingController craftingController) {
        if (craftingController == null) {
            return;
        }
        carriedStack = craftingController.takeResult(carriedStack);
    }

    private ItemStack returnStackToInventory(Inventory inventory, ItemStack stack) {
        if (stack == null || stack.getCount() <= 0) {
            return null;
        }
        return inventory.addStack(stack);
    }

    private boolean isWritableSlot(Inventory inventory, CraftingController craftingController, int slotIndex) {
        if (slotIndex >= 0 && slotIndex < inventory.getTotalSize()) {
            return true;
        }
        return craftingController != null
            && InventoryLayout.isCraftInputSlot(slotIndex, craftingController.getGrid());
    }

    private ItemStack getSlot(Inventory inventory, CraftingController craftingController, int slotIndex) {
        if (slotIndex >= 0 && slotIndex < inventory.getTotalSize()) {
            return inventory.getSlot(slotIndex);
        }
        if (craftingController != null && InventoryLayout.isCraftInputSlot(slotIndex, craftingController.getGrid())) {
            return craftingController.getGrid().getSlot(InventoryLayout.toCraftInputIndex(slotIndex));
        }
        return null;
    }

    private void setSlot(Inventory inventory, CraftingController craftingController, int slotIndex, ItemStack stack) {
        if (slotIndex >= 0 && slotIndex < inventory.getTotalSize()) {
            inventory.setSlot(slotIndex, stack);
            return;
        }
        if (craftingController != null && InventoryLayout.isCraftInputSlot(slotIndex, craftingController.getGrid())) {
            craftingController.getGrid().setSlot(InventoryLayout.toCraftInputIndex(slotIndex), stack);
        }
    }
}
