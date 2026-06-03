package com.main.game.projectile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.badlogic.gdx.math.Rectangle;
import com.main.game.blocks.SimpleBlock;
import com.main.game.world.World;
import org.junit.Test;

public class ProjectileTest {

    private static final float EPSILON = 0.0001f;

    @Test
    public void movesByVelocityAndExpiresAfterLifetime() {
        Projectile projectile = new Projectile(ProjectileType.PILLAGER_ARROW,
            0f, 0f, 0.35f, 0.08f, 2f, 0f, 3, 0.5f);

        projectile.update(0.25f, null);

        assertTrue(projectile.isAlive());
        assertEquals(0.5f, projectile.getX(), EPSILON);

        projectile.update(0.25f, null);

        assertFalse(projectile.isAlive());
    }

    @Test
    public void diesWhenTouchingSolidBlock() {
        World world = new World(77L);
        world.setBlock(1, 0, new SimpleBlock(1, 0, "stone", true, true, 1f, null));
        Projectile projectile = new Projectile(ProjectileType.PILLAGER_ARROW,
            0f, 0.2f, 0.35f, 0.08f, 10f, 0f, 3, 3f);

        projectile.update(0.1f, world);

        assertFalse(projectile.isAlive());
    }

    @Test
    public void overlapsTargetBoundsOnlyWhileAlive() {
        Projectile projectile = new Projectile(ProjectileType.PILLAGER_ARROW,
            2f, 3f, 0.35f, 0.08f, 0f, 0f, 3, 3f);
        Rectangle target = new Rectangle(2.1f, 2.9f, 0.8f, 1.8f);

        assertTrue(projectile.overlaps(target));

        projectile.kill();

        assertFalse(projectile.overlaps(target));
    }
}
