package com.main.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.files.FileHandle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextureManager {
    private static TextureManager instance;
    private TextureAtlas atlas;
    private final List<Texture> ownedTextures = new ArrayList<>();
    private final Map<String, TextureRegion> generatedFallbacks = new HashMap<>();

    private TextureManager() {
        // Tự động load atlas từ đường dẫn trong Constants của bạn
        try {
            FileHandle atlasFile = Gdx.files.internal(Constants.TEXTURE_ATLAS_PATH);
            if (atlasFile.exists()) {
                atlas = new TextureAtlas(atlasFile);
            } else {
                atlas = null;
            }
        } catch (Exception e) {
            atlas = null;
        }
    }

    public static TextureManager getInstance() {
        if (instance == null) instance = new TextureManager();
        return instance;
    }

    public TextureRegion getTexture(String name) {
        // If libGDX file resolver is not initialized yet (no Application created), bail out.
        if (Gdx.files == null) {
            System.err.println("Gdx.files not initialized yet; cannot load texture: " + name);
            return null;
        }
        if (atlas != null) {
            TextureRegion r = atlas.findRegion(name);
            if (r != null) return r;
        }

        // Fallback: try to load individual image files from common asset locations.
        // Provide some common name mappings for convenience.
        java.util.Map<String, String> nameMap = new java.util.HashMap<>();
        nameMap.put("grass_block", "grass");
        nameMap.put("oak_log", "wood");
        nameMap.put("oak_leaves", "leaves");
        nameMap.put("oak_planks", "planks");
        nameMap.put("dirt", "dirt");
        nameMap.put("snow", "tiles/snowy/snow/snow");
        nameMap.put("ice", "tiles/snowy/ice/ice");
        nameMap.put("sand", "tiles/desert/sand/sand");
        nameMap.put("sandstone", "tiles/desert/sand/sandstone");
        nameMap.put("cactus", "tiles/desert/cactus/cactus");
        nameMap.put("nether_quartz", "quartz_ore");
        nameMap.put("deepslate", "tiles/cave/natural/deepslate");
        nameMap.put("deepslate_co", "tiles/cave/ores_deepslate/deepslate_co");
        nameMap.put("deepslate_io", "tiles/cave/ores_deepslate/deepslate_io");
        nameMap.put("deepslate_go", "tiles/cave/ores_deepslate/deepslate_go");
        nameMap.put("deepslate_do", "tiles/cave/ores_deepslate/deepslate_do");
        nameMap.put("deepslate_copper", "tiles/cave/ores_deepslate/deepslate_copper");
        nameMap.put("ore_lapis_deepslate", "tiles/cave/ores_deepslate/ore_lapis_deepslate");
        nameMap.put("deepslate_ro", "tiles/cave/ores_deepslate/deepslate_ro");
        nameMap.put("deepslate_eo", "tiles/cave/ores_deepslate/deepslate_eo");

        String base = nameMap.getOrDefault(name, name);

        String[] searchDirs = new String[] {"", "atlas/", "items/", "mvp/tiles/", "mvp/player/", "mvp/ui/", "util_block/", "images/gui_invrow/", "Ores/", "tiles/cave/Ores/"};
        String[] exts = new String[] {".png", ".jpg", ".jpeg"};

        for (String dir : searchDirs) {
            for (String ext : exts) {
                String path = dir + base + ext;
                FileHandle fh = Gdx.files.internal(path);
                if (fh.exists()) {
                    try {
                        Texture t = new Texture(fh);
                        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                        ownedTextures.add(t);
                        return new TextureRegion(t);
                    } catch (Exception e) {
                        // continue searching
                    }
                }
            }
        }

        // Last resort: try variants (remove suffixes like _block)
        if (base.endsWith("_block")) {
            return getTexture(base.substring(0, base.length() - 6));
        }

        TextureRegion generatedOre = generatedOreFallback(name);
        if (generatedOre != null) return generatedOre;

        return null;
    }

    public void dispose() {
        if (atlas != null) atlas.dispose();
        for (Texture t : ownedTextures) t.dispose();
        ownedTextures.clear();
        generatedFallbacks.clear();
    }

    private TextureRegion generatedOreFallback(String name) {
        Color color = oreColor(name);
        if (color == null) return null;
        if (generatedFallbacks.containsKey(name)) return generatedFallbacks.get(name);

        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.35f, 0.35f, 0.35f, 1f));
        pixmap.fill();
        pixmap.setColor(new Color(0.24f, 0.24f, 0.24f, 1f));
        for (int y = 0; y < 16; y += 4) {
            pixmap.drawLine(0, y, 15, y);
        }
        pixmap.setColor(color);
        pixmap.fillCircle(4, 5, 2);
        pixmap.fillCircle(10, 4, 2);
        pixmap.fillCircle(7, 11, 2);

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
        ownedTextures.add(texture);

        TextureRegion region = new TextureRegion(texture);
        generatedFallbacks.put(name, region);
        return region;
    }

    private Color oreColor(String name) {
        if ("coal_ore".equals(name)) return new Color(0.08f, 0.08f, 0.08f, 1f);
        if ("iron_ore".equals(name)) return new Color(0.72f, 0.52f, 0.36f, 1f);
        if ("gold_ore".equals(name)) return new Color(1f, 0.78f, 0.18f, 1f);
        if ("diamond_ore".equals(name)) return new Color(0.2f, 0.9f, 1f, 1f);
        if ("copper_ore".equals(name)) return new Color(0.9f, 0.45f, 0.2f, 1f);
        if ("lapis_ore".equals(name)) return new Color(0.12f, 0.22f, 0.9f, 1f);
        if ("redstone_ore".equals(name)) return new Color(0.9f, 0.05f, 0.04f, 1f);
        if ("emerald_ore".equals(name)) return new Color(0.1f, 0.85f, 0.32f, 1f);
        if ("quartz_ore".equals(name) || "nether_quartz".equals(name)) return new Color(0.92f, 0.86f, 0.76f, 1f);
        if ("deepslate_co".equals(name)) return new Color(0.08f, 0.08f, 0.08f, 1f);
        if ("deepslate_io".equals(name)) return new Color(0.72f, 0.52f, 0.36f, 1f);
        if ("deepslate_go".equals(name)) return new Color(1f, 0.78f, 0.18f, 1f);
        if ("deepslate_do".equals(name)) return new Color(0.2f, 0.9f, 1f, 1f);
        if ("deepslate_copper".equals(name)) return new Color(0.9f, 0.45f, 0.2f, 1f);
        if ("ore_lapis_deepslate".equals(name)) return new Color(0.12f, 0.22f, 0.9f, 1f);
        if ("deepslate_ro".equals(name)) return new Color(0.9f, 0.05f, 0.04f, 1f);
        if ("deepslate_eo".equals(name)) return new Color(0.1f, 0.85f, 0.32f, 1f);
        return null;
    }
}
