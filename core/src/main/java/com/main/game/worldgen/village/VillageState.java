package com.main.game.worldgen.village;

import java.util.Collections;
import java.util.List;

public final class VillageState {

    private static final VillageState NONE = new VillageState(false, 0, 0, 0, 0, 0, 0, Collections.emptyList());

    private final boolean present;
    private final int centerX;
    private final int centerY;
    private final int houseBaseX;
    private final int houseFloorY;
    private final int houseWidth;
    private final int radius;
    private final List<VillageSpawnPoint> villagerSpawnPoints;

    private VillageState(boolean present, int centerX, int centerY, int houseBaseX,
                         int houseFloorY, int houseWidth, int radius,
                         List<VillageSpawnPoint> villagerSpawnPoints) {
        this.present = present;
        this.centerX = centerX;
        this.centerY = centerY;
        this.houseBaseX = houseBaseX;
        this.houseFloorY = houseFloorY;
        this.houseWidth = houseWidth;
        this.radius = radius;
        this.villagerSpawnPoints = List.copyOf(villagerSpawnPoints);
    }

    public static VillageState none() {
        return NONE;
    }

    public static VillageState present(int centerX, int centerY, int houseBaseX,
                                       int houseFloorY, int houseWidth, int radius) {
        return present(centerX, centerY, houseBaseX, houseFloorY, houseWidth, radius, Collections.emptyList());
    }

    public static VillageState present(int centerX, int centerY, int houseBaseX,
                                       int houseFloorY, int houseWidth, int radius,
                                       List<VillageSpawnPoint> villagerSpawnPoints) {
        return new VillageState(true, centerX, centerY, houseBaseX, houseFloorY, houseWidth, radius,
            villagerSpawnPoints == null ? Collections.emptyList() : villagerSpawnPoints);
    }

    public boolean isPresent() {
        return present;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getHouseBaseX() {
        return houseBaseX;
    }

    public int getHouseFloorY() {
        return houseFloorY;
    }

    public int getHouseWidth() {
        return houseWidth;
    }

    public int getRadius() {
        return radius;
    }

    public List<VillageSpawnPoint> getVillagerSpawnPoints() {
        return villagerSpawnPoints;
    }

    public boolean containsRaidBannerTile(int tileX, int tileY) {
        if (!present) return false;
        return tileX >= houseBaseX
            && tileX < houseBaseX + houseWidth
            && tileY >= houseFloorY + 1
            && tileY <= houseFloorY + 4;
    }
}
