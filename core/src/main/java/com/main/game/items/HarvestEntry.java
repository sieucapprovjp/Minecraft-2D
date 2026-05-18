package com.main.game.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.world.World;

public class HarvestEntry {

    public static final float RANDOM_VERTICAL_SPEED = -999f;

    private final int tileIdx;
    private final String itemId;
    private final TextureRegion texture;
    private final int count;
    private final float sx;
    private final float sy;

    public HarvestEntry(int tileIdx, String itemId, TextureRegion texture, int count, float sx, float sy) {
        this.tileIdx = tileIdx;
        this.itemId = itemId;
        this.texture = texture;
        this.count = count;
        this.sx = sx;
        this.sy = sy;
    }

    public static int toTileIdx(int tileX, int tileY, World world) {
        return tileY * world.width + tileX + 1;
    }

    public float getWorldX(World world) {
        return ((tileIdx - 1) % world.width) + 0.5f;
    }

    public float getWorldY(World world) {
        return ((tileIdx - 1) / world.width) + 0.5f;
    }

    public int getTileIdx() {
        return tileIdx;
    }

    public String getItemId() {
        return itemId;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public int getCount() {
        return count;
    }

    public float getSx() {
        return sx;
    }

    public float getSy() {
        return sy;
    }
}
