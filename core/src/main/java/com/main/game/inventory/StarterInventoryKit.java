package com.main.game.inventory;

public final class StarterInventoryKit {

    public static final int NO_CHEST = 0;
    public static final int CHEST = 1;
    public static final int LARGE_CHEST = 2;

    private static final String[] IRON_TOOLS = {
        "iron_pickaxe",
        "iron_axe",
        "iron_shovel",
        "iron_sword"
    };

    private static final String[] IRON_ARMOR = {
        "iron_helmet",
        "iron_chestplate",
        "iron_leggings",
        "iron_boots"
    };

    private static final String[] NETHERITE_TOOLS = {
        "netherite_pickaxe",
        "netherite_axe",
        "netherite_shovel",
        "netherite_sword"
    };

    private static final String[] NETHERITE_ARMOR = {
        "netherite_helmet",
        "netherite_chestplate",
        "netherite_leggings",
        "netherite_boots"
    };

    private StarterInventoryKit() {
    }

    public static void grant(Inventory inventory) {
        grant(inventory, NO_CHEST);
    }

    public static void grant(Inventory inventory, int bonusChest) {
        if (inventory == null) {
            return;
        }

        if (bonusChest == CHEST) {
            grantSet(inventory, IRON_TOOLS);
            grantSet(inventory, IRON_ARMOR);
            inventory.add("cooked_beef", 8);
            return;
        }

        if (bonusChest == LARGE_CHEST) {
            grantSet(inventory, NETHERITE_TOOLS);
            grantSet(inventory, NETHERITE_ARMOR);
            inventory.add("golden_apple", 64);
        }
    }

    private static void grantSet(Inventory inventory, String[] itemIds) {
        for (String itemId : itemIds) {
            inventory.add(itemId, 1);
        }
    }
}
