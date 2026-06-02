package com.main.game.worldgen.village;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.main.game.blocks.SimpleBlock;
import com.main.game.entities.mob.Mob;
import com.main.game.world.World;
import com.main.game.worldgen.BiomeType;
import com.main.game.worldgen.SpawnSafety;
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
        assertEquals(3, village.getVillagerSpawnPoints().size());
        int spawnWidth = Mob.getRequiredSpawnWidth(Mob.MobType.VILLAGER);
        int spawnHeight = Mob.getRequiredSpawnHeight(Mob.MobType.VILLAGER);
        for (VillageSpawnPoint spawnPoint : village.getVillagerSpawnPoints()) {
            assertTrue(SpawnSafety.isSafeEntitySpawn(world,
                spawnPoint.getTileX(), spawnPoint.getTileY(), spawnWidth, spawnHeight));
        }
    }
}
