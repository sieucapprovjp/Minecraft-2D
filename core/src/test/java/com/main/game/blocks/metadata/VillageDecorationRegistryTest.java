package com.main.game.blocks.metadata;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VillageDecorationRegistryTest {

    @Test
    public void villageDecorationsAreRegisteredAndPassThroughWhereNeeded() {
        assertPassThroughDecoration("village_bookshelf");
        assertPassThroughDecoration("village_glass2");
        assertPassThroughDecoration("village_glass3");
        assertPassThroughDecoration("village_bed_left");
        assertPassThroughDecoration("village_bed_right");
    }

    @Test
    public void villageStructuralDecorationsRemainSolid() {
        assertTrue(BlockRegistry.isPlaceable("village_cobblestone"));
        assertTrue(BlockRegistry.isSolid("village_cobblestone"));
        assertTrue(BlockRegistry.isPlaceable("village_moss_stone"));
        assertTrue(BlockRegistry.isSolid("village_moss_stone"));
        assertTrue(BlockRegistry.isPlaceable("village_roof_stair_left"));
        assertTrue(BlockRegistry.isSolid("village_roof_stair_left"));
        assertTrue(BlockRegistry.isPlaceable("village_roof_stair_right"));
        assertTrue(BlockRegistry.isSolid("village_roof_stair_right"));
    }

    private void assertPassThroughDecoration(String blockId) {
        assertTrue(BlockRegistry.isPlaceable(blockId));
        assertFalse(BlockRegistry.isSolid(blockId));
    }
}
