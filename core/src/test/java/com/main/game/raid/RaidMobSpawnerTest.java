package com.main.game.raid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.main.game.blocks.SimpleBlock;
import com.main.game.entities.EntityManager;
import com.main.game.entities.mob.Mob;
import com.main.game.world.World;
import com.main.game.worldgen.village.VillageState;
import org.junit.Test;

public class RaidMobSpawnerTest {

    @Test
    public void previewRaidContainsOneOfEachRaidMob() {
        Mob.MobType[] mobs = RaidMobSpawner.previewRaidMobs();

        assertEquals(Mob.MobType.PILLAGER, mobs[0]);
        assertEquals(Mob.MobType.VINDICATOR, mobs[1]);
        assertEquals(Mob.MobType.EVOKER, mobs[2]);
        assertEquals(Mob.MobType.RAVAGER, mobs[3]);
    }

    @Test
    public void raidHasThreeEscalatingWaves() {
        assertEquals(3, RaidMobSpawner.maxWaveCount());
        assertEquals(4, RaidMobSpawner.waveMobs(1).length);
        assertEquals(7, RaidMobSpawner.waveMobs(2).length);
        assertEquals(9, RaidMobSpawner.waveMobs(3).length);
        assertMobCount(1, Mob.MobType.PILLAGER, 2);
        assertMobCount(1, Mob.MobType.VINDICATOR, 2);
        assertMobCount(2, Mob.MobType.EVOKER, 1);
        assertMobCount(2, Mob.MobType.PILLAGER, 3);
        assertMobCount(2, Mob.MobType.VINDICATOR, 3);
        assertMobCount(3, Mob.MobType.RAVAGER, 1);
        assertMobCount(3, Mob.MobType.EVOKER, 2);
        assertMobCount(3, Mob.MobType.PILLAGER, 3);
        assertMobCount(3, Mob.MobType.VINDICATOR, 3);
    }

    @Test
    public void spawnsOneOfEachRaidMobAroundVillage() {
        World world = new World(123L);
        int centerX = 100;
        int surfaceY = 50;
        world.setVillageState(VillageState.present(centerX, surfaceY + 1, 90, surfaceY, 21, 20));
        fillFlatGrass(world, 50, 150, surfaceY);
        EntityManager entityManager = new EntityManager();

        int spawned = RaidMobSpawner.spawnOneOfEach(world, null, null, entityManager);

        assertEquals(4, spawned);
        assertEquals(4, entityManager.getMobs().size());
        assertEquals(Mob.MobType.PILLAGER, entityManager.getMobs().get(0).getType());
        assertEquals(Mob.MobType.VINDICATOR, entityManager.getMobs().get(1).getType());
        assertEquals(Mob.MobType.EVOKER, entityManager.getMobs().get(2).getType());
        assertEquals(Mob.MobType.RAVAGER, entityManager.getMobs().get(3).getType());
        assertEquals(75.1f, entityManager.getMobs().get(0).getX(), 0.001f);
        assertEquals(125.1f, entityManager.getMobs().get(1).getX(), 0.001f);
        assertEquals(70.1f, entityManager.getMobs().get(2).getX(), 0.001f);
        assertEquals(130.1f, entityManager.getMobs().get(3).getX(), 0.001f);
    }

    @Test
    public void preferredSpawnPositionsAlternateOutsideVillageRadius() {
        VillageState village = VillageState.present(100, 51, 90, 50, 21, 20);

        assertEquals(75, RaidMobSpawner.preferredSpawnX(village, 0));
        assertEquals(125, RaidMobSpawner.preferredSpawnX(village, 1));
        assertEquals(70, RaidMobSpawner.preferredSpawnX(village, 2));
        assertEquals(130, RaidMobSpawner.preferredSpawnX(village, 3));
    }

    @Test
    public void summonedVexCountsAsRaidMobUntilKilled() {
        assertTrue(RaidMobSpawner.isRaidMobType(Mob.MobType.VEX));
    }

    private void fillFlatGrass(World world, int minX, int maxX, int surfaceY) {
        for (int x = minX; x <= maxX; x++) {
            world.setSurfaceY(x, surfaceY);
            world.setBlock(x, surfaceY - 1,
                new SimpleBlock(x, surfaceY - 1, "dirt", true, true, 0.6f, null));
            world.setBlock(x, surfaceY,
                new SimpleBlock(x, surfaceY, "grass", true, true, 0.6f, null));
        }
    }

    private void assertMobCount(int waveNumber, Mob.MobType type, int expectedCount) {
        int count = 0;
        for (Mob.MobType mobType : RaidMobSpawner.waveMobs(waveNumber)) {
            if (mobType == type) {
                count++;
            }
        }
        assertEquals(expectedCount, count);
    }
}
