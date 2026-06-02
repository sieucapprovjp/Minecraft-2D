package com.main.game.blocks.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RaidBannerRegistryTest {

    @Test
    public void raidBannerIsPlaceableNonSolidBlock() {
        assertTrue(BlockRegistry.isPlaceable("raid_banner"));
        assertFalse(BlockRegistry.isSolid("raid_banner"));
        assertTrue(BlockRegistry.isBreakable("raid_banner"));
        assertEquals("raid_banner", BlockRegistry.getDropItemId("raid_banner"));
        assertEquals("raid_banner", BlockRegistry.getTextureName("raid_banner"));
    }
}
