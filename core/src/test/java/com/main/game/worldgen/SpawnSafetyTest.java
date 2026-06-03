package com.main.game.worldgen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.main.game.blocks.SimpleBlock;
import com.main.game.world.World;
import org.junit.Test;

public class SpawnSafetyTest {

    @Test
    public void surfaceSpawnRequiresGrassGround() {
        World world = new World(123L);
        setBlock(world, 10, 40, "grass", true);

        Vector2 spawn = SpawnSafety.findSurfaceSpawn(world, 10, 0, 1, 2);

        assertNotNull(spawn);
        assertEquals(10.1f, spawn.x, 0.001f);
        assertEquals(41f, spawn.y, 0.001f);
    }

    @Test
    public void surfaceSpawnRejectsVillageRoofBlocks() {
        World world = new World(123L);
        setBlock(world, 10, 40, "grass", true);
        setBlock(world, 10, 50, "planks", true);

        assertNull(SpawnSafety.findSurfaceSpawn(world, 10, 0, 1, 2));
    }

    @Test
    public void wideSurfaceSpawnRequiresGrassUnderEveryFootTile() {
        World world = new World(123L);
        setBlock(world, 10, 40, "grass", true);
        setBlock(world, 11, 40, "dirt", true);

        assertTrue(SpawnSafety.isSafeEntitySpawn(world, 10, 41, 2, 2));
        assertNull(SpawnSafety.findSurfaceSpawn(world, 10, 0, 2, 2));
    }

    private static void setBlock(World world, int x, int y, String blockId, boolean solid) {
        world.setBlock(x, y, new SimpleBlock(x, y, blockId, solid, true, 0.6f, null));
    }
}
