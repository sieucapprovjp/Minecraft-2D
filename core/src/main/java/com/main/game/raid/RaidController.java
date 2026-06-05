package com.main.game.raid;

import com.badlogic.gdx.Gdx;
import com.main.game.entities.EntityManager;
import com.main.game.entities.mob.Mob;
import com.main.game.entities.player.Player;
import com.main.game.physics.PhysicsEngine;
import com.main.game.world.World;
import com.main.game.worldgen.village.VillageState;

public final class RaidController {

    public static final String RAID_BANNER_BLOCK_ID = "raid_banner";
    public static final float PREPARATION_DELAY_SECONDS = 30.0f;

    private RaidState state = RaidState.IDLE;
    private int triggerTileX = -1;
    private int triggerTileY = -1;
    private int currentWave = 0;
    private float nextWaveTimer = 0f;

    public boolean tryStartFromBanner(World world, String blockId, int tileX, int tileY) {
        if (!RAID_BANNER_BLOCK_ID.equals(blockId) || state != RaidState.IDLE || world == null) {
            return false;
        }
        VillageState village = world.getVillageState();
        if (village == null || !village.containsRaidBannerTile(tileX, tileY)) {
            return false;
        }

        state = RaidState.COUNTDOWN;
        triggerTileX = tileX;
        triggerTileY = tileY;
        currentWave = 0;
        nextWaveTimer = PREPARATION_DELAY_SECONDS;
        if (Gdx.app != null) {
            Gdx.app.log("RaidController", "Raid started from banner at " + tileX + "," + tileY);
        }
        return true;
    }

    public int update(float delta, World world, Player player, PhysicsEngine physics,
                      EntityManager entityManager) {
        if (state == RaidState.IDLE || state == RaidState.VICTORY || state == RaidState.FAILED) {
            return 0;
        }
        if (player != null && !player.isAlive()) {
            state = RaidState.FAILED;
            log("Raid failed: player died");
            return 0;
        }
        if (state == RaidState.COUNTDOWN) {
            nextWaveTimer = Math.max(0f, nextWaveTimer - Math.max(0f, delta));
            if (nextWaveTimer <= 0f) {
                return spawnNextWave(world, player, physics, entityManager);
            }
            return 0;
        }
        if (state == RaidState.WAVE_ACTIVE) {
            if (countAliveRaidMobs(entityManager) == 0) {
                if (currentWave >= getMaxWaves()) {
                    state = RaidState.VICTORY;
                    log("Raid victory after wave " + currentWave);
                } else {
                    state = RaidState.BETWEEN_WAVES;
                    nextWaveTimer = PREPARATION_DELAY_SECONDS;
                    log("Raid wave " + currentWave + " cleared");
                }
            }
            return 0;
        }
        if (state == RaidState.BETWEEN_WAVES) {
            nextWaveTimer = Math.max(0f, nextWaveTimer - Math.max(0f, delta));
            if (nextWaveTimer <= 0f) {
                return spawnNextWave(world, player, physics, entityManager);
            }
        }
        return 0;
    }

    public RaidState getState() {
        return state;
    }

    public int getTriggerTileX() {
        return triggerTileX;
    }

    public int getTriggerTileY() {
        return triggerTileY;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public int getMaxWaves() {
        return RaidMobSpawner.maxWaveCount();
    }

    public int getCurrentWaveMobCount() {
        return currentWave <= 0 ? 0 : RaidMobSpawner.waveMobCount(currentWave);
    }

    public float getNextWaveTimer() {
        return nextWaveTimer;
    }

    public void markWaveActive() {
        if (state == RaidState.COUNTDOWN) {
            state = RaidState.WAVE_ACTIVE;
        }
    }

    public static int countAliveRaidMobs(EntityManager entityManager) {
        if (entityManager == null) {
            return 0;
        }
        int count = 0;
        for (Mob mob : entityManager.getMobs()) {
            if (mob != null && mob.isAlive() && RaidMobSpawner.isRaidMobType(mob.getType())) {
                count++;
            }
        }
        return count;
    }

    private int spawnNextWave(World world, Player player, PhysicsEngine physics,
                              EntityManager entityManager) {
        int nextWave = currentWave + 1;
        if (nextWave > getMaxWaves()) {
            state = RaidState.VICTORY;
            return 0;
        }
        int spawned = RaidMobSpawner.spawnWave(world, player, physics, entityManager, nextWave);
        if (spawned <= 0) {
            state = RaidState.FAILED;
            log("Raid failed: could not spawn wave " + nextWave);
            return 0;
        }
        currentWave = nextWave;
        state = RaidState.WAVE_ACTIVE;
        nextWaveTimer = 0f;
        log("Raid wave " + currentWave + "/" + getMaxWaves() + " spawned mobs=" + spawned);
        return spawned;
    }

    private void log(String message) {
        if (Gdx.app != null) {
            Gdx.app.log("RaidController", message);
        }
    }
}
