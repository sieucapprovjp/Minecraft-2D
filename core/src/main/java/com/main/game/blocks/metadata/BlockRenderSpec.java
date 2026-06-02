package com.main.game.blocks.metadata;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class BlockRenderSpec {

    private static final float DEFAULT_PIXELS_PER_TILE = 80f;
    private static final BlockRenderSpec FULL_TILE =
        new BlockRenderSpec(false, 1f, 1f, 0f, 0f, DEFAULT_PIXELS_PER_TILE, false);
    private static final BlockRenderSpec NATIVE_BOTTOM_CENTER =
        new BlockRenderSpec(true, 1f, 1f, 0f, 0f, DEFAULT_PIXELS_PER_TILE, true);

    private final boolean useTextureSize;
    private final float widthTiles;
    private final float heightTiles;
    private final float offsetXTiles;
    private final float offsetYTiles;
    private final float pixelsPerTile;
    private final boolean centerHorizontally;

    private BlockRenderSpec(boolean useTextureSize, float widthTiles, float heightTiles,
                            float offsetXTiles, float offsetYTiles, float pixelsPerTile,
                            boolean centerHorizontally) {
        this.useTextureSize = useTextureSize;
        this.widthTiles = widthTiles;
        this.heightTiles = heightTiles;
        this.offsetXTiles = offsetXTiles;
        this.offsetYTiles = offsetYTiles;
        this.pixelsPerTile = pixelsPerTile <= 0f ? DEFAULT_PIXELS_PER_TILE : pixelsPerTile;
        this.centerHorizontally = centerHorizontally;
    }

    public static BlockRenderSpec fullTile() {
        return FULL_TILE;
    }

    public static BlockRenderSpec nativeBottomCenter() {
        return NATIVE_BOTTOM_CENTER;
    }

    public static BlockRenderSpec fixed(float widthTiles, float heightTiles,
                                        float offsetXTiles, float offsetYTiles) {
        return new BlockRenderSpec(false, widthTiles, heightTiles, offsetXTiles, offsetYTiles,
            DEFAULT_PIXELS_PER_TILE, false);
    }

    public float width(TextureRegion texture) {
        return useTextureSize ? widthFromPixels(regionWidth(texture)) : widthTiles;
    }

    public float height(TextureRegion texture) {
        return useTextureSize ? heightFromPixels(regionHeight(texture)) : heightTiles;
    }

    public float offsetX(TextureRegion texture) {
        if (!centerHorizontally) {
            return offsetXTiles;
        }
        return offsetXTiles + (1f - width(texture)) * 0.5f;
    }

    public float offsetY(TextureRegion texture) {
        return offsetYTiles;
    }

    float widthFromPixels(int textureWidth) {
        return useTextureSize && textureWidth > 0 ? textureWidth / pixelsPerTile : widthTiles;
    }

    float heightFromPixels(int textureHeight) {
        return useTextureSize && textureHeight > 0 ? textureHeight / pixelsPerTile : heightTiles;
    }

    private int regionWidth(TextureRegion texture) {
        return texture == null ? 0 : texture.getRegionWidth();
    }

    private int regionHeight(TextureRegion texture) {
        return texture == null ? 0 : texture.getRegionHeight();
    }
}
