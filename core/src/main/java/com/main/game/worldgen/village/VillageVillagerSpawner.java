package com.main.game.worldgen.village;

import com.main.game.entities.EntityManager;
import com.main.game.entities.mob.Mob;
import com.main.game.entities.mob.VillagerProfession;
import com.main.game.entities.player.Player;
import com.main.game.physics.PhysicsEngine;
import com.main.game.world.World;
import com.main.game.worldgen.SpawnSafety;

public final class VillageVillagerSpawner {

    private static final VillagerProfession[] PROFESSION_ORDER = {
        VillagerProfession.UNEMPLOYED,
        VillagerProfession.FARMER,
        VillagerProfession.BLACKSMITH
    };

    private boolean spawned;

    public int update(World world, Player player, PhysicsEngine physics, EntityManager entityManager) {
        if (spawned || !isPlayerNearVillage(world, player)) {
            return 0;
        }
        int spawnedCount = spawnNow(world, player, physics, entityManager);
        if (spawnedCount > 0) {
            spawned = true;
        }
        return spawnedCount;
    }

    public boolean hasSpawned() {
        return spawned;
    }

    static boolean isPlayerNearVillage(World world, Player player) {
        if (world == null || player == null) {
            return false;
        }
        VillageState village = world.getVillageState();
        if (village == null || !village.isPresent()) {
            return false;
        }
        float playerCenterX = player.getX() + player.getWidth() / 2f;
        return isPlayerNearVillage(village, playerCenterX);
    }

    static boolean isPlayerNearVillage(VillageState village, float playerCenterX) {
        return village != null
            && village.isPresent()
            && Math.abs(playerCenterX - village.getCenterX()) <= village.getRadius();
    }

    private static int spawnNow(World world, Player player, PhysicsEngine physics, EntityManager entityManager) {
        if (world == null || player == null || physics == null || entityManager == null) {
            return 0;
        }
        VillageState village = world.getVillageState();
        if (village == null || !village.isPresent()) {
            return 0;
        }

        int spawnWidth = Mob.getRequiredSpawnWidth(Mob.MobType.VILLAGER);
        int spawnHeight = Mob.getRequiredSpawnHeight(Mob.MobType.VILLAGER);
        int spawned = 0;
        for (VillageSpawnPoint spawnPoint : village.getVillagerSpawnPoints()) {
            int x = spawnPoint.getTileX();
            int y = spawnPoint.getTileY();
            if (!SpawnSafety.isSafeEntitySpawn(world, x, y, spawnWidth, spawnHeight)) {
                continue;
            }
            entityManager.addMob(new Mob(x + 0.1f, y, Mob.MobType.VILLAGER,
                professionForVillageIndex(spawned), player, physics, world));
            spawned++;
        }
        return spawned;
    }

    static VillagerProfession professionForVillageIndex(int index) {
        if (index < 0) {
            index = 0;
        }
        return PROFESSION_ORDER[index % PROFESSION_ORDER.length];
    }
}
