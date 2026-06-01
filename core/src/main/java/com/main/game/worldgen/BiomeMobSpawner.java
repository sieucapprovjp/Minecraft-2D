package com.main.game.worldgen;

import com.badlogic.gdx.math.Vector2;
import com.main.game.entities.EntityManager;
import com.main.game.entities.mob.Mob;
import com.main.game.entities.player.Player;
import com.main.game.physics.PhysicsEngine;
import com.main.game.world.World;
import java.util.ArrayList;
import java.util.Random;

public final class BiomeMobSpawner {

    private static final int INITIAL_TARGET_MOBS = 6;
    private static final int NEARBY_TOTAL_CAP = 12;
    private static final int FOREST_PASSIVE_TARGET = 7;
    private static final int HOSTILE_BIOME_HOSTILE_TARGET = 6;
    private static final int MAX_MOBS_PER_AREA = 20;
    private static final int SPAWN_AREA_WIDTH = 48;
    private static final int INITIAL_MAX_ATTEMPTS = 24;
    private static final int RUNTIME_MAX_ATTEMPTS = 8;
    private static final int INITIAL_MIN_DISTANCE = 5;
    private static final int RUNTIME_MIN_DISTANCE = 7;
    private static final int INITIAL_DISTANCE_STEP = 2;
    private static final int RUNTIME_DISTANCE_STEP = 3;
    private static final int DISTANCE_JITTER = 3;
    private static final int SPAWN_SEARCH_RADIUS = 14;
    private static final float MIN_DISTANCE_FROM_PLAYER = 4f;
    private static final float ACTIVE_SPAWN_RADIUS = 24f;
    private static final float DESPAWN_RADIUS = 36f;
    private static final float RUNTIME_SPAWN_INTERVAL = 2.5f;

    private final Random random;
    private final BiomeSpawnTable spawnTable;
    private float spawnTimer;
    private int hostileWaveRemaining;
    private BiomeType lastPlayerBiome;

    public BiomeMobSpawner(long seed) {
        this.random = new Random(seed + 404);
        this.spawnTable = new BiomeSpawnTable();
        this.spawnTimer = 1f;
    }

    public static void spawnInitialMobs(World world, Player player, PhysicsEngine physics,
                                        EntityManager entityManager, long seed) {
        new BiomeMobSpawner(seed).spawnInitial(world, player, physics, entityManager, true);
    }

    public void spawnInitial(World world, Player player, PhysicsEngine physics, EntityManager entityManager) {
        spawnInitial(world, player, physics, entityManager, true);
    }

    public void spawnInitial(World world, Player player, PhysicsEngine physics,
                             EntityManager entityManager, boolean isNight) {
        if (!canSpawn(world, player, physics, entityManager)) {
            return;
        }
        BiomeType playerBiome = world.getBiome(Math.round(player.getX()));
        lastPlayerBiome = playerBiome;
        int initialTarget = Math.min(INITIAL_TARGET_MOBS, targetForBiome(playerBiome));
        if (isHostileBiome(playerBiome)) {
            if (!isNight) {
                hostileWaveRemaining = 0;
                return;
            }
            int spawned = spawnAroundPlayer(world, player, physics, entityManager,
                1, INITIAL_MAX_ATTEMPTS, INITIAL_MIN_DISTANCE, INITIAL_DISTANCE_STEP, playerBiome);
            hostileWaveRemaining = Math.max(0, initialTarget - spawned);
            return;
        }
        spawnAroundPlayer(world, player, physics, entityManager,
            initialTarget, INITIAL_MAX_ATTEMPTS, INITIAL_MIN_DISTANCE, INITIAL_DISTANCE_STEP, playerBiome);
    }

    public void update(float delta, World world, Player player, PhysicsEngine physics, EntityManager entityManager) {
        update(delta, world, player, physics, entityManager, true);
    }

    public void update(float delta, World world, Player player, PhysicsEngine physics,
                       EntityManager entityManager, boolean isNight) {
        if (!canSpawn(world, player, physics, entityManager)) {
            return;
        }
        BiomeType playerBiome = world.getBiome(Math.round(player.getX()));
        boolean biomeChanged = lastPlayerBiome != null && lastPlayerBiome != playerBiome;
        lastPlayerBiome = playerBiome;

        despawnDistantMobs(entityManager, player);
        NearbyMobCounts nearbyMobs = countNearbyMobs(entityManager, player, ACTIVE_SPAWN_RADIUS);
        int areaSpace = MAX_MOBS_PER_AREA - countMobsInArea(entityManager, Math.round(player.getX()));
        int totalSpace = Math.min(areaSpace, NEARBY_TOTAL_CAP - nearbyMobs.total);
        if (totalSpace <= 0) {
            spawnTimer = RUNTIME_SPAWN_INTERVAL;
            return;
        }

        if (isHostileBiome(playerBiome)) {
            updateHostileBiome(delta, world, player, physics, entityManager,
                playerBiome, biomeChanged, nearbyMobs, totalSpace, isNight);
            return;
        }

        hostileWaveRemaining = 0;
        int desiredNearbyMobs = targetForBiome(playerBiome);
        int matchingNearbyMobs = matchingCountForBiome(nearbyMobs, playerBiome);
        if (matchingNearbyMobs >= desiredNearbyMobs && !biomeChanged) {
            spawnTimer = RUNTIME_SPAWN_INTERVAL;
            return;
        }

        if (biomeChanged) {
            spawnTimer = 0f;
        }
        spawnTimer -= delta;
        if (spawnTimer > 0f) {
            return;
        }
        spawnTimer = RUNTIME_SPAWN_INTERVAL;

        int missing = Math.min(totalSpace, Math.max(0, desiredNearbyMobs - matchingNearbyMobs));
        if (missing <= 0) {
            return;
        }
        int targetCount = biomeChanged ? Math.min(3, missing) : Math.min(2, missing);
        spawnAroundPlayer(world, player, physics, entityManager,
            targetCount, RUNTIME_MAX_ATTEMPTS, RUNTIME_MIN_DISTANCE, RUNTIME_DISTANCE_STEP,
            playerBiome);
    }

    private void updateHostileBiome(float delta, World world, Player player, PhysicsEngine physics,
                                    EntityManager entityManager, BiomeType playerBiome, boolean biomeChanged,
                                    NearbyMobCounts nearbyMobs, int totalSpace, boolean isNight) {
        if (biomeChanged) {
            hostileWaveRemaining = 0;
            spawnTimer = 0f;
        }
        if (!isNight) {
            hostileWaveRemaining = 0;
            spawnTimer = RUNTIME_SPAWN_INTERVAL;
            return;
        }

        if (hostileWaveRemaining <= 0) {
            if (nearbyMobs.hostile > 0) {
                spawnTimer = RUNTIME_SPAWN_INTERVAL;
                return;
            }
            hostileWaveRemaining = Math.min(HOSTILE_BIOME_HOSTILE_TARGET, totalSpace);
        }

        spawnTimer -= delta;
        if (spawnTimer > 0f) {
            return;
        }
        spawnTimer = RUNTIME_SPAWN_INTERVAL;

        int spawned = spawnAroundPlayer(world, player, physics, entityManager,
            1, RUNTIME_MAX_ATTEMPTS, RUNTIME_MIN_DISTANCE, RUNTIME_DISTANCE_STEP, playerBiome);
        if (spawned > 0) {
            hostileWaveRemaining -= spawned;
        }
    }

    private int spawnAroundPlayer(World world, Player player, PhysicsEngine physics, EntityManager entityManager,
                                  int targetCount, int maxAttempts, int minDistance, int distanceStep,
                                  BiomeType requiredBiome) {
        if (!canSpawn(world, player, physics, entityManager) || targetCount <= 0) {
            return 0;
        }
        int playerX = Math.round(player.getX());
        int spawned = 0;

        for (int i = 0; i < maxAttempts && spawned < targetCount; i++) {
            int side = chooseSide(i);
            int distance = minDistance + (i / 2) * distanceStep + random.nextInt(DISTANCE_JITTER + 1);
            int targetX = playerX + side * distance;
            BiomeType spawnBiome = world.getBiome(targetX);
            if (requiredBiome != null && spawnBiome != requiredBiome) continue;

            Mob.MobType type = spawnTable.selectMobForBiome(spawnBiome, random);
            int spawnWidth = Mob.getRequiredSpawnWidth(type);
            int spawnHeight = Mob.getRequiredSpawnHeight(type);
            Vector2 spawn = SpawnSafety.findSurfaceSpawn(world, targetX, SPAWN_SEARCH_RADIUS, spawnWidth, spawnHeight);
            if (spawn == null) continue;
            if (isTooCloseToPlayer(spawn, spawnWidth, player)) continue;
            if (countMobsInArea(entityManager, Math.round(spawn.x)) >= MAX_MOBS_PER_AREA) continue;

            entityManager.addMob(new Mob(spawn.x, spawn.y, type, player, physics, world));
            spawned++;
        }
        return spawned;
    }

    private void despawnDistantMobs(EntityManager entityManager, Player player) {
        for (Mob mob : new ArrayList<>(entityManager.getMobs())) {
            if (!mob.isAlive() || distanceFromPlayer(mob, player) <= DESPAWN_RADIUS) {
                continue;
            }
            entityManager.removeMob(mob);
        }
    }

    private NearbyMobCounts countNearbyMobs(EntityManager entityManager, Player player, float radius) {
        NearbyMobCounts counts = new NearbyMobCounts();
        for (Mob mob : entityManager.getMobs()) {
            if (mob.isAlive() && distanceFromPlayer(mob, player) <= radius) {
                counts.total++;
                if (mob.isPassive()) {
                    counts.passive++;
                } else if (mob.isHostile()) {
                    counts.hostile++;
                }
            }
        }
        return counts;
    }

    private int countMobsInArea(EntityManager entityManager, int tileX) {
        int area = areaIndex(tileX);
        int count = 0;
        for (Mob mob : entityManager.getMobs()) {
            int mobTileX = Math.round(mob.getX() + mob.getWidth() / 2f);
            if (mob.isAlive() && areaIndex(mobTileX) == area) {
                count++;
            }
        }
        return count;
    }

    private boolean canSpawn(World world, Player player, PhysicsEngine physics, EntityManager entityManager) {
        return world != null && player != null && physics != null && entityManager != null;
    }

    private int targetForBiome(BiomeType biome) {
        return biome == BiomeType.FOREST ? FOREST_PASSIVE_TARGET : HOSTILE_BIOME_HOSTILE_TARGET;
    }

    private boolean isHostileBiome(BiomeType biome) {
        return biome == BiomeType.DESERT || biome == BiomeType.SNOW;
    }

    private int matchingCountForBiome(NearbyMobCounts counts, BiomeType biome) {
        return biome == BiomeType.FOREST ? counts.passive : counts.hostile;
    }

    private int chooseSide(int attempt) {
        int side = attempt % 2 == 0 ? -1 : 1;
        return random.nextBoolean() ? side : -side;
    }

    private boolean isTooCloseToPlayer(Vector2 spawn, int spawnWidth, Player player) {
        float mobCenterX = spawn.x + spawnWidth / 2f;
        float playerCenterX = player.getX() + player.getWidth() / 2f;
        return Math.abs(mobCenterX - playerCenterX) < MIN_DISTANCE_FROM_PLAYER;
    }

    private int areaIndex(int tileX) {
        return Math.floorDiv(tileX, SPAWN_AREA_WIDTH);
    }

    private float distanceFromPlayer(Mob mob, Player player) {
        float mobCenterX = mob.getX() + mob.getWidth() / 2f;
        float playerCenterX = player.getX() + player.getWidth() / 2f;
        return Math.abs(mobCenterX - playerCenterX);
    }

    private static final class NearbyMobCounts {
        int total;
        int passive;
        int hostile;
    }
}
