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
    public void normalPassiveMobsAreNotPersistentAcrossDistanceDespawn() {
        assertFalse(BiomeMobSpawner.isPersistentMob(new Mob(100f, 50f,
            Mob.MobType.COW, null, null, null)));
    }
}
