package com.main.game.blocks.types;
import com.main.game.blocks.AbstractBlock;
import com.main.game.utils.TextureManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class NatureBlocks {
    public static class DirtBlock extends AbstractBlock {
        public DirtBlock(int x, int y) { super(x, y, "dirt", true, true, 0.5f); }
        @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("dirt"); }
    }
    public static class GrassBlockBlock extends AbstractBlock {
        public GrassBlockBlock(int x, int y) { super(x, y, "grass_block", true, true, 0.6f); }
        @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("grass_block"); }
    }
    public static class SandBlock extends AbstractBlock {
        public SandBlock(int x, int y) { super(x, y, "sand", true, true, 0.5f); }
        @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("sand"); }
    }
}
