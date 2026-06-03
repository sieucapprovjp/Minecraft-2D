package com.main.game.projectile;

import static org.junit.Assert.assertEquals;

import com.badlogic.gdx.math.Vector2;
import org.junit.Test;

public class ProjectileAimTest {

    private static final float EPSILON = 0.0001f;

    @Test
    public void aimsDirectlyAtTargetHeight() {
        Vector2 direction = ProjectileAim.directionBetween(0f, 0f, 4f, 1f, 0f);

        assertEquals(0.9701f, direction.x, EPSILON);
        assertEquals(0.2425f, direction.y, EPSILON);
    }

    @Test
    public void spreadRotatesAimDirection() {
        Vector2 direction = ProjectileAim.directionBetween(0f, 0f, 1f, 0f, 10f);

        assertEquals((float) Math.cos(Math.toRadians(10)), direction.x, EPSILON);
        assertEquals((float) Math.sin(Math.toRadians(10)), direction.y, EPSILON);
    }

    @Test
    public void spawnCenterUsesUpperBodyMuzzle() {
        Vector2 center = ProjectileAim.spawnCenter(10f, 20f, 1.8f, new Vector2(1f, 0f));

        assertEquals(10.55f, center.x, EPSILON);
        assertEquals(21.404f, center.y, EPSILON);
    }
}
