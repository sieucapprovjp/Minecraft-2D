package com.main.game.interaction;

import com.main.game.blocks.AbstractBlock;

import java.util.Set;

public final class BlockBreakRules {

    private static final Set<String> UNBREAKABLE_BLOCK_IDS = Set.of(
        "bedrock"
    );

    private BlockBreakRules() {
    }

    public static boolean canBreak(AbstractBlock block) {
        if (block == null || !block.isBreakable()) {
            return false;
        }
        return !UNBREAKABLE_BLOCK_IDS.contains(block.getBlockId());
    }
}
