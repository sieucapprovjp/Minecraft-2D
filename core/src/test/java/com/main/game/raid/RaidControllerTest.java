package com.main.game.raid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.main.game.blocks.SimpleBlock;
import com.main.game.entities.EntityManager;
import com.main.game.entities.mob.Mob;
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
        assertEquals(RaidController.PREPARATION_DELAY_SECONDS, controller.getNextWaveTimer(), 0.001f);
        assertEquals(0, controller.getCurrentWaveMobCount());
    }

    @Test
    public void doesNotStartMultipleRaidsFromMoreBannerPlacements() {
        World world = new World(123L);
        world.setVillageState(VillageState.present(100, 51, 90, 50, 21, 28));
        RaidController controller = new RaidController();

        assertTrue(controller.tryStartFromBanner(world, "raid_banner", 100, 51));
        assertFalse(controller.tryStartFromBanner(world, "raid_banner", 101, 51));
    }

    @Test
    public void movesCountdownRaidToWaveActiveAfterMobsSpawn() {
        World world = new World(123L);
        world.setVillageState(VillageState.present(100, 51, 90, 50, 21, 28));
        RaidController controller = new RaidController();

        assertTrue(controller.tryStartFromBanner(world, "raid_banner", 100, 51));
        controller.markWaveActive();

        assertEquals(RaidState.WAVE_ACTIVE, controller.getState());
    }

    @Test
    public void updateSpawnsFirstWaveAfterPreparationDelay() {
        World world = raidWorld();
        EntityManager entityManager = new EntityManager();
        RaidController controller = new RaidController();

        assertTrue(controller.tryStartFromBanner(world, "raid_banner", 100, 51));
        assertEquals(0, controller.update(29f, world, null, null, entityManager));
        assertEquals(RaidState.COUNTDOWN, controller.getState());

        int spawned = controller.update(1f, world, null, null, entityManager);

        assertEquals(4, spawned);
        assertEquals(RaidState.WAVE_ACTIVE, controller.getState());
        assertEquals(1, controller.getCurrentWave());
        assertEquals(4, controller.getCurrentWaveMobCount());
        assertEquals(4, RaidController.countAliveRaidMobs(entityManager));
    }

    @Test
    public void clearWaveWaitsBeforeSpawningNextWave() {
        World world = raidWorld();
        EntityManager entityManager = new EntityManager();
        RaidController controller = new RaidController();

        assertTrue(controller.tryStartFromBanner(world, "raid_banner", 100, 51));
        controller.update(30f, world, null, null, entityManager);
        killRaidMobs(entityManager);

        assertEquals(0, controller.update(0f, world, null, null, entityManager));
        assertEquals(RaidState.BETWEEN_WAVES, controller.getState());
        assertEquals(1, controller.getCurrentWave());
        assertEquals(RaidController.PREPARATION_DELAY_SECONDS, controller.getNextWaveTimer(), 0.001f);
        assertEquals(0, controller.update(29f, world, null, null, entityManager));

        int spawned = controller.update(1f, world, null, null, entityManager);

        assertEquals(7, spawned);
        assertEquals(RaidState.WAVE_ACTIVE, controller.getState());
        assertEquals(2, controller.getCurrentWave());
        assertEquals(7, controller.getCurrentWaveMobCount());
        assertEquals(7, RaidController.countAliveRaidMobs(entityManager));
    }

    @Test
    public void clearingFinalWaveCompletesRaid() {
        World world = raidWorld();
        EntityManager entityManager = new EntityManager();
        RaidController controller = new RaidController();

        assertTrue(controller.tryStartFromBanner(world, "raid_banner", 100, 51));
        for (int wave = 1; wave <= controller.getMaxWaves(); wave++) {
            int spawned = controller.update(30f, world, null, null, entityManager);
            assertTrue("Expected wave " + wave + " to spawn", spawned > 0);
            killRaidMobs(entityManager);
            controller.update(0f, world, null, null, entityManager);
        }

        assertEquals(RaidState.VICTORY, controller.getState());
        assertEquals(controller.getMaxWaves(), controller.getCurrentWave());
    }

    private World raidWorld() {
        World world = new World(123L);
        int centerX = 100;
        int surfaceY = 50;
        world.setVillageState(VillageState.present(centerX, surfaceY + 1, 90, surfaceY, 21, 28));
        for (int x = 50; x <= 150; x++) {
            world.setSurfaceY(x, surfaceY);
            world.setBlock(x, surfaceY - 1,
                new SimpleBlock(x, surfaceY - 1, "dirt", true, true, 0.6f, null));
            world.setBlock(x, surfaceY,
                new SimpleBlock(x, surfaceY, "grass", true, true, 0.6f, null));
        }
        return world;
    }

    private void killRaidMobs(EntityManager entityManager) {
        for (Mob mob : entityManager.getMobs()) {
            if (RaidMobSpawner.isRaidMobType(mob.getType())) {
                mob.setAlive(false);
            }
        }
    }
}
