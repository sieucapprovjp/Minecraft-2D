package com.main.game.entities.mob;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MobRendererTest {

    private static final float EPSILON = 0.0001f;

    @Test
    public void convertsTexturePixelsToTileRenderSize() {
        assertEquals(0.625f, MobRenderer.widthTilesForPixels(50, 0.8f), EPSILON);
        assertEquals(1.25f, MobRenderer.heightTilesForPixels(100, 1.8f), EPSILON);
    }

    @Test
    public void fallsBackToCollisionSizeWhenTextureSizeIsUnavailable() {
        assertEquals(0.8f, MobRenderer.widthTilesForPixels(0, 0.8f), EPSILON);
        assertEquals(1.8f, MobRenderer.heightTilesForPixels(0, 1.8f), EPSILON);
    }

    @Test
    public void convertsScratchMobPixelsWithFortyPixelsPerTile() {
        assertEquals(1.25f, MobRenderer.widthTilesForPixels(50, 0.8f, 40f), EPSILON);
        assertEquals(2.5f, MobRenderer.heightTilesForPixels(100, 1.8f, 40f), EPSILON);
    }

    @Test
    public void centersNativeRenderWidthOnCollisionBox() {
        assertEquals(10.0875f, MobRenderer.centeredRenderX(10f, 0.8f, 0.625f), EPSILON);
        assertEquals(9.65f, MobRenderer.centeredRenderX(10f, 2.0f, 2.7f), EPSILON);
    }

    @Test
    public void constrainsOversizedNativeSpriteWithoutStretching() {
        assertEquals(0.5625f, MobRenderer.constrainedRenderScale(2.4f, 1.6f, 1.35f, 0.95f), EPSILON);
    }

    @Test
    public void leavesSpriteScaleAloneWhenNoRenderCapExists() {
        assertEquals(1f, MobRenderer.constrainedRenderScale(2.4f, 1.6f, 0f, 0f), EPSILON);
    }
}
