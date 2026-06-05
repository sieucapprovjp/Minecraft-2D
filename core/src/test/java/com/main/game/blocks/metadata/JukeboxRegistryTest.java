package com.main.game.blocks.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.main.game.inventory.ItemRegistry;
import org.junit.Test;

public class JukeboxRegistryTest {

    @Test
    public void jukeboxIsAPlaceableSolidUtilityBlock() {
        assertTrue(BlockRegistry.isPlaceable("jukebox"));
        assertTrue(BlockRegistry.isSolid("jukebox"));
        assertTrue(ItemRegistry.isPlaceableBlock("jukebox"));
        assertEquals("jukebox", BlockRegistry.getDropItemId("jukebox"));
    }
}
