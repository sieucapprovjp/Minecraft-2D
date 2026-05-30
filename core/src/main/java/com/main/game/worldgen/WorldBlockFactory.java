package com.main.game.worldgen;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.blocks.AbstractBlock;
import com.main.game.blocks.SimpleBlock;
import com.main.game.utils.TextureManager;
import com.main.game.world.BlockPalette;

public final class WorldBlockFactory {

    private WorldBlockFactory() {
    }

    public static AbstractBlock create(int x, int y, String id) {
        if (id == null || "air".equals(id)) {
            return null;
        }
        boolean solid = !"leaves".equals(id);
        boolean breakable = !"bedrock".equals(id);
        return new SimpleBlock(x, y, id, solid, breakable, hardness(id), texture(id));
    }

    private static float hardness(String id) {
        if ("bedrock".equals(id)) return 999f;
        if (isOre(id)) return ("diamond_ore".equals(id) || "deepslate_do".equals(id)) ? 5f : 3f;
        if ("deepslate".equals(id)) return 1.8f;
        if ("stone".equals(id) || "sandstone".equals(id)) return 1.2f;
        if ("wood".equals(id) || "cactus".equals(id)) return 0.9f;
        if ("ice".equals(id)) return 0.4f;
        if ("snow".equals(id) || "leaves".equals(id)) return 0.2f;
        return 0.6f;
    }

    private static TextureRegion texture(String id) {
        if ("grass".equals(id)) return BlockPalette.getGrass();
        if ("dirt".equals(id)) return BlockPalette.getDirt();
        if ("stone".equals(id)) return BlockPalette.getStone();
        if ("bedrock".equals(id)) return BlockPalette.getBedrock();
        if ("sand".equals(id)) return BlockPalette.getSand();
        if ("wood".equals(id)) return BlockPalette.getWood();
        if ("leaves".equals(id)) return BlockPalette.getLeaves();
        if ("planks".equals(id)) return BlockPalette.getPlanks();
        if ("snow".equals(id)) return BlockPalette.getSnow();
        if ("ice".equals(id)) return BlockPalette.getIce();
        if ("sandstone".equals(id)) return BlockPalette.getSandstone();
        if ("cactus".equals(id)) return BlockPalette.getCactus();
        if ("deepslate".equals(id)) {
            TextureRegion texture = TextureManager.getInstance().getTexture(id);
            return texture != null ? texture : BlockPalette.getStone();
        }
        if (isOre(id)) {
            TextureRegion texture = TextureManager.getInstance().getTexture(id);
            return texture != null ? texture : BlockPalette.getStone();
        }
        return BlockPalette.getStone();
    }

    private static boolean isOre(String id) {
        return "coal_ore".equals(id)
            || "iron_ore".equals(id)
            || "gold_ore".equals(id)
            || "diamond_ore".equals(id)
            || "copper_ore".equals(id)
            || "lapis_ore".equals(id)
            || "redstone_ore".equals(id)
            || "emerald_ore".equals(id)
            || "deepslate_co".equals(id)
            || "deepslate_io".equals(id)
            || "deepslate_go".equals(id)
            || "deepslate_do".equals(id)
            || "deepslate_copper".equals(id)
            || "ore_lapis_deepslate".equals(id)
            || "deepslate_ro".equals(id)
            || "deepslate_eo".equals(id);
    }
}
