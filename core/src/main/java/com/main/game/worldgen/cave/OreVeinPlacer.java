package com.main.game.worldgen.cave;

import com.main.game.blocks.AbstractBlock;
import com.main.game.world.World;
import com.main.game.worldgen.WorldBlockFactory;

import java.util.Random;

public final class OreVeinPlacer {

    private static final OreConfig[] ORES = {
        new OreConfig("coal_ore", 70, 7, 24, 1.4f, 2.4f),
        new OreConfig("iron_ore", 62, 9, 26, 1.3f, 2.2f),
        new OreConfig("copper_ore", 42, 8, 22, 1.2f, 2.0f),
        new OreConfig("gold_ore", 30, 15, 28, 1.1f, 1.8f),
        new OreConfig("redstone_ore", 24, 18, 29, 1.1f, 1.8f),
        new OreConfig("lapis_ore", 16, 15, 26, 1.0f, 1.6f),
        new OreConfig("diamond_ore", 11, 22, 29, 0.8f, 1.4f),
        new OreConfig("emerald_ore", 8, 18, 28, 0.7f, 1.2f)
    };

    private OreVeinPlacer() {
    }

    public static void place(World world, long seed) {
        Random random = new Random(seed ^ 0x0F3A12E5L);
        for (OreConfig ore : ORES) {
            int attempts = Math.max(1, Math.round(ore.attemptsPer400Tiles * (world.width / 400f)));
            for (int i = 0; i < attempts; i++) {
                int x = random.nextInt(world.width);
                int y = pickY(world, x, random, ore);
                if (y < 0) continue;
                float radius = ore.minRadius + random.nextFloat() * (ore.maxRadius - ore.minRadius);
                placeVein(world, ore.id, x, y, radius);
            }
        }
    }

    private static int pickY(World world, int x, Random random, OreConfig ore) {
        int surface = world.getSurfaceY(x);
        if (surface < 0) return -1;

        int minY = Math.max(3, surface - ore.maxDepthFromSurface);
        int maxY = Math.min(world.height - 2, surface - ore.minDepthFromSurface);
        if (maxY <= minY) return -1;

        return minY + random.nextInt(maxY - minY + 1);
    }

    private static void placeVein(World world, String oreId, int centerX, int centerY, float radius) {
        int minX = Math.max(0, (int) Math.floor(centerX - radius));
        int maxX = Math.min(world.width - 1, (int) Math.ceil(centerX + radius));
        int minY = Math.max(0, (int) Math.floor(centerY - radius));
        int maxY = Math.min(world.height - 1, (int) Math.ceil(centerY + radius));

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                float dx = x - centerX;
                float dy = y - centerY;
                if ((dx * dx) + (dy * dy) <= radius * radius) {
                    String targetBlockId = getReplaceableBlockId(world, x, y);
                    if (targetBlockId != null) {
                        world.setBlock(x, y, WorldBlockFactory.create(x, y, oreIdForLayer(oreId, targetBlockId)));
                    }
                }
            }
        }
    }

    private static String getReplaceableBlockId(World world, int x, int y) {
        if (!world.isInBounds(x, y)) return null;
        AbstractBlock block = world.getBlock(x, y);
        if (block == null) return null;
        String blockId = block.getBlockId();
        return "stone".equals(blockId) || "deepslate".equals(blockId) ? blockId : null;
    }

    private static String oreIdForLayer(String oreId, String targetBlockId) {
        if (!"deepslate".equals(targetBlockId)) return oreId;
        if ("coal_ore".equals(oreId)) return "deepslate_co";
        if ("iron_ore".equals(oreId)) return "deepslate_io";
        if ("gold_ore".equals(oreId)) return "deepslate_go";
        if ("diamond_ore".equals(oreId)) return "deepslate_do";
        if ("copper_ore".equals(oreId)) return "deepslate_copper";
        if ("lapis_ore".equals(oreId)) return "ore_lapis_deepslate";
        if ("redstone_ore".equals(oreId)) return "deepslate_ro";
        if ("emerald_ore".equals(oreId)) return "deepslate_eo";
        return oreId;
    }

    private static final class OreConfig {
        final String id;
        final int attemptsPer400Tiles;
        final int minDepthFromSurface;
        final int maxDepthFromSurface;
        final float minRadius;
        final float maxRadius;

        OreConfig(String id, int attemptsPer400Tiles, int minDepthFromSurface,
                  int maxDepthFromSurface, float minRadius, float maxRadius) {
            this.id = id;
            this.attemptsPer400Tiles = attemptsPer400Tiles;
            this.minDepthFromSurface = minDepthFromSurface;
            this.maxDepthFromSurface = maxDepthFromSurface;
            this.minRadius = minRadius;
            this.maxRadius = maxRadius;
        }
    }
}
