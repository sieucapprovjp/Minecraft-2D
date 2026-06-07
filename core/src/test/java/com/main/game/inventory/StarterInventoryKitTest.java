package com.main.game.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StarterInventoryKitTest {

    @Test
    public void noChestGrantsNoItems() {
        Inventory inventory = new Inventory();

        StarterInventoryKit.grant(inventory, StarterInventoryKit.NO_CHEST);

        for (int i = 0; i < inventory.getTotalSize(); i++) {
            assertNull(inventory.getSlot(i));
        }
    }

    @Test
    public void chestGrantsIronArmorIronToolsAndSteak() {
        Inventory inventory = new Inventory();

        StarterInventoryKit.grant(inventory, StarterInventoryKit.CHEST);

        assertTool(inventory, "iron_pickaxe");
        assertTool(inventory, "iron_axe");
        assertTool(inventory, "iron_shovel");
        assertTool(inventory, "iron_sword");
        assertArmor(inventory, "iron_helmet", ArmorSlot.HELMET);
        assertArmor(inventory, "iron_chestplate", ArmorSlot.CHESTPLATE);
        assertArmor(inventory, "iron_leggings", ArmorSlot.LEGGINGS);
        assertArmor(inventory, "iron_boots", ArmorSlot.BOOTS);
        assertEquals(8, inventory.countItem("cooked_beef"));
    }

    @Test
    public void largeChestGrantsNetheriteGearAndGoldenApples() {
        Inventory inventory = new Inventory();

        StarterInventoryKit.grant(inventory, StarterInventoryKit.LARGE_CHEST);

        assertTool(inventory, "netherite_pickaxe");
        assertTool(inventory, "netherite_axe");
        assertTool(inventory, "netherite_shovel");
        assertTool(inventory, "netherite_sword");
        assertArmor(inventory, "netherite_helmet", ArmorSlot.HELMET);
        assertArmor(inventory, "netherite_chestplate", ArmorSlot.CHESTPLATE);
        assertArmor(inventory, "netherite_leggings", ArmorSlot.LEGGINGS);
        assertArmor(inventory, "netherite_boots", ArmorSlot.BOOTS);
        assertEquals(64, inventory.countItem("golden_apple"));
    }

    @Test
    public void legacyGrantDefaultsToNoChest() {
        Inventory inventory = new Inventory();

        StarterInventoryKit.grant(inventory);

        for (int i = 0; i < inventory.getTotalSize(); i++) {
            assertNull(inventory.getSlot(i));
        }
    }

    private void assertTool(Inventory inventory, String itemId) {
        ItemStack stack = findStack(inventory, itemId);
        assertNotNull(itemId, stack);
        assertEquals(1, stack.getCount());
        assertTrue(ToolRegistry.isTool(itemId));
    }

    private void assertArmor(Inventory inventory, String itemId, ArmorSlot slot) {
        ItemStack stack = findStack(inventory, itemId);
        assertNotNull(itemId, stack);
        assertEquals(1, stack.getCount());
        assertTrue(ArmorRegistry.isArmor(itemId));
        assertEquals(slot, ArmorRegistry.getSlot(itemId));
    }

    private ItemStack findStack(Inventory inventory, String itemId) {
        for (int i = 0; i < inventory.getTotalSize(); i++) {
            ItemStack stack = inventory.getSlot(i);
            if (stack != null && itemId.equals(stack.getItemId())) {
                return stack;
            }
        }
        return null;
    }
}
