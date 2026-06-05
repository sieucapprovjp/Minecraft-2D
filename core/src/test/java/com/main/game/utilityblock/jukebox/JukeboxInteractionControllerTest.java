package com.main.game.utilityblock.jukebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.main.game.world.World;
import org.junit.Test;

public class JukeboxInteractionControllerTest {

    @Test
    public void insertsMusicDiscIntoEmptyJukebox() {
        World world = new World(123L);
        JukeboxManager manager = new JukeboxManager();
        JukeboxInteractionController controller = new JukeboxInteractionController(manager);

        JukeboxInteractionController.InteractionResult result = controller.interact(world, 10, 20, "pigstep");

        assertNotNull(result);
        assertEquals(JukeboxInteractionController.InteractionType.INSERT, result.getType());
        assertEquals("pigstep", manager.getDisc(world, 10, 20));
        assertEquals("Pigstep", result.getDisplayName());
        assertEquals("audio/ui_menu/disc/pigstep.ogg", result.getMusicPath());
    }

    @Test
    public void ejectsStoredDiscOnNextInteraction() {
        World world = new World(123L);
        JukeboxManager manager = new JukeboxManager();
        JukeboxInteractionController controller = new JukeboxInteractionController(manager);
        assertTrue(manager.insertDisc(world, 10, 20, "pigstep"));

        JukeboxInteractionController.InteractionResult result = controller.interact(world, 10, 20, null);

        assertNotNull(result);
        assertEquals(JukeboxInteractionController.InteractionType.EJECT, result.getType());
        assertEquals("pigstep", result.getDiscItemId());
        assertNull(manager.getDisc(world, 10, 20));
    }

    @Test
    public void ignoresNonDiscItemsForEmptyJukebox() {
        World world = new World(123L);
        JukeboxInteractionController controller = new JukeboxInteractionController(new JukeboxManager());

        assertNull(controller.interact(world, 10, 20, "apple"));
        assertNull(JukeboxInteractionController.createInsertRequest("chest", 10, 20, "pigstep"));
    }
}
