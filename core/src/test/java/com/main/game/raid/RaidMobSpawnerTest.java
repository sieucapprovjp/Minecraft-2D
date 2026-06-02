package com.main.game.raid;

import static org.junit.Assert.assertEquals;

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
        assertEquals(92.1f, entityManager.getMobs().get(0).getX(), 0.001f);
        assertEquals(96.1f, entityManager.getMobs().get(1).getX(), 0.001f);
        assertEquals(102.1f, entityManager.getMobs().get(2).getX(), 0.001f);
        assertEquals(107.1f, entityManager.getMobs().get(3).getX(), 0.001f);
    }

    @Test
    public void preferredSpawnPositionsAlternateOutsideVillageRadius() {
        VillageState village = VillageState.present(100, 51, 90, 50, 21, 20);

        assertEquals(75, RaidMobSpawner.preferredSpawnX(village, 0));
        assertEquals(125, RaidMobSpawner.preferredSpawnX(village, 1));
        assertEquals(70, RaidMobSpawner.preferredSpawnX(village, 2));
        assertEquals(130, RaidMobSpawner.preferredSpawnX(village, 3));
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
}
