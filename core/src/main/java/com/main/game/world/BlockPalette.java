package com.main.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.List;

public final class BlockPalette {

    public static final TextureRegion GRASS;
    public static final TextureRegion DIRT;
    public static final TextureRegion STONE;
    public static final TextureRegion BEDROCK;
    public static final TextureRegion SAND;
    public static final TextureRegion WOOD;
    public static final TextureRegion LEAVES;
    public static final TextureRegion PLANKS;

    private static final List<Texture> textures = new ArrayList<>();
    private static TextureRegion fallbackRegion;

    static {
        STONE   = loadRegion("organized_assets_en/images/tiles/smooth-stone.png");
        fallbackRegion = STONE;
        GRASS   = loadRegionOrFallback("organized_assets_en/images/tiles/grass.png");
        BEDROCK = loadRegionOrFallback("organized_assets_en/images/tiles/bedrock.png");
        SAND    = loadRegionOrFallback("organized_assets_en/images/tiles/sand.png");
        WOOD    = loadRegionOrFallback("organized_assets_en/images/tiles/wood.png");
        LEAVES  = loadRegionOrFallback("organized_assets_en/images/tiles/leaves.png");
        PLANKS  = loadRegionOrFallback("organized_assets_en/images/tiles/woodenplanks.png");

        // TODO(VHUNG-BLOCKS): thay bằng dirt texture thật khi team chốt asset.
        DIRT    = loadRegionOrFallback("organized_assets_en/images/tiles/coarse_dirt.png");
    }

    private BlockPalette() {
    }

    private static TextureRegion loadRegion(String path) {
        Texture texture = new Texture(path);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        textures.add(texture);
        return new TextureRegion(texture);
    }

    private static TextureRegion loadRegionOrFallback(String path) {
        try {
            return loadRegion(path);
        } catch (Exception ignored) {
            return fallbackRegion;
        }
    }

    public static void dispose() {
        for (Texture texture : textures) {
            texture.dispose();
        }
        textures.clear();
    }
}
