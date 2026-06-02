package com.main.game.blocks.metadata;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BlockRenderSpecTest {

    private static final float EPSILON = 0.0001f;

    @Test
    public void normalBlocksRenderAsFullTile() {
        BlockRenderSpec spec = BlockRegistry.getRenderSpec("stone");

        assertEquals(1f, spec.width(null), EPSILON);
        assertEquals(1f, spec.height(null), EPSILON);
        assertEquals(0f, spec.offsetX(null), EPSILON);
        assertEquals(0f, spec.offsetY(null), EPSILON);
    }

    @Test
    public void vegetationUsesNativePixelSizeRelativeToEightyPixelTiles() {
        BlockRenderSpec spec = BlockRegistry.getRenderSpec("poppy");

        assertEquals(35f / 80f, spec.widthFromPixels(35), EPSILON);
        assertEquals(55f / 80f, spec.heightFromPixels(55), EPSILON);
    }

    @Test
    public void unknownBlocksFallBackToFullTileRendering() {
        BlockRenderSpec spec = BlockRegistry.getRenderSpec("does_not_exist");

        assertEquals(1f, spec.width(null), EPSILON);
        assertEquals(1f, spec.height(null), EPSILON);
    }
}
