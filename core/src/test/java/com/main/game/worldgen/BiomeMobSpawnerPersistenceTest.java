package com.main.game.worldgen;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.main.game.entities.mob.Mob;
import org.junit.Test;

public class BiomeMobSpawnerPersistenceTest {

    @Test
    public void villagersArePersistentAcrossDistanceDespawn() {
        assertTrue(BiomeMobSpawner.isPersistentMob(new Mob(100f, 50f,
            Mob.MobType.VILLAGER, null, null, null)));
    }

    @Test
    public void raidMobsArePersistentAcrossDistanceDespawn() {
        assertTrue(BiomeMobSpawner.isPersistentMob(new Mob(100f, 50f,
            Mob.MobType.PILLAGER, null, null, null)));
        assertTrue(BiomeMobSpawner.isPersistentMob(new Mob(100f, 50f,
            Mob.MobType.VINDICATOR, null, null, null)));
        assertTrue(BiomeMobSpawner.isPersistentMob(new Mob(100f, 50f,
            Mob.MobType.EVOKER, null, null, null)));
        assertTrue(BiomeMobSpawner.isPersistentMob(new Mob(100f, 50f,
            Mob.MobType.VEX, null, null, null)));
        assertTrue(BiomeMobSpawner.isPersistentMob(new Mob(100f, 50f,
            Mob.MobType.RAVAGER, null, null, null)));
    }

    @Test
    public void normalPassiveMobsAreNotPersistentAcrossDistanceDespawn() {
        assertFalse(BiomeMobSpawner.isPersistentMob(new Mob(100f, 50f,
            Mob.MobType.COW, null, null, null)));
    }
}
