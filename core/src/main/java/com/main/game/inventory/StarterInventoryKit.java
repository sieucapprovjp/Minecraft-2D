package com.main.game.inventory;

public final class StarterInventoryKit {

    private static final int STARTER_FOOD_COUNT = 8;
    private static final int STARTER_EMERALD_COUNT = 64;
    private static final String[] STARTER_TOOLS = {
        "netherite_sword"
    };
    private static final String[] STARTER_ARMOR = {
        "netherite_helmet",
        "netherite_chestplate",
        "netherite_leggings",
        "netherite_boots"
    };
    private static final String[] STARTER_BLOCKS = {
        "raid_banner",
        "jukebox"
    };
    private static final String[] STARTER_MUSIC_DISCS = {
        "pigstep",
        "lava_chicken"
    };

    private StarterInventoryKit() {
    }

    public static void grant(Inventory inventory) {
        if (inventory == null) {
            return;
        }
        for (String itemId : STARTER_TOOLS) {
            inventory.add(itemId, 1);
        }
        for (String itemId : STARTER_ARMOR) {
            inventory.add(itemId, 1);
        }
        for (String itemId : STARTER_BLOCKS) {
            inventory.add(itemId, 1);
        }
        for (String itemId : STARTER_MUSIC_DISCS) {
            inventory.add(itemId, 1);
        }
        inventory.add("emerald", STARTER_EMERALD_COUNT);
        for (String itemId : FoodRegistry.getFoodItemIds()) {
            inventory.add(itemId, STARTER_FOOD_COUNT);
        }
    }
}
