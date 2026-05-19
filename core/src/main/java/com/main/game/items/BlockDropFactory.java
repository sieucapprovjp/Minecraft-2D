package com.main.game.items;

import com.badlogic.gdx.math.MathUtils;
import com.main.game.blocks.AbstractBlock;
import com.main.game.world.World;

public final class BlockDropFactory {

    private BlockDropFactory() {
    }

    public static HarvestEntry createDrop(AbstractBlock block, World world) {
        if (block == null) {
            return null;
        }

        int tileIdx = HarvestEntry.toTileIdx(block.getTileX(), block.getTileY(), world);
        return new HarvestEntry(
            tileIdx,
            block.getBlockId(),
            block.getTexture(),
            1,
            MathUtils.random(-0.1f, 0.1f),
            HarvestEntry.RANDOM_VERTICAL_SPEED
        );
    }
}
