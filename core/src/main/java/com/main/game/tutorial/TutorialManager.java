package com.main.game.tutorial;

import com.main.game.blocks.AbstractBlock;

/**
 * Tracks the six tutorial milestones described in doc/TUTORIAL_DESIGN.md.
 * The current step is shown first, then completing its gameplay condition
 * unlocks the next step popup.
 */
public class TutorialManager {

    public static final int MAX_STEPS = 6;

    private int currentStep;
    private boolean popupVisible;
    private boolean raidVictoryAchieved;

    public TutorialManager() {
        currentStep = 1;
        popupVisible = true;
        raidVictoryAchieved = false;
    }

    public void onBlockBroken(AbstractBlock block) {
        if (isComplete() || block == null) {
            return;
        }
        String id = block.getBlockId();
        if (currentStep == 1 && isWoodLog(id)) {
            advanceStep();
        } else if (currentStep == 5 && (isStoneBlock(id) || isOreBlock(id))) {
            advanceStep();
        }
    }

    public void onCraftingCompleted(String outputItemId, int count) {
        if (isComplete() || outputItemId == null) {
            return;
        }
        if (currentStep == 2 && "stick".equals(outputItemId) && count >= 4) {
            advanceStep();
        } else if (currentStep == 4 && "wood_pickaxe".equals(outputItemId)) {
            advanceStep();
        }
    }

    public void onBlockPlaced(String blockId, int tileX, int tileY) {
        if (isComplete() || blockId == null) {
            return;
        }
        if (currentStep == 3 && "crafting_table".equals(blockId)) {
            advanceStep();
        }
    }

    public void onRaidVictory() {
        if (!isComplete() && currentStep == 6) {
            raidVictoryAchieved = true;
            popupVisible = true;
        }
    }

    public void dismissPopup() {
        popupVisible = false;
        if (currentStep == MAX_STEPS && raidVictoryAchieved) {
            currentStep++;
        }
    }

    public boolean isShowing() {
        return popupVisible && !isComplete();
    }

    public int getActiveStep() {
        return isComplete() ? 0 : currentStep;
    }

    public boolean isComplete() {
        return currentStep > MAX_STEPS;
    }

    private void advanceStep() {
        if (currentStep >= MAX_STEPS) {
            return;
        }
        currentStep++;
        popupVisible = true;
    }

    private static boolean isWoodLog(String id) {
        return "wood".equals(id)
            || "natural_wood".equals(id)
            || "spruce_log".equals(id)
            || "natural_spruce_log".equals(id)
            || "cherry_log".equals(id)
            || "natural_cherry_log".equals(id);
    }

    private static boolean isStoneBlock(String id) {
        return "stone".equals(id)
            || "sandstone".equals(id)
            || "deepslate".equals(id);
    }

    private static boolean isOreBlock(String id) {
        return id != null && (id.endsWith("_ore")
            || "nether_quartz".equals(id)
            || "ancient_debris".equals(id));
    }
}
