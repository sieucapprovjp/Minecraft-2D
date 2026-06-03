package com.main.game.worldgen.village;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.main.game.entities.mob.VillagerProfession;
import org.junit.Test;

public class VillageVillagerSpawnerTest {

    @Test
    public void playerMustEnterVillageRadiusBeforeVillagersSpawn() {
        VillageState village = VillageState.present(320, 65, 310, 64, 21, 42);

        assertFalse(VillageVillagerSpawner.isPlayerNearVillage(village, 250f));
        assertTrue(VillageVillagerSpawner.isPlayerNearVillage(village, 278f));
        assertTrue(VillageVillagerSpawner.isPlayerNearVillage(village, 320f));
    }

    @Test
    public void missingVillageNeverActivatesVillagerSpawn() {
        assertFalse(VillageVillagerSpawner.isPlayerNearVillage(VillageState.none(), 320f));
    }

    @Test
    public void villagerProfessionsCycleThroughVillageOrder() {
        assertEquals(VillagerProfession.UNEMPLOYED, VillageVillagerSpawner.professionForVillageIndex(0));
        assertEquals(VillagerProfession.FARMER, VillageVillagerSpawner.professionForVillageIndex(1));
        assertEquals(VillagerProfession.BLACKSMITH, VillageVillagerSpawner.professionForVillageIndex(2));
        assertEquals(VillagerProfession.UNEMPLOYED, VillageVillagerSpawner.professionForVillageIndex(3));
    }
}
