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
