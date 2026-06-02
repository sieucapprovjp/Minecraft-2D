package com.main.game.inventory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InventoryTransactionTest {

    @Test
    public void countsAndRemovesItemsAcrossStacks() {
        Inventory inventory = new Inventory();
        inventory.setSlot(0, new ItemStack("emerald", 40));
        inventory.setSlot(1, new ItemStack("emerald", 25));

        assertEquals(65, inventory.countItem("emerald"));
        assertTrue(inventory.remove("emerald", 64));
        assertEquals(1, inventory.countItem("emerald"));
    }

    @Test
    public void removeFailsWithoutChangingInventoryWhenMissingItems() {
        Inventory inventory = new Inventory();
        inventory.setSlot(0, new ItemStack("coal", 8));

        assertFalse(inventory.remove("coal", 15));
        assertEquals(8, inventory.countItem("coal"));
    }

    @Test
    public void durableItemNeedsEmptySlot() {
        Inventory inventory = new Inventory();
        for (int i = 0; i < Inventory.TOTAL_SIZE; i++) {
            inventory.setSlot(i, new ItemStack("dirt", 1));
        }

        assertFalse(inventory.canAdd(new ItemStack("iron_axe", 1)));
    }
}
