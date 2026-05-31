package com.main.game.utilityblock.furnace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.main.game.inventory.Inventory;
import com.main.game.inventory.ItemRegistry;
import com.main.game.inventory.ItemStack;

public class FurnaceInteractionHandler {

    private ItemStack carriedStack;

    public void update(Inventory inventory, FurnaceState furnaceState, FurnaceRenderer renderer) {
        if (inventory == null || furnaceState == null || renderer == null) {
            return;
        }

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        int slot = renderer.findHoveredSlot(mouseX, mouseY);
        if (slot < 0) {
            return;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            onLeftClick(inventory, furnaceState, slot);
        } else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            onRightClick(inventory, furnaceState, slot);
        }
    }

    public void onCloseInventory(Inventory inventory) {
        if (inventory == null) {
            carriedStack = null;
            return;
        }
        carriedStack = returnStackToInventory(inventory, carriedStack);
    }

    public ItemStack getCarriedStack() {
        return carriedStack;
    }

    private void onLeftClick(Inventory inventory, FurnaceState furnaceState, int slotIndex) {
        if (FurnaceLayout.isOutputSlot(slotIndex)) {
            takeOutput(furnaceState, false);
            return;
        }
        if (!isWritableSlot(inventory, slotIndex)) {
            return;
        }

        ItemStack slotStack = getSlot(inventory, furnaceState, slotIndex);
        if (carriedStack == null) {
            if (slotStack != null && slotStack.getCount() > 0) {
                carriedStack = slotStack;
                setSlot(inventory, furnaceState, slotIndex, null);
            }
            return;
        }

        if (slotStack == null || slotStack.getCount() <= 0) {
            setSlot(inventory, furnaceState, slotIndex, carriedStack);
            carriedStack = null;
            return;
        }

        if (!slotStack.getItemId().equals(carriedStack.getItemId())) {
            setSlot(inventory, furnaceState, slotIndex, carriedStack);
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

    private void onRightClick(Inventory inventory, FurnaceState furnaceState, int slotIndex) {
        if (FurnaceLayout.isOutputSlot(slotIndex)) {
            takeOutput(furnaceState, true);
            return;
        }
        if (!isWritableSlot(inventory, slotIndex)) {
            return;
        }

        ItemStack slotStack = getSlot(inventory, furnaceState, slotIndex);
        if (carriedStack == null) {
            if (slotStack == null || slotStack.getCount() <= 0) {
                return;
            }
            int take = (slotStack.getCount() + 1) / 2;
            carriedStack = slotStack.copy();
            carriedStack.setCount(take);
            slotStack.subtract(take);
            if (slotStack.getCount() <= 0) {
                setSlot(inventory, furnaceState, slotIndex, null);
            }
            return;
        }

        if (slotStack == null || slotStack.getCount() <= 0) {
            ItemStack placed = carriedStack.copy();
            placed.setCount(1);
            setSlot(inventory, furnaceState, slotIndex, placed);
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

    private void takeOutput(FurnaceState furnaceState, boolean singleItem) {
        ItemStack output = furnaceState.getOutput();
        if (output == null || output.getCount() <= 0) {
            furnaceState.setOutput(null);
            return;
        }

        int moved = singleItem ? 1 : output.getCount();
        if (carriedStack == null) {
            carriedStack = output.copy();
            carriedStack.setCount(moved);
            output.subtract(moved);
            if (output.getCount() <= 0) {
                furnaceState.setOutput(null);
            }
            return;
        }

        if (!carriedStack.getItemId().equals(output.getItemId())) {
            return;
        }
        int room = ItemRegistry.getMaxStack(carriedStack.getItemId()) - carriedStack.getCount();
        if (room <= 0) {
            return;
        }
        moved = Math.min(room, moved);
        carriedStack.add(moved);
        output.subtract(moved);
        if (output.getCount() <= 0) {
            furnaceState.setOutput(null);
        }
    }

    private ItemStack returnStackToInventory(Inventory inventory, ItemStack stack) {
        if (stack == null || stack.getCount() <= 0) {
            return null;
        }
        return inventory.addStack(stack);
    }

    private boolean isWritableSlot(Inventory inventory, int slotIndex) {
        return slotIndex >= 0 && slotIndex < inventory.getTotalSize()
            || FurnaceLayout.isInputSlot(slotIndex)
            || FurnaceLayout.isFuelSlot(slotIndex);
    }

    private ItemStack getSlot(Inventory inventory, FurnaceState furnaceState, int slotIndex) {
        if (slotIndex >= 0 && slotIndex < inventory.getTotalSize()) {
            return inventory.getSlot(slotIndex);
        }
        if (FurnaceLayout.isInputSlot(slotIndex)) {
            return furnaceState.getInput();
        }
        if (FurnaceLayout.isFuelSlot(slotIndex)) {
            return furnaceState.getFuel();
        }
        return null;
    }

    private void setSlot(Inventory inventory, FurnaceState furnaceState, int slotIndex, ItemStack stack) {
        if (slotIndex >= 0 && slotIndex < inventory.getTotalSize()) {
            inventory.setSlot(slotIndex, stack);
            return;
        }
        if (FurnaceLayout.isInputSlot(slotIndex)) {
            furnaceState.setInput(stack);
        } else if (FurnaceLayout.isFuelSlot(slotIndex)) {
            furnaceState.setFuel(stack);
        }
    }
}
