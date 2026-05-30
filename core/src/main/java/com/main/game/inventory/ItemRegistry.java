package com.main.game.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.utils.TextureManager;
import com.main.game.world.BlockPalette;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ItemRegistry {

    private static final Map<String, TextureRegion> TEXTURE_CACHE = new HashMap<>();
    private static final Map<String, TextureRegion> HELD_TEXTURE_CACHE = new HashMap<>();
    private static final Set<String> PLACEABLE_BLOCKS = Set.of(
        "dirt",
        "grass",
        "stone",
        "deepslate",
        "sand",
        "wood",
        "planks",
        "leaves",
        "snow",
        "ice",
        "sandstone",
        "cactus",
        "coal_ore",
        "iron_ore",
        "gold_ore",
        "diamond_ore",
        "copper_ore",
        "lapis_ore",
        "redstone_ore",
        "emerald_ore",
        "deepslate_co",
        "deepslate_io",
        "deepslate_go",
        "deepslate_do",
        "deepslate_copper",
        "ore_lapis_deepslate",
        "deepslate_ro",
        "deepslate_eo"
    );

    private ItemRegistry() {
    }

    public static int getMaxStack(String itemId) {
        if (ToolRegistry.isTool(itemId)) {
            return 1;
        }
        return 64;
    }

    public static TextureRegion getTexture(String itemId) {
        if (TEXTURE_CACHE.containsKey(itemId)) {
            return TEXTURE_CACHE.get(itemId);
        }

        TextureRegion texture = getToolTexture(itemId);
        if (texture != null) {
            TEXTURE_CACHE.put(itemId, texture);
            return texture;
        }

        texture = TextureManager.getInstance().getTexture(toTextureName(itemId));
        if (texture != null) {
            TEXTURE_CACHE.put(itemId, texture);
            return texture;
        }
        texture = getBlockPaletteTexture(itemId);
        TEXTURE_CACHE.put(itemId, texture);
        return texture;
    }

    public static TextureRegion getHeldTexture(String itemId) {
        if (HELD_TEXTURE_CACHE.containsKey(itemId)) {
            return HELD_TEXTURE_CACHE.get(itemId);
        }

        TextureRegion texture = getToolHeldTexture(itemId);
        if (texture != null) {
            HELD_TEXTURE_CACHE.put(itemId, texture);
            return texture;
        }

        texture = getTexture(itemId);
        HELD_TEXTURE_CACHE.put(itemId, texture);
        return texture;
    }

    public static boolean isPlaceableBlock(String itemId) {
        return itemId != null && !ToolRegistry.isTool(itemId) && PLACEABLE_BLOCKS.contains(itemId);
    }

    private static String toTextureName(String itemId) {
        if ("grass".equals(itemId)) return "grass";
        if ("wood".equals(itemId)) return "wood";
        if ("leaves".equals(itemId)) return "leaves";
        if ("planks".equals(itemId)) return "planks";
        if ("snow".equals(itemId)) return "snow";
        if ("ice".equals(itemId)) return "ice";
        if ("sandstone".equals(itemId)) return "sandstone";
        if ("cactus".equals(itemId)) return "cactus";
        return itemId;
    }

    private static TextureRegion getToolTexture(String itemId) {
        ToolRegistry.ToolDefinition tool = ToolRegistry.get(itemId);
        if (tool == null) {
            return null;
        }
        return TextureManager.getInstance().getTexture(tool.getTextureName());
    }

    private static TextureRegion getToolHeldTexture(String itemId) {
        ToolRegistry.ToolDefinition tool = ToolRegistry.get(itemId);
        if (tool == null) {
            return null;
        }
        return TextureManager.getInstance().getTexture(tool.getHeldTextureName());
    }

    private static TextureRegion getBlockPaletteTexture(String itemId) {
        if ("grass".equals(itemId)) return BlockPalette.getGrass();
        if ("dirt".equals(itemId)) return BlockPalette.getDirt();
        if ("stone".equals(itemId)) return BlockPalette.getStone();
        if ("sand".equals(itemId)) return BlockPalette.getSand();
        if ("wood".equals(itemId)) return BlockPalette.getWood();
        if ("leaves".equals(itemId)) return BlockPalette.getLeaves();
        if ("planks".equals(itemId)) return BlockPalette.getPlanks();
        if ("bedrock".equals(itemId)) return BlockPalette.getBedrock();
        if ("snow".equals(itemId)) return BlockPalette.getSnow();
        if ("ice".equals(itemId)) return BlockPalette.getIce();
        if ("sandstone".equals(itemId)) return BlockPalette.getSandstone();
        if ("cactus".equals(itemId)) return BlockPalette.getCactus();
        return null;
    }
}
