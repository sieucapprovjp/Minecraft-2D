package com.main.game.worldgen.village;

public final class VillageSpawnPoint {

    private final int tileX;
    private final int tileY;

    public VillageSpawnPoint(int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }
}
