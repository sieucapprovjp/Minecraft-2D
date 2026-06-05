package com.main.game.worldgen.village;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.main.game.blocks.AbstractBlock;
import com.main.game.blocks.SimpleBlock;
import com.main.game.entities.mob.Mob;
import com.main.game.world.World;
import com.main.game.worldgen.BiomeType;
import com.main.game.worldgen.SpawnSafety;
import com.main.game.worldgen.WorldBlockFactory;
import org.junit.Test;

public class VillagePlacerTest {

    @Test
    public void villagerSpawnPointsAreSafeInsidePlacedVillage() {
        World world = new World(123L);
        int centerX = 320;
        int plainsHalfWidth = 48;
        int surfaceY = 64;
        for (int x = centerX - plainsHalfWidth; x <= centerX + plainsHalfWidth; x++) {
            world.setBiome(x, BiomeType.PLAINS);
            world.setSurfaceY(x, surfaceY);
            world.setBlock(x, surfaceY, new SimpleBlock(x, surfaceY, "grass", true, true, 0.6f, null));
        }

        VillageState village = VillagePlacer.place(world, 123L, centerX, plainsHalfWidth);

        assertTrue(village.isPresent());
        assertEquals(VillagePlacer.HOUSE_WIDTH, village.getHouseWidth());
        assertEquals(VillagePlacer.VILLAGE_RADIUS, village.getRadius());
        assertEquals(3, village.getVillagerSpawnPoints().size());
        int spawnWidth = Mob.getRequiredSpawnWidth(Mob.MobType.VILLAGER);
        int spawnHeight = Mob.getRequiredSpawnHeight(Mob.MobType.VILLAGER);
        for (VillageSpawnPoint spawnPoint : village.getVillagerSpawnPoints()) {
            assertTrue(SpawnSafety.isSafeEntitySpawn(world,
                spawnPoint.getTileX(), spawnPoint.getTileY(), spawnWidth, spawnHeight));
        }
    }

    @Test
    public void villageFoundationRemovesSurfaceFlowersUnderRaisedFloor() {
        World world = new World(123L);
        int centerX = 320;
        int plainsHalfWidth = 48;
        int highSurfaceY = 64;
        int lowSurfaceY = 62;
        int flowerX = centerX;
        int flowerY = lowSurfaceY + 1;

        for (int x = centerX - plainsHalfWidth; x <= centerX + plainsHalfWidth; x++) {
            int surfaceY = x == flowerX ? lowSurfaceY : highSurfaceY;
            world.setBiome(x, BiomeType.PLAINS);
            world.setSurfaceY(x, surfaceY);
            for (int y = World.BEDROCK_TOP_Y + 1; y < surfaceY; y++) {
                world.setBlock(x, y, WorldBlockFactory.create(x, y, "dirt"));
            }
            world.setBlock(x, surfaceY, WorldBlockFactory.create(x, surfaceY, "grass"));
        }
        world.setBlock(flowerX, flowerY, WorldBlockFactory.create(flowerX, flowerY, "poppy"));

        VillageState village = VillagePlacer.place(world, 123L, centerX, plainsHalfWidth);

        assertTrue(village.isPresent());
        AbstractBlock block = world.getBlock(flowerX, flowerY);
        assertEquals("dirt", block.getBlockId());
        assertTrue(block.isSolid());
    }
}
