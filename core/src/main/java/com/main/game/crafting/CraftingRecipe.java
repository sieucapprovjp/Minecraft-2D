package com.main.game.crafting;

import com.main.game.inventory.ItemRegistry;
import com.main.game.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CraftingRecipe {

    public enum Type {
        NORMAL,
        SHAPELESS
    }

    private final String name;
    private final Type type;
    private final String[] input;
    private final String outputItemId;
    private final int outputCount;

    private CraftingRecipe(String name, Type type, String[] input, String outputItemId, int outputCount) {
        this.name = name;
        this.type = type;
        this.input = input;
        this.outputItemId = outputItemId;
        this.outputCount = outputCount;
    }

    public static CraftingRecipe shaped(String name, String[] pattern, String outputItemId, int outputCount) {
        if (pattern == null || pattern.length != CraftingGrid.SIZE) {
            throw new IllegalArgumentException("2x2 shaped recipes require exactly 4 input slots.");
        }
        return new CraftingRecipe(name, Type.NORMAL, Arrays.copyOf(pattern, pattern.length), outputItemId, outputCount);
    }

    public static CraftingRecipe shapeless(String name, String[] ingredients, String outputItemId, int outputCount) {
        if (ingredients == null || ingredients.length == 0 || ingredients.length > CraftingGrid.SIZE) {
            throw new IllegalArgumentException("2x2 shapeless recipes require 1-4 ingredients.");
        }
        return new CraftingRecipe(name, Type.SHAPELESS, Arrays.copyOf(ingredients, ingredients.length), outputItemId, outputCount);
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public String getOutputItemId() {
        return outputItemId;
    }

    public int getOutputCount() {
        return outputCount;
    }

    CraftingMatch match(CraftingGrid grid) {
        if (type == Type.SHAPELESS) {
            return matchShapeless(grid);
        }
        return matchShaped(grid);
    }

    private CraftingMatch matchShaped(CraftingGrid grid) {
        SlotRef[] normalized = normalize(readSlots(grid));
        int[] slots = matchedSlots(normalized, input);
        if (slots == null) {
            slots = matchedSlots(mirror(normalized), input);
        }
        return createMatch(grid, slots);
    }

    private CraftingMatch matchShapeless(CraftingGrid grid) {
        List<Integer> remainingSlots = new ArrayList<>();
        for (int i = 0; i < CraftingGrid.SIZE; i++) {
            ItemStack stack = grid.getSlot(i);
            if (stack != null && stack.getCount() > 0) {
                remainingSlots.add(i);
            }
        }
        if (remainingSlots.size() != input.length) {
            return null;
        }

        int[] matchedSlots = new int[input.length];
        boolean[] used = new boolean[remainingSlots.size()];
        for (int i = 0; i < input.length; i++) {
            boolean matched = false;
            for (int slotIndex = 0; slotIndex < remainingSlots.size(); slotIndex++) {
                if (used[slotIndex]) {
                    continue;
                }
                int gridSlot = remainingSlots.get(slotIndex);
                ItemStack stack = grid.getSlot(gridSlot);
                if (stack != null && input[i].equals(stack.getItemId())) {
                    matchedSlots[i] = gridSlot;
                    used[slotIndex] = true;
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return null;
            }
        }
        return createMatch(grid, matchedSlots);
    }

    private CraftingMatch createMatch(CraftingGrid grid, int[] ingredientSlots) {
        if (ingredientSlots == null || outputCount <= 0 || outputItemId == null) {
            return null;
        }

        int ingredientCrafts = Integer.MAX_VALUE;
        for (int slot : ingredientSlots) {
            ItemStack stack = grid.getSlot(slot);
            if (stack == null || stack.getCount() <= 0) {
                return null;
            }
            ingredientCrafts = Math.min(ingredientCrafts, stack.getCount());
        }

        int maxOutputStack = ItemRegistry.getMaxStack(outputItemId);
        int outputCrafts = maxOutputStack / outputCount;
        int craftCount = Math.min(ingredientCrafts, outputCrafts);
        if (craftCount <= 0) {
            return null;
        }
        return new CraftingMatch(this, ingredientSlots, craftCount);
    }

    private int[] matchedSlots(SlotRef[] candidate, String[] pattern) {
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < CraftingGrid.SIZE; i++) {
            String expected = pattern[i];
            String actual = candidate[i] == null ? null : candidate[i].itemId;
            if (expected == null) {
                if (actual != null) {
                    return null;
                }
                continue;
            }
            if (!expected.equals(actual)) {
                return null;
            }
            slots.add(candidate[i].slotIndex);
        }
        int[] result = new int[slots.size()];
        for (int i = 0; i < slots.size(); i++) {
            result[i] = slots.get(i);
        }
        return result;
    }

    private SlotRef[] readSlots(CraftingGrid grid) {
        SlotRef[] slots = new SlotRef[CraftingGrid.SIZE];
        for (int i = 0; i < CraftingGrid.SIZE; i++) {
            ItemStack stack = grid.getSlot(i);
            if (stack != null && stack.getCount() > 0) {
                slots[i] = new SlotRef(stack.getItemId(), i);
            }
        }
        return slots;
    }

    private SlotRef[] normalize(SlotRef[] source) {
        SlotRef[] normalized = Arrays.copyOf(source, source.length);
        boolean topRowEmpty = normalized[0] == null && normalized[1] == null;
        boolean bottomRowHasItem = normalized[2] != null || normalized[3] != null;
        if (topRowEmpty && bottomRowHasItem) {
            normalized[0] = normalized[2];
            normalized[1] = normalized[3];
            normalized[2] = null;
            normalized[3] = null;
        }

        boolean leftColumnEmpty = normalized[0] == null && normalized[2] == null;
        boolean rightColumnHasItem = normalized[1] != null || normalized[3] != null;
        if (leftColumnEmpty && rightColumnHasItem) {
            normalized[0] = normalized[1];
            normalized[2] = normalized[3];
            normalized[1] = null;
            normalized[3] = null;
        }
        return normalized;
    }

    private SlotRef[] mirror(SlotRef[] source) {
        return new SlotRef[] {source[1], source[0], source[3], source[2]};
    }

    private static final class SlotRef {
        private final String itemId;
        private final int slotIndex;

        private SlotRef(String itemId, int slotIndex) {
            this.itemId = itemId;
            this.slotIndex = slotIndex;
        }
    }
}
