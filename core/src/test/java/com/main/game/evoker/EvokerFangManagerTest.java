package com.main.game.evoker;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EvokerFangManagerTest {

    private static final float EPSILON = 0.0001f;

    @Test
    public void spawnsFiveFangsFromNearToFarTowardRightTarget() {
        EvokerFangManager manager = new EvokerFangManager();

        manager.spawnLine(10f, 20f, 15f, null);

        assertEquals(5, manager.activeCount());
        assertEquals(11f, manager.get(0).getCenterX(), EPSILON);
        assertEquals(15f, manager.get(4).getCenterX(), EPSILON);
        assertEquals(0f, manager.get(0).getDelay(), EPSILON);
        assertEquals(2f / 60f, manager.get(1).getDelay(), EPSILON);
    }

    @Test
    public void spawnsFiveFangsFromNearToFarTowardLeftTarget() {
        EvokerFangManager manager = new EvokerFangManager();

        manager.spawnLine(10f, 20f, 5f, null);

        assertEquals(5, manager.activeCount());
        assertEquals(9f, manager.get(0).getCenterX(), EPSILON);
        assertEquals(5f, manager.get(4).getCenterX(), EPSILON);
    }
}
