package com.main.game.crafting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.main.game.blocks.metadata.BlockRegistry;
import com.main.game.inventory.ItemStack;
import com.main.game.inventory.ToolRegistry;
import com.main.game.utilityblock.furnace.FuelRegistry;
import org.junit.Test;

public class RecipeRegistryTest {

    @Test
    public void cherryLogCraftsIntoCherryPlanks() {
        CraftingGrid grid = new CraftingGrid();
        grid.setSlot(0, new ItemStack("cherry_log", 1));

        CraftingMatch match = RecipeRegistry.findMatch(grid);

        assertNotNull(match);
        assertEquals("cherry_planks", match.getRecipe().getOutputItemId());
        assertEquals(4, match.getTotalOutputCount());
        assertTrue(BlockRegistry.isPlaceable("cherry_planks"));
        assertTrue(BlockRegistry.isSolid("cherry_planks"));
        assertTrue(ToolRegistry.getMiningMultiplier("wood_axe", "cherry_log") > 1f);
        assertTrue(ToolRegistry.getMiningMultiplier("wood_axe", "cherry_planks") > 1f);
        assertEquals(15f, FuelRegistry.getBurnSeconds("cherry_log"), 0.001f);
        assertEquals(15f, FuelRegistry.getBurnSeconds("cherry_planks"), 0.001f);
    }

    @Test
    public void spruceLogCraftsIntoSprucePlanks() {
        CraftingGrid grid = new CraftingGrid();
        grid.setSlot(0, new ItemStack("spruce_log", 1));

        CraftingMatch match = RecipeRegistry.findMatch(grid);

        assertNotNull(match);
        assertEquals("spruce_planks", match.getRecipe().getOutputItemId());
        assertEquals(4, match.getTotalOutputCount());
        assertTrue(BlockRegistry.isPlaceable("spruce_planks"));
        assertTrue(BlockRegistry.isSolid("spruce_planks"));
        assertTrue(ToolRegistry.getMiningMultiplier("wood_axe", "spruce_log") > 1f);
        assertTrue(ToolRegistry.getMiningMultiplier("wood_axe", "spruce_planks") > 1f);
        assertEquals(15f, FuelRegistry.getBurnSeconds("spruce_log"), 0.001f);
        assertEquals(15f, FuelRegistry.getBurnSeconds("spruce_planks"), 0.001f);
    }

    @Test
    public void cherryPlanksCanCraftSharedWoodRecipes() {
        assertRecipeWithPlanks("cherry_planks", new int[] {0, 3}, "stick", 4);
        assertRecipeWithPlanks("cherry_planks", new int[] {0, 1, 3, 4}, "crafting_table", 1);
        assertRecipeWithPlanks("cherry_planks", new int[] {0, 1, 2}, "wood_pickaxe", 1);
        assertRecipeWithPlanks("cherry_planks", new int[] {0, 1, 2, 3, 5, 6, 7, 8}, "chest", 1);
    }

    @Test
    public void sprucePlanksCanCraftSharedWoodRecipes() {
        assertRecipeWithPlanks("spruce_planks", new int[] {0, 3}, "stick", 4);
        assertRecipeWithPlanks("spruce_planks", new int[] {0, 1, 3, 4}, "crafting_table", 1);
        assertRecipeWithPlanks("spruce_planks", new int[] {0, 1, 2}, "wood_pickaxe", 1);
        assertRecipeWithPlanks("spruce_planks", new int[] {0, 1, 2, 3, 5, 6, 7, 8}, "chest", 1);
    }

    @Test
    public void partialPlankRecipeDoesNotCrashWhenMatchedAgainstEmptySlots() {
        CraftingGrid grid = new CraftingGrid(CraftingMode.TABLE_3X3);
        grid.setSlot(0, new ItemStack("cherry_planks", 1));

        assertNull(RecipeRegistry.findMatch(grid));
    }

    private void assertRecipeWithPlanks(String plankItemId, int[] plankSlots, String outputItemId, int outputCount) {
        CraftingGrid grid = new CraftingGrid(CraftingMode.TABLE_3X3);
        for (int slot : plankSlots) {
            grid.setSlot(slot, new ItemStack(plankItemId, 1));
        }
        if ("wood_pickaxe".equals(outputItemId)) {
            grid.setSlot(4, new ItemStack("stick", 1));
            grid.setSlot(7, new ItemStack("stick", 1));
        }

        CraftingMatch match = RecipeRegistry.findMatch(grid);

        assertNotNull(match);
        assertEquals(outputItemId, match.getRecipe().getOutputItemId());
        assertEquals(outputCount, match.getTotalOutputCount());
    }
}
