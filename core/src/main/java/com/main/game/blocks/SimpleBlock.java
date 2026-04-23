package com.main.game.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SimpleBlock extends AbstractBlock {

    private final TextureRegion texture;

    public SimpleBlock(int tileX, int tileY, String blockId,
                       boolean isSolid, boolean isBreakable, float hardness,
                       TextureRegion texture) {
        super(tileX, tileY, blockId, isSolid, isBreakable, hardness);
        this.texture = texture;
    }

    @Override
    public TextureRegion getTexture() {
        return texture;
    }
}
