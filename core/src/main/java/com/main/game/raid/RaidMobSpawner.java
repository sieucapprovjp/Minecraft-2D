package com.main.game.raid;

import com.badlogic.gdx.math.Vector2;
import com.main.game.entities.EntityManager;
import com.main.game.entities.mob.Mob;
import com.main.game.entities.player.Player;
import com.main.game.physics.PhysicsEngine;
import com.main.game.world.World;
import com.main.game.worldgen.SpawnSafety;
import com.main.game.worldgen.village.VillageState;

public final class RaidMobSpawner {

    private static final int SPAWN_SEARCH_RADIUS = 16;
    private static final int SPAWN_EDGE_GAP = 5;
    private static final int SPAWN_PAIR_SPACING = 5;
    private static final int[] HOUSE_PREVIEW_OFFSETS = {2, 6, 12, 17};
    private static final Mob.MobType[] PREVIEW_RAID_MOBS = {
        Mob.MobType.PILLAGER,
        Mob.MobType.VINDICATOR,
        Mob.MobType.EVOKER,
        Mob.MobType.RAVAGER
    };

    private RaidMobSpawner() {
    }

    public static int spawnOneOfEach(World world, Player player, PhysicsEngine physics,
                                     EntityManager entityManager) {
        if (world == null || entityManager == null) {
            return 0;
        }
        VillageState village = world.getVillageState();
        if (village == null || !village.isPresent()) {
            return 0;
        }

        int spawned = 0;
        for (int i = 0; i < PREVIEW_RAID_MOBS.length; i++) {
            Mob.MobType type = PREVIEW_RAID_MOBS[i];
            Vector2 spawn = findRaidMobSpawn(world, village, type, i);
            if (spawn == null) {
                continue;
            }
            entityManager.addMob(new Mob(spawn.x, spawn.y, type, player, physics, world));
            spawned++;
        }
        return spawned;
    }

    static Mob.MobType[] previewRaidMobs() {
        return PREVIEW_RAID_MOBS.clone();
    }

    static int preferredSpawnX(VillageState village, int index) {
        if (village == null) {
            return 0;
        }
        int side = index % 2 == 0 ? -1 : 1;
        int pair = index / 2;
        return village.getCenterX() + side * (village.getRadius() + SPAWN_EDGE_GAP + pair * SPAWN_PAIR_SPACING);
    }

    private static Vector2 findRaidMobSpawn(World world, VillageState village, Mob.MobType type, int index) {
        int width = Mob.getRequiredSpawnWidth(type);
        int height = Mob.getRequiredSpawnHeight(type);
        Vector2 houseSpawn = findHousePreviewSpawn(world, village, width, height, index);
        if (houseSpawn != null) {
            return houseSpawn;
        }

        Vector2 spawn = SpawnSafety.findSurfaceSpawn(world,
            preferredSpawnX(village, index), SPAWN_SEARCH_RADIUS, width, height);
        if (spawn != null) {
            return spawn;
        }
        return SpawnSafety.findSurfaceSpawn(world,
            preferredSpawnX(village, index + 1), SPAWN_SEARCH_RADIUS, width, height);
    }

    private static Vector2 findHousePreviewSpawn(World world, VillageState village, int width, int height, int index) {
        if (world == null || village == null || HOUSE_PREVIEW_OFFSETS.length == 0) {
            return null;
        }
        int baseX = village.getHouseBaseX();
        int y = village.getHouseFloorY() + 1;
        for (int attempt = 0; attempt < HOUSE_PREVIEW_OFFSETS.length; attempt++) {
            int offsetIndex = (index + attempt) % HOUSE_PREVIEW_OFFSETS.length;
            int x = baseX + HOUSE_PREVIEW_OFFSETS[offsetIndex];
            if (SpawnSafety.isSafeEntitySpawn(world, x, y, width, height)) {
                return new Vector2(x + 0.1f, y);
            }
        }
        return null;
    }
}
