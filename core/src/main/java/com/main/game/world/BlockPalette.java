package com.main.game.world;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class BlockPalette {

    public static final TextureRegion GRASS;
    public static final TextureRegion DIRT;
    public static final TextureRegion STONE;
    public static final TextureRegion BEDROCK;

    private static final Texture texture;

    static {
        Pixmap pixmap = new Pixmap(4, 1, Pixmap.Format.RGBA8888);
        pixmap.drawPixel(0, 0, rgba(95, 159, 53));
        pixmap.drawPixel(1, 0, rgba(122, 85, 49));
        pixmap.drawPixel(2, 0, rgba(116, 116, 116));
        pixmap.drawPixel(3, 0, rgba(61, 61, 61));

        texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        GRASS = new TextureRegion(texture, 0, 0, 1, 1);
        DIRT = new TextureRegion(texture, 1, 0, 1, 1);
        STONE = new TextureRegion(texture, 2, 0, 1, 1);
        BEDROCK = new TextureRegion(texture, 3, 0, 1, 1);

        pixmap.dispose();
    }

    private BlockPalette() {
    }

    private static int rgba(int r, int g, int b) {
        return (r << 24) | (g << 16) | (b << 8) | 255;
    }

    public static void dispose() {
        texture.dispose();
    }
}
