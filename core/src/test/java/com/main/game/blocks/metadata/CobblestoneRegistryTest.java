package com.main.game.blocks.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.main.game.inventory.ItemRegistry;
import org.junit.Test;

public class CobblestoneRegistryTest {

    @Test
    public void cobblestoneIsAPlaceableBlockItem() {
        assertTrue(BlockRegistry.isPlaceable("cobblestone"));
        assertTrue(BlockRegistry.isSolid("cobblestone"));
        assertTrue(BlockRegistry.isBreakable("cobblestone"));
        assertEquals("cobblestone", BlockRegistry.getDropItemId("cobblestone"));
        assertEquals("cobble_stone", BlockRegistry.getTextureName("cobblestone"));
        assertTrue(ItemRegistry.isPlaceableBlock("cobblestone"));
    }
}
