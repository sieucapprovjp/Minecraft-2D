package com.main.game.entities.mob;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.main.game.world.World;
import org.junit.Test;

public class MobVexTest {

    @Test
    public void vexHoversOnSecondTileAboveSurfaceWithoutGravityPhysics() {
        World world = new World(7L);
        world.setSurfaceY(12, 40);
        Mob vex = new Mob(12f, 1f, Mob.MobType.VEX, null, null, world);

        vex.update(0.25f);

        assertEquals(42f, vex.getY(), 0.0001f);
        assertTrue(vex.isOnGround());
        assertEquals(Mob.AIState.PATROL, vex.getAIState());
    }
}
