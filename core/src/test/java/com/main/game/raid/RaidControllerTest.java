package com.main.game.raid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.main.game.world.World;
import com.main.game.worldgen.village.VillageState;
import org.junit.Test;

public class RaidControllerTest {

    @Test
    public void startsOnlyWhenRaidBannerIsPlacedInsideVillageHouse() {
        World world = new World(123L);
        world.setVillageState(VillageState.present(100, 51, 90, 50, 21, 28));
        RaidController controller = new RaidController();

        assertFalse(controller.tryStartFromBanner(world, "planks", 100, 51));
        assertFalse(controller.tryStartFromBanner(world, "raid_banner", 100, 55));
        assertTrue(controller.tryStartFromBanner(world, "raid_banner", 100, 51));
        assertEquals(RaidState.COUNTDOWN, controller.getState());
        assertEquals(100, controller.getTriggerTileX());
        assertEquals(51, controller.getTriggerTileY());
    }

    @Test
    public void doesNotStartMultipleRaidsFromMoreBannerPlacements() {
        World world = new World(123L);
        world.setVillageState(VillageState.present(100, 51, 90, 50, 21, 28));
        RaidController controller = new RaidController();

        assertTrue(controller.tryStartFromBanner(world, "raid_banner", 100, 51));
        assertFalse(controller.tryStartFromBanner(world, "raid_banner", 101, 51));
    }
}
