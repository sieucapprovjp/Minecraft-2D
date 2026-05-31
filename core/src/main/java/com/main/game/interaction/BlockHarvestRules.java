package com.main.game.interaction;

import com.main.game.inventory.ToolRegistry;

import java.util.Map;

public final class BlockHarvestRules {

    private static final Map<String, Integer> PICKAXE_DROP_LEVELS = Map.ofEntries(
        Map.entry("stone", 1),
        Map.entry("deepslate", 1),
        Map.entry("sandstone", 1),
        Map.entry("furnace", 1),
        Map.entry("coal_ore", 1),
        Map.entry("deepslate_co", 1),
        Map.entry("copper_ore", 1),
        Map.entry("deepslate_copper", 1),
        Map.entry("iron_ore", 2),
        Map.entry("deepslate_io", 2),
        Map.entry("gold_ore", 2),
        Map.entry("deepslate_go", 2),
        Map.entry("lapis_ore", 2),
        Map.entry("ore_lapis_deepslate", 2),
        Map.entry("redstone_ore", 2),
        Map.entry("deepslate_ro", 2),
        Map.entry("diamond_ore", 3),
        Map.entry("deepslate_do", 3),
        Map.entry("emerald_ore", 3),
        Map.entry("deepslate_eo", 3)
    );

    private BlockHarvestRules() {
    }

    public static boolean canDrop(String blockId, String heldItemId) {
        Integer requiredLevel = PICKAXE_DROP_LEVELS.get(blockId);
        if (requiredLevel == null) {
            return true;
        }
        return ToolRegistry.isPickaxe(heldItemId)
            && ToolRegistry.getHarvestLevel(heldItemId) >= requiredLevel;
    }
}
