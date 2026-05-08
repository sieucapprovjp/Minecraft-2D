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
        STONE   = loadRegion("mvp/tiles/stone.png");
        fallbackRegion = STONE;
        GRASS   = loadRegionOrFallback("mvp/tiles/grass.png");
        BEDROCK = loadRegionOrFallback("mvp/tiles/bedrock.png");
        SAND    = loadRegionOrFallback("mvp/tiles/sand.png");
        WOOD    = loadRegionOrFallback("mvp/tiles/wood.png");
        LEAVES  = loadRegionOrFallback("mvp/tiles/leaves.png");
        PLANKS  = loadRegionOrFallback("mvp/tiles/planks.png");
        DIRT    = loadRegionOrFallback("mvp/tiles/dirt.jpg");
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
