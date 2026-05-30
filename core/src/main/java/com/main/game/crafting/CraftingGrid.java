package com.main.game.crafting;

import com.main.game.inventory.ItemStack;

public class CraftingGrid {

    public static final int SIZE = 4;

    private final ItemStack[] slots = new ItemStack[SIZE];

    public ItemStack getSlot(int index) {
        if (index < 0 || index >= SIZE) {
            return null;
        }
        return slots[index];
    }

    public void setSlot(int index, ItemStack stack) {
        if (index < 0 || index >= SIZE) {
            return;
        }
        if (stack == null || stack.getCount() <= 0) {
            slots[index] = null;
            return;
        }
        slots[index] = stack;
    }
}
