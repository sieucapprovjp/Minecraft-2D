package com.main.game.worldgen.cave;

import com.main.game.blocks.AbstractBlock;
import com.main.game.world.World;

import java.util.Random;

public final class CaveCarver {

    private static final int SURFACE_BUFFER = 6;
    private static final int MIN_WORLD_Y = World.BEDROCK_TOP_Y;
    private static final int SPAWN_PROTECT_RADIUS = 14;
    private static final int SPAWN_PROTECT_DEPTH = 14;
    private static final float MIN_RADIUS = 1.4f;
    private static final float MAX_RADIUS = 3.4f;

    private CaveCarver() {
    }

    public static void carve(World world, long seed) {
        Random random = new Random(seed ^ 0x5CA1AB1EL);
        int caveSystems = Math.max(10, world.width / 28);

        for (int i = 0; i < caveSystems; i++) {
            int startX = 4 + random.nextInt(Math.max(1, world.width - 8));
            int startY = pickStartY(world, startX, random);
            if (startY < 0) continue;

            float radius = 2.0f + random.nextFloat() * 2.2f;
            carveBlob(world, startX, startY, radius);

            int tunnels = 1 + random.nextInt(2);
            for (int t = 0; t < tunnels; t++) {
                double direction = (random.nextBoolean() ? 0.0 : Math.PI)
                    + ((random.nextFloat() - 0.5f) * Math.PI * 0.55f);
                int length = 18 + random.nextInt(42);
                carveTunnel(world, random, startX, startY, direction, radius, length, 0);
            }
        }
    }

    private static int pickStartY(World world, int x, Random random) {
        int surface = world.getSurfaceY(x);
        if (surface < 0) return -1;

        int minY = Math.max(MIN_WORLD_Y + 1, surface - 28);
        int maxY = surface - SURFACE_BUFFER;
        if (maxY <= minY) return -1;

        return minY + random.nextInt(maxY - minY + 1);
    }

    private static void carveTunnel(World world, Random random, float x, float y,
                                    double direction, float radius, int length, int depth) {
        for (int step = 0; step < length; step++) {
            int tileX = Math.round(x);
            int tileY = Math.round(y);
            if (tileX < 1 || tileX >= world.width - 1 || tileY <= MIN_WORLD_Y || tileY >= world.height - 1) {
                return;
            }

            int surface = world.getSurfaceY(tileX);
            if (surface < 0 || tileY > surface - SURFACE_BUFFER) {
                return;
            }

            carveBlob(world, tileX, tileY, radius);

            if (depth == 0 && step > 8 && random.nextFloat() < 0.035f) {
                double branchDirection = direction + ((random.nextFloat() - 0.5f) * Math.PI * 0.85f);
                carveTunnel(world, random, x, y, branchDirection, radius * 0.72f,
                    Math.max(8, length / 2), depth + 1);
            }

            direction += (random.nextFloat() - 0.5f) * 0.28f;
            radius = clamp(radius + (random.nextFloat() - 0.5f) * 0.18f, MIN_RADIUS, MAX_RADIUS);
            x += Math.cos(direction) * 0.95f;
            y += Math.sin(direction) * 0.55f;
        }
    }

    private static void carveBlob(World world, int centerX, int centerY, float radius) {
        int minX = Math.max(0, (int) Math.floor(centerX - radius));
        int maxX = Math.min(world.width - 1, (int) Math.ceil(centerX + radius));
        float verticalRadius = Math.max(1.0f, radius * 0.75f);
        int minY = Math.max(0, (int) Math.floor(centerY - verticalRadius));
        int maxY = Math.min(world.height - 1, (int) Math.ceil(centerY + verticalRadius));

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                float dx = x - centerX;
                float dy = y - centerY;
                float normalized = (dx * dx) / (radius * radius)
                    + (dy * dy) / (verticalRadius * verticalRadius);
                if (normalized <= 1f && canCarve(world, x, y)) {
                    world.setBlock(x, y, null);
                }
            }
        }
    }

    private static boolean canCarve(World world, int x, int y) {
        if (!world.isInBounds(x, y) || y <= MIN_WORLD_Y) return false;
        int surface = world.getSurfaceY(x);
        if (surface < 0 || y > surface - SURFACE_BUFFER) return false;
        if (isNearSurfaceSpawn(world, x, y, surface)) return false;

        AbstractBlock block = world.getBlock(x, y);
        return block != null && ("stone".equals(block.getBlockId()) || "deepslate".equals(block.getBlockId()));
    }

    private static boolean isNearSurfaceSpawn(World world, int x, int y, int surface) {
        return Math.abs(x - (world.width / 2)) <= SPAWN_PROTECT_RADIUS
            && y >= surface - SPAWN_PROTECT_DEPTH;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
