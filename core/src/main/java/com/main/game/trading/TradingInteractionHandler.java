package com.main.game.trading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.main.game.inventory.Inventory;
import com.main.game.inventory.ItemSlotAccess;
import com.main.game.inventory.ItemSlotInteractionController;
import com.main.game.inventory.ItemStack;

public final class TradingInteractionHandler {

    private final ItemSlotInteractionController slotInteraction = new ItemSlotInteractionController();

    public void update(Inventory inventory, TradingController controller, TradingRenderer renderer) {
        if (inventory == null || controller == null || renderer == null || !controller.isOpen()) {
            return;
        }

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            int offer = renderer.findHoveredOffer(mouseX, mouseY, controller);
            if (offer >= 0) {
                controller.selectOffer(offer);
                controller.executeSelected(inventory);
                return;
            }
        }

        int slot = renderer.findHoveredInventorySlot(mouseX, mouseY);
        if (slot < 0) {
            return;
        }

        TradingSlotAccess slotAccess = new TradingSlotAccess(inventory);
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            slotInteraction.onLeftClick(slotAccess, slot);
        } else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            slotInteraction.onRightClick(slotAccess, slot);
        }
    }

    public void onCloseInventory(Inventory inventory) {
        slotInteraction.returnCarriedStackToInventory(inventory);
    }

    public ItemStack getCarriedStack() {
        return slotInteraction.getCarriedStack();
    }

    private static final class TradingSlotAccess implements ItemSlotAccess {
        private final Inventory inventory;

        private TradingSlotAccess(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public boolean isWritableSlot(int slotIndex) {
            return slotIndex >= 0 && slotIndex < inventory.getTotalSize();
        }

        @Override
        public ItemStack getSlot(int slotIndex) {
            return slotIndex >= 0 && slotIndex < inventory.getTotalSize()
                ? inventory.getSlot(slotIndex)
                : null;
        }

        @Override
        public void setSlot(int slotIndex, ItemStack stack) {
            if (slotIndex >= 0 && slotIndex < inventory.getTotalSize()) {
                inventory.setSlot(slotIndex, stack);
            }
        }
    }
}
