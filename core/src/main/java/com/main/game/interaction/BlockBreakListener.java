package com.main.game.interaction;

import com.main.game.blocks.AbstractBlock;
import com.main.game.world.World;

public interface BlockBreakListener {
    void onBlockBroken(AbstractBlock block, World world);
}
