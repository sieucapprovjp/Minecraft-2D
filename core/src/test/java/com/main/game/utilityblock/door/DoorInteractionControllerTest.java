package com.main.game.utilityblock.door;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.main.game.blocks.AbstractBlock;
import com.main.game.world.World;
import com.main.game.worldgen.WorldBlockFactory;
import org.junit.Test;

public class DoorInteractionControllerTest {

    @Test
    public void togglesBothDoorHalvesBetweenSolidClosedAndPassThroughOpen() {
        World world = new World(123L);
        world.setBlock(10, 20, WorldBlockFactory.create(10, 20, DoorInteractionController.BOTTOM_CLOSED));
        world.setBlock(10, 21, WorldBlockFactory.create(10, 21, DoorInteractionController.TOP_CLOSED));

        DoorInteractionController.ToggleResult opened = DoorInteractionController.toggleDoor(world, 10, 20);

        assertTrue(opened.toggled);
        assertTrue(opened.opened);
        assertDoorBlock(world, 10, 20, DoorInteractionController.BOTTOM_OPEN, false);
        assertDoorBlock(world, 10, 21, DoorInteractionController.TOP_OPEN, false);

        DoorInteractionController.ToggleResult closed = DoorInteractionController.toggleDoor(world, 10, 21);

        assertTrue(closed.toggled);
        assertFalse(closed.opened);
        assertDoorBlock(world, 10, 20, DoorInteractionController.BOTTOM_CLOSED, true);
        assertDoorBlock(world, 10, 21, DoorInteractionController.TOP_CLOSED, true);
    }

    @Test
    public void ignoresIncompleteDoorPairs() {
        World world = new World(123L);
        world.setBlock(10, 20, WorldBlockFactory.create(10, 20, DoorInteractionController.BOTTOM_CLOSED));

        DoorInteractionController.ToggleResult result = DoorInteractionController.toggleDoor(world, 10, 20);

        assertFalse(result.toggled);
        assertDoorBlock(world, 10, 20, DoorInteractionController.BOTTOM_CLOSED, true);
    }

    @Test
    public void removesBothHalvesWhenOneDoorHalfBreaks() {
        World world = new World(123L);
        AbstractBlock bottom = WorldBlockFactory.create(10, 20, DoorInteractionController.BOTTOM_CLOSED);
        world.setBlock(10, 20, bottom);
        world.setBlock(10, 21, WorldBlockFactory.create(10, 21, DoorInteractionController.TOP_CLOSED));

        assertTrue(DoorInteractionController.removeDoorPair(world, bottom));

        assertEquals(null, world.getBlock(10, 20));
        assertEquals(null, world.getBlock(10, 21));
    }

    private static void assertDoorBlock(World world, int x, int y, String expectedId, boolean expectedSolid) {
        AbstractBlock block = world.getBlock(x, y);
        assertTrue(block != null);
        assertEquals(expectedId, block.getBlockId());
        assertEquals(expectedSolid, block.isSolid());
    }
}
