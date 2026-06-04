package com.main.game.tutorial;

import com.main.game.blocks.AbstractBlock;

/**
 * State machine quản lý 6 bước hướng dẫn (tutorial) cho người chơi.
 * Mỗi bước chỉ xuất hiện sau khi bước trước được hoàn thành.
 * Không phải singleton — được sở hữu bởi GameScreen.
 */
public class TutorialManager {

    public static final int MAX_STEPS = 6;

    private int activeStep; // 0 = không có popup, 1-6 = bước đang hiển thị
    private int nextStep;   // 1-6 = bước đang chờ trigger, 7 = hoàn thành

    public TutorialManager() {
        this.activeStep = 0;
        this.nextStep = 1;
    }

    // ─── Event Handlers ─────────────────────────────

    /** Gọi khi player phá bất kỳ block nào. Kiểm tra step 1 (chặt gỗ) và step 5 (khai thác). */
    public void onBlockBroken(AbstractBlock block) {
        if (activeStep > 0 || nextStep > MAX_STEPS) return;
        if (block == null) return;
        String id = block.getBlockId();
        if (nextStep == 1 && isWoodLog(id)) {
            activeStep = 1;
        } else if (nextStep == 5 && (isStoneBlock(id) || isOreBlock(id))) {
            activeStep = 5;
        }
    }

    /** Gọi khi player chế tạo item. Kiểm tra step 2 (que) và step 4 (cúp gỗ). */
    public void onCraftingCompleted(String outputItemId, int count) {
        if (activeStep > 0 || nextStep > MAX_STEPS || outputItemId == null) return;
        if (nextStep == 2 && "stick".equals(outputItemId)) {
            activeStep = 2;
        } else if (nextStep == 4 && "wood_pickaxe".equals(outputItemId)) {
            activeStep = 4;
        }
    }

    /** Gọi khi player đặt block. Kiểm tra step 3 (bàn chế tạo). */
    public void onBlockPlaced(String blockId, int tileX, int tileY) {
        if (activeStep > 0 || nextStep > MAX_STEPS || blockId == null) return;
        if (nextStep == 3 && "crafting_table".equals(blockId)) {
            activeStep = 3;
        }
    }

    /** Gọi khi raid kết thúc thắng lợi. Kiểm tra step 6. */
    public void onRaidVictory() {
        if (activeStep > 0 || nextStep > MAX_STEPS) return;
        if (nextStep == 6) {
            activeStep = 6;
        }
    }

    /** Gọi khi player click "Đã hiểu!" trên popup. */
    public void dismissPopup() {
        if (activeStep <= 0) return;
        activeStep = 0;
        nextStep++;
    }

    // ─── Getters ─────────────────────────────────────

    public boolean isShowing() {
        return activeStep > 0;
    }

    public int getActiveStep() {
        return activeStep;
    }

    public boolean isComplete() {
        return nextStep > MAX_STEPS;
    }

    // ─── Block Classification Helpers ────────────────

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
