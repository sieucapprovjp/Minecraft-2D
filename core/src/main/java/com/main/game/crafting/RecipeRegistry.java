package com.main.game.crafting;

import java.util.List;

public final class RecipeRegistry {

    private static final List<CraftingRecipe> RECIPES = List.of(
        CraftingRecipe.shapeless(
            "wood planks",
            new String[] {"wood"},
            "planks",
            4
        ),
        CraftingRecipe.shaped(
            "sticks",
            1,
            2,
            new String[] {"planks", "planks"},
            "stick",
            4
        ),
        CraftingRecipe.shaped(
            "crafting table",
            new String[] {"planks", "planks", "planks", "planks"},
            "crafting_table",
            1
        ),
        CraftingRecipe.shaped(
            "wood pickaxe",
            3,
            3,
            new String[] {"planks", "planks", "planks", null, "stick", null, null, "stick", null},
            "wood_pickaxe",
            1
        ),
        CraftingRecipe.shaped(
            "wood axe",
            2,
            3,
            new String[] {"planks", "planks", "planks", "stick", null, "stick"},
            "wood_axe",
            1
        ),
        CraftingRecipe.shaped(
            "wood shovel",
            1,
            3,
            new String[] {"planks", "stick", "stick"},
            "wood_shovel",
            1
        ),
        CraftingRecipe.shaped(
            "wood sword",
            1,
            3,
            new String[] {"planks", "planks", "stick"},
            "wood_sword",
            1
        ),
        CraftingRecipe.shaped(
            "wood hoe",
            2,
            3,
            new String[] {"planks", "planks", null, "stick", null, "stick"},
            "wood_hoe",
            1
        ),
        CraftingRecipe.shaped(
            "stone pickaxe",
            3,
            3,
            new String[] {"cobblestone", "cobblestone", "cobblestone", null, "stick", null, null, "stick", null},
            "stone_pickaxe",
            1
        ),
        CraftingRecipe.shaped(
            "stone axe",
            2,
            3,
            new String[] {"cobblestone", "cobblestone", "cobblestone", "stick", null, "stick"},
            "stone_axe",
            1
        ),
        CraftingRecipe.shaped(
            "stone shovel",
            1,
            3,
            new String[] {"cobblestone", "stick", "stick"},
            "stone_shovel",
            1
        ),
        CraftingRecipe.shaped(
            "stone sword",
            1,
            3,
            new String[] {"cobblestone", "cobblestone", "stick"},
            "stone_sword",
            1
        ),
        CraftingRecipe.shaped(
            "stone hoe",
            2,
            3,
            new String[] {"cobblestone", "cobblestone", null, "stick", null, "stick"},
            "stone_hoe",
            1
        ),
        CraftingRecipe.shaped(
            "furnace",
            3,
            3,
            new String[] {
                "cobblestone", "cobblestone", "cobblestone",
                "cobblestone", null, "cobblestone",
                "cobblestone", "cobblestone", "cobblestone"
            },
            "furnace",
            1
        ),
        CraftingRecipe.shaped(
            "chest",
            3,
            3,
            new String[] {
                "planks", "planks", "planks",
                "planks", null, "planks",
                "planks", "planks", "planks"
            },
            "chest",
            1
        ),
        CraftingRecipe.shaped(
            "copper pickaxe",
            3,
            3,
            new String[] {"copper_ingot", "copper_ingot", "copper_ingot", null, "stick", null, null, "stick", null},
            "copper_pickaxe",
            1
        ),
        CraftingRecipe.shaped(
            "copper axe",
            2,
            3,
            new String[] {"copper_ingot", "copper_ingot", "copper_ingot", "stick", null, "stick"},
            "copper_axe",
            1
        ),
        CraftingRecipe.shaped(
            "copper shovel",
            1,
            3,
            new String[] {"copper_ingot", "stick", "stick"},
            "copper_shovel",
            1
        ),
        CraftingRecipe.shaped(
            "copper sword",
            1,
            3,
            new String[] {"copper_ingot", "copper_ingot", "stick"},
            "copper_sword",
            1
        ),
        CraftingRecipe.shaped(
            "copper hoe",
            2,
            3,
            new String[] {"copper_ingot", "copper_ingot", null, "stick", null, "stick"},
            "copper_hoe",
            1
        ),
        CraftingRecipe.shaped(
            "iron pickaxe",
            3,
            3,
            new String[] {"iron_ingot", "iron_ingot", "iron_ingot", null, "stick", null, null, "stick", null},
            "iron_pickaxe",
            1
        ),
        CraftingRecipe.shaped(
            "iron axe",
            2,
            3,
            new String[] {"iron_ingot", "iron_ingot", "iron_ingot", "stick", null, "stick"},
            "iron_axe",
            1
        ),
        CraftingRecipe.shaped(
            "iron shovel",
            1,
            3,
            new String[] {"iron_ingot", "stick", "stick"},
            "iron_shovel",
            1
        ),
        CraftingRecipe.shaped(
            "iron sword",
            1,
            3,
            new String[] {"iron_ingot", "iron_ingot", "stick"},
            "iron_sword",
            1
        ),
        CraftingRecipe.shaped(
            "iron hoe",
            2,
            3,
            new String[] {"iron_ingot", "iron_ingot", null, "stick", null, "stick"},
            "iron_hoe",
            1
        ),
        CraftingRecipe.shaped(
            "gold pickaxe",
            3,
            3,
            new String[] {"gold_ingot", "gold_ingot", "gold_ingot", null, "stick", null, null, "stick", null},
            "gold_pickaxe",
            1
        ),
        CraftingRecipe.shaped(
            "gold axe",
            2,
            3,
            new String[] {"gold_ingot", "gold_ingot", "gold_ingot", "stick", null, "stick"},
            "gold_axe",
            1
        ),
        CraftingRecipe.shaped(
            "gold shovel",
            1,
            3,
            new String[] {"gold_ingot", "stick", "stick"},
            "gold_shovel",
            1
        ),
        CraftingRecipe.shaped(
            "gold sword",
            1,
            3,
            new String[] {"gold_ingot", "gold_ingot", "stick"},
            "gold_sword",
            1
        ),
        CraftingRecipe.shaped(
            "gold hoe",
            2,
            3,
            new String[] {"gold_ingot", "gold_ingot", null, "stick", null, "stick"},
            "gold_hoe",
            1
        ),
        CraftingRecipe.shaped(
            "diamond pickaxe",
            3,
            3,
            new String[] {"diamond", "diamond", "diamond", null, "stick", null, null, "stick", null},
            "diamond_pickaxe",
            1
        ),
        CraftingRecipe.shaped(
            "diamond axe",
            2,
            3,
            new String[] {"diamond", "diamond", "diamond", "stick", null, "stick"},
            "diamond_axe",
            1
        ),
        CraftingRecipe.shaped(
            "diamond shovel",
            1,
            3,
            new String[] {"diamond", "stick", "stick"},
            "diamond_shovel",
            1
        ),
        CraftingRecipe.shaped(
            "diamond sword",
            1,
            3,
            new String[] {"diamond", "diamond", "stick"},
            "diamond_sword",
            1
        ),
        CraftingRecipe.shaped(
            "diamond hoe",
            2,
            3,
            new String[] {"diamond", "diamond", null, "stick", null, "stick"},
            "diamond_hoe",
            1
        )
    );

    private RecipeRegistry() {
    }

    static CraftingMatch findMatch(CraftingGrid grid) {
        if (grid == null) {
            return null;
        }
        for (CraftingRecipe recipe : RECIPES) {
            CraftingMatch match = recipe.match(grid);
            if (match != null) {
                return match;
            }
        }
        return null;
    }
}
