package com.main.game.blocks.types;

import com.main.game.blocks.AbstractBlock;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class UtilityBlocks {
    // Phải có chữ "static" và "public"
    public static class AirBlock extends AbstractBlock {
        public AirBlock(int x, int y) {
            super(x, y, "air", false, false, 0f);
        }
        @Override public TextureRegion getTexture() { return null; }
    }

    public static class WaterBlock extends AbstractBlock {
        public WaterBlock(int x, int y) {
            super(x, y, "water", false, false, 0f);
        }
        @Override public TextureRegion getTexture() { return com.main.game.world.BlockPalette.getWater(); }
    }
}
