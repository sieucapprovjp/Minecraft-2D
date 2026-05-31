package com.main.game.inventory;


public final class StarterArmorKit {

    private static final String[] STARTER_ARMOR = {
        "leather_cap", "leather_chestplate", "leather_pants", "leather_boots",
        "copper_helmet", "copper_chestplate", "copper_leggings", "copper_boots",
        "iron_helmet", "iron_chestplate", "iron_leggings", "iron_boots",
        "gold_helmet", "gold_chestplate", "gold_leggings", "gold_boots",
        "diamond_helmet", "diamond_chestplate", "diamond_leggings", "diamond_boots",
        "netherite_helmet", "netherite_chestplate", "netherite_leggings", "netherite_boots"
    };

    private StarterArmorKit() {
    }

    public static void grantAllArmor(Inventory inventory) {
        if (inventory == null) {
            return;
        }
        for (String itemId : STARTER_ARMOR) {
            inventory.add(itemId, 1);
        }
    }
}
