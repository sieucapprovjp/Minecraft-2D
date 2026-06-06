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

    @Test
    public void villagePlacesPathAndFlowerGardensBetweenHouses() {
        World world = new World(123L);
        int centerX = 320;
        int plainsHalfWidth = 48;
        int surfaceY = 64;
        for (int x = centerX - plainsHalfWidth; x <= centerX + plainsHalfWidth; x++) {
            world.setBiome(x, BiomeType.PLAINS);
            world.setSurfaceY(x, surfaceY);
            world.setBlock(x, surfaceY, WorldBlockFactory.create(x, surfaceY, "grass"));
        }

        VillageState village = VillagePlacer.place(world, 123L, centerX, plainsHalfWidth);

        assertTrue(village.isPresent());
        int mainBaseX = village.getHouseBaseX();
        int floorY = village.getHouseFloorY();
        AbstractBlock leftPath = world.getBlock(mainBaseX - 4, floorY);
        AbstractBlock leftGarden = world.getBlock(mainBaseX - 3, floorY + 1);
        AbstractBlock rightPath = world.getBlock(mainBaseX + VillagePlacer.HOUSE_WIDTH + 2, floorY);
        AbstractBlock rightGarden = world.getBlock(mainBaseX + VillagePlacer.HOUSE_WIDTH + 2, floorY + 1);

        assertTrue(isVillagePathBlock(leftPath));
        assertTrue(leftGarden != null && !leftGarden.isSolid());
        assertTrue(isVillagePathBlock(rightPath));
        assertTrue(rightGarden != null && !rightGarden.isSolid());
    }

    @Test
    public void villageHousesContainEndDoorsAndPassThroughUtilityBlocks() {
        World world = new World(123L);
        int centerX = 320;
        int plainsHalfWidth = 48;
        int surfaceY = 64;
        for (int x = centerX - plainsHalfWidth; x <= centerX + plainsHalfWidth; x++) {
            world.setBiome(x, BiomeType.PLAINS);
            world.setSurfaceY(x, surfaceY);
            world.setBlock(x, surfaceY, WorldBlockFactory.create(x, surfaceY, "grass"));
        }

        VillageState village = VillagePlacer.place(world, 123L, centerX, plainsHalfWidth);

        assertTrue(village.isPresent());
        int mainBaseX = village.getHouseBaseX();
        int floorY = village.getHouseFloorY();
        assertEquals(6, countBlocks(world, mainBaseX - 20, mainBaseX + 40, floorY + 1,
            "village_door_bottom_closed"));
        assertBlockId(world, mainBaseX, floorY + 1, "village_door_bottom_closed");
        assertBlockId(world, mainBaseX, floorY + 2, "village_door_top_closed");
        assertBlockId(world, mainBaseX + VillagePlacer.HOUSE_WIDTH - 1, floorY + 1,
            "village_door_bottom_closed");
        assertBlockId(world, mainBaseX + VillagePlacer.HOUSE_WIDTH - 1, floorY + 2,
            "village_door_top_closed");
        assertPassThroughBlockId(world, mainBaseX + 5, floorY + 1, "village_crafting_table");
        assertPassThroughBlockId(world, mainBaseX + 6, floorY + 1, "village_furnace");
        assertPassThroughBlockId(world, mainBaseX + 12, floorY + 1, "village_chest");
    }

    private static boolean isVillagePathBlock(AbstractBlock block) {
        return block != null
            && ("village_cobblestone".equals(block.getBlockId())
            || "village_moss_stone".equals(block.getBlockId()));
    }

    private static void assertBlockId(World world, int x, int y, String expectedId) {
        AbstractBlock block = world.getBlock(x, y);
        assertTrue(block != null);
        assertEquals(expectedId, block.getBlockId());
    }

    private static void assertPassThroughBlockId(World world, int x, int y, String expectedId) {
        AbstractBlock block = world.getBlock(x, y);
        assertTrue(block != null);
        assertEquals(expectedId, block.getBlockId());
        assertTrue(!block.isSolid());
    }

    private static int countBlocks(World world, int minX, int maxX, int y, String blockId) {
        int count = 0;
        for (int x = minX; x <= maxX; x++) {
            AbstractBlock block = world.getBlock(x, y);
            if (block != null && blockId.equals(block.getBlockId())) {
                count++;
            }
        }
        return count;
    }
}
