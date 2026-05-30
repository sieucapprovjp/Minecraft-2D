package com.main.game.world;

import com.main.game.blocks.AbstractBlock;
import com.main.game.blocks.SimpleBlock;
import com.main.game.blocks.types.NatureBlocks;
import com.main.game.blocks.types.StoneBlocks;
import com.main.game.blocks.types.UtilityBlocks;
import com.main.game.blocks.types.WoodBlocks;
import java.util.ArrayList;
import java.util.List;

/*
 * Utility to populate a small demo grid in the world so you can visually inspect block types.
 *
 * Usage: call DemoBlockViewer.populateDemo(world, startX, startY) from GameScreen (for example on a key press).
 */
public final class DemoBlockViewer {

    private DemoBlockViewer() {}

    public interface BlockFactory {
        AbstractBlock create(int x, int y);
    }

    private static List<BlockFactory> makeFactories() {
        List<BlockFactory> f = new ArrayList<>();
        // Utility
        f.add((x,y) -> new UtilityBlocks.AirBlock(x,y));
        f.add((x,y) -> new UtilityBlocks.WaterBlock(x,y));

        // Nature
        f.add((x,y) -> new NatureBlocks.DirtBlock(x,y));
        f.add((x,y) -> new NatureBlocks.GrassBlockBlock(x,y));
        f.add((x,y) -> new NatureBlocks.SandBlock(x,y));
        f.add((x,y) -> new NatureBlocks.SnowBlock(x,y));
        f.add((x,y) -> new NatureBlocks.IceBlock(x,y));
        f.add((x,y) -> new NatureBlocks.CactusBlock(x,y));

        // Stone / base
        f.add((x,y) -> new StoneBlocks.StoneBlock(x,y));
        f.add((x,y) -> new StoneBlocks.BedrockBlock(x,y));
        f.add((x,y) -> new StoneBlocks.SandstoneBlock(x,y));

        // Wood
        f.add((x,y) -> new WoodBlocks.OakLogBlock(x,y));
        f.add((x,y) -> new WoodBlocks.OakPlanksBlock(x,y));
        f.add((x,y) -> new WoodBlocks.OakLeavesBlock(x,y));

        // Add a few placeholder variations using SimpleBlock with BlockPalette textures - if available
        f.add((x,y) -> new SimpleBlock(x,y, "placeholder_stone", true, true, 1.0f, BlockPalette.getStone()));
        f.add((x,y) -> new SimpleBlock(x,y, "placeholder_dirt", true, true, 0.6f, BlockPalette.getDirt()));
        f.add((x,y) -> new SimpleBlock(x,y, "placeholder_grass", true, true, 0.6f, BlockPalette.getGrass()));

        return f;
    }

    /** Populate a compact grid of blocks starting at (startX, startY). */
    public static void populateDemo(World world, int startX, int startY) {
        List<BlockFactory> factories = makeFactories();
        int cols = 6;
        for (int i = 0; i < factories.size(); i++) {
            int cx = i % cols;
            int cy = i / cols;
            int tx = startX + cx;
            int ty = startY + cy;
            if (!world.isInBounds(tx, ty)) continue;
            AbstractBlock b = factories.get(i).create(tx, ty);
            world.setBlock(tx, ty, b);
        }
    }
}
