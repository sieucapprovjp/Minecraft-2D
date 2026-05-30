package com.main.game.crafting;

import com.main.game.inventory.Inventory;
import com.main.game.inventory.ItemRegistry;
import com.main.game.inventory.ItemStack;

public class CraftingController {

    private final CraftingGrid grid;

    public CraftingController() {
        this(new CraftingGrid());
    }

    public CraftingController(CraftingGrid grid) {
        this.grid = grid;
    }

    public CraftingGrid getGrid() {
        return grid;
    }

    public ItemStack getResult() {
        CraftingMatch match = RecipeRegistry.findMatch(grid);
        if (match == null) {
            return null;
        }
        return new ItemStack(match.getRecipe().getOutputItemId(), match.getTotalOutputCount());
    }

    public ItemStack takeResult(ItemStack carriedStack) {
        CraftingMatch match = RecipeRegistry.findMatch(grid);
        if (match == null) {
            return carriedStack;
        }

        CraftingRecipe recipe = match.getRecipe();
        int craftsToTake = match.getCraftCount();
        if (carriedStack != null) {
            if (!recipe.getOutputItemId().equals(carriedStack.getItemId())) {
                return carriedStack;
            }
            int room = ItemRegistry.getMaxStack(carriedStack.getItemId()) - carriedStack.getCount();
            craftsToTake = Math.min(craftsToTake, room / recipe.getOutputCount());
            if (craftsToTake <= 0) {
                return carriedStack;
            }
            carriedStack.add(craftsToTake * recipe.getOutputCount());
            consume(match, craftsToTake);
            return carriedStack;
        }

        consume(match, craftsToTake);
        return new ItemStack(recipe.getOutputItemId(), craftsToTake * recipe.getOutputCount());
    }

    public void returnInputsToInventory(Inventory inventory) {
        if (inventory == null) {
            return;
        }
        for (int i = 0; i < CraftingGrid.SIZE; i++) {
            ItemStack stack = grid.getSlot(i);
            if (stack == null || stack.getCount() <= 0) {
                grid.setSlot(i, null);
                continue;
            }
            ItemStack remaining = inventory.addStack(stack);
            grid.setSlot(i, remaining);
        }
    }

    private void consume(CraftingMatch match, int craftCount) {
        for (int slot : match.getIngredientSlots()) {
            ItemStack stack = grid.getSlot(slot);
            if (stack == null) {
                continue;
            }
            stack.subtract(craftCount);
            if (stack.getCount() <= 0) {
                grid.setSlot(slot, null);
            }
        }
    }
}
