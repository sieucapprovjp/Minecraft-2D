package com.main.game.worldgen.village;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VillageStateTest {

    @Test
    public void raidBannerAreaOnlyCoversInteriorOfPresentVillageHouse() {
        VillageState village = VillageState.present(100, 51, 90, 50, 21, 28);

        assertTrue(village.containsRaidBannerTile(90, 51));
        assertTrue(village.containsRaidBannerTile(110, 54));
        assertFalse(village.containsRaidBannerTile(89, 51));
        assertFalse(village.containsRaidBannerTile(111, 51));
        assertFalse(village.containsRaidBannerTile(100, 50));
        assertFalse(village.containsRaidBannerTile(100, 55));
    }

    @Test
    public void missingVillageNeverAcceptsRaidBannerTiles() {
        assertFalse(VillageState.none().containsRaidBannerTile(100, 51));
    }
}
