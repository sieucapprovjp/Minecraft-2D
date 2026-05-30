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
            new String[] {"planks", null, "planks", null},
            "stick",
            4
        ),
        CraftingRecipe.shaped(
            "crafting table",
            new String[] {"planks", "planks", "planks", "planks"},
            "crafting_table",
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
