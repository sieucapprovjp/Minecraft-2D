package com.main.game.worldgen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GuaranteedPlainsTest {

    @Test
    public void guaranteedPlainsRegionIsCenteredAwayFromSpawn() {
        int center = WorldGenerator.guaranteedPlainsCenterX(500);

        assertEquals(320, center);
        assertTrue(center > 250);
        assertTrue(WorldGenerator.isGuaranteedPlainsColumn(center, center));
        assertTrue(WorldGenerator.isGuaranteedPlainsColumn(center - WorldGenerator.guaranteedPlainsHalfWidth(), center));
        assertTrue(WorldGenerator.isGuaranteedPlainsColumn(center + WorldGenerator.guaranteedPlainsHalfWidth(), center));
        assertFalse(WorldGenerator.isGuaranteedPlainsColumn(center + WorldGenerator.guaranteedPlainsHalfWidth() + 1, center));
    }

    @Test
    public void guaranteedPlainsOverridesNoiseBiomeSelection() {
        int center = WorldGenerator.guaranteedPlainsCenterX(500);

        assertEquals(BiomeType.PLAINS, WorldGenerator.chooseBiome(center, 12345L, center));
        assertEquals(BiomeType.PLAINS, WorldGenerator.chooseBiome(center - 20, 12345L, center));
        assertEquals(BiomeType.PLAINS, WorldGenerator.chooseBiome(center + 20, 12345L, center));
    }

    @Test
    public void guaranteedPlainsSurfaceStaysFlatEnoughForVillageHouse() {
        int baseGround = 64;
        int center = WorldGenerator.guaranteedPlainsCenterX(500);
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int x = center - WorldGenerator.guaranteedPlainsHalfWidth(); x <= center + WorldGenerator.guaranteedPlainsHalfWidth(); x++) {
            int surface = WorldGenerator.guaranteedPlainsSurface(baseGround, x, 12345L);
            min = Math.min(min, surface);
            max = Math.max(max, surface);
        }

        assertTrue(max - min <= 2);
    }
}
