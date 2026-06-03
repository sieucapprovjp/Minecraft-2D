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
    private static final Mob.MobType[] PREVIEW_RAID_MOBS = {
        Mob.MobType.PILLAGER,
        Mob.MobType.VINDICATOR,
        Mob.MobType.EVOKER,
        Mob.MobType.RAVAGER
    };
    private static final Mob.MobType[][] RAID_WAVES = {
        {
            Mob.MobType.PILLAGER,
            Mob.MobType.PILLAGER,
            Mob.MobType.VINDICATOR,
            Mob.MobType.VINDICATOR
        },
        {
            Mob.MobType.EVOKER,
            Mob.MobType.PILLAGER,
            Mob.MobType.PILLAGER,
            Mob.MobType.PILLAGER,
            Mob.MobType.VINDICATOR,
            Mob.MobType.VINDICATOR,
            Mob.MobType.VINDICATOR
        },
        {
            Mob.MobType.RAVAGER,
            Mob.MobType.EVOKER,
            Mob.MobType.EVOKER,
            Mob.MobType.PILLAGER,
            Mob.MobType.PILLAGER,
            Mob.MobType.PILLAGER,
            Mob.MobType.VINDICATOR,
            Mob.MobType.VINDICATOR,
            Mob.MobType.VINDICATOR
        }
    };

    private RaidMobSpawner() {
    }

    public static int spawnOneOfEach(World world, Player player, PhysicsEngine physics,
                                     EntityManager entityManager) {
        return spawnTypes(world, player, physics, entityManager, PREVIEW_RAID_MOBS);
    }

    public static int spawnWave(World world, Player player, PhysicsEngine physics,
                                EntityManager entityManager, int waveNumber) {
        return spawnTypes(world, player, physics, entityManager, waveMobs(waveNumber));
    }

    private static int spawnTypes(World world, Player player, PhysicsEngine physics,
                                  EntityManager entityManager, Mob.MobType[] mobTypes) {
        if (world == null || entityManager == null) {
            return 0;
        }
        VillageState village = world.getVillageState();
        if (village == null || !village.isPresent()) {
            return 0;
        }

        int spawned = 0;
        for (int i = 0; i < mobTypes.length; i++) {
            Mob.MobType type = mobTypes[i];
            Vector2 spawn = findRaidMobSpawn(world, village, type, i);
            if (spawn == null) {
                continue;
            }
            entityManager.addMob(new Mob(spawn.x, spawn.y, type, player, physics, world));
            spawned++;
        }
        return spawned;
    }

    public static boolean isRaidMobType(Mob.MobType type) {
        return type == Mob.MobType.PILLAGER
            || type == Mob.MobType.VINDICATOR
            || type == Mob.MobType.EVOKER
            || type == Mob.MobType.RAVAGER;
    }

    static int maxWaveCount() {
        return RAID_WAVES.length;
    }

    static Mob.MobType[] waveMobs(int waveNumber) {
        int index = Math.max(1, Math.min(waveNumber, RAID_WAVES.length)) - 1;
        return RAID_WAVES[index].clone();
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
        Vector2 spawn = SpawnSafety.findSurfaceSpawn(world,
            preferredSpawnX(village, index), SPAWN_SEARCH_RADIUS, width, height);
        if (spawn != null) {
            return spawn;
        }
        return SpawnSafety.findSurfaceSpawn(world,
            preferredSpawnX(village, index + 1), SPAWN_SEARCH_RADIUS, width, height);
    }
}
