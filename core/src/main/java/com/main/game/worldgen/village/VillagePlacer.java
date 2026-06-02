package com.main.game.worldgen.village;

import com.main.game.world.World;
import com.main.game.worldgen.BiomeType;
import com.main.game.worldgen.WorldBlockFactory;

public final class VillagePlacer {

    public static final int HOUSE_WIDTH = 21;
    public static final int HOUSE_HEIGHT = 8;
    public static final int VILLAGE_RADIUS = 28;

    private static final int HOUSE_CLEAR_PADDING = 2;
    private static final int MAX_SITE_SURFACE_DELTA = 2;

    private VillagePlacer() {
    }

    public static VillageState place(World world, long seed, int plainsCenterX, int plainsHalfWidth) {
        if (world == null) return VillageState.none();
        int houseBaseX = clamp(plainsCenterX - HOUSE_WIDTH / 2, 2, world.width - HOUSE_WIDTH - 2);
        if (!isValidPlainsSite(world, houseBaseX, plainsCenterX, plainsHalfWidth)) {
            return VillageState.none();
        }

        int floorY = findFloorY(world, houseBaseX);
        if (floorY <= World.DEEPSLATE_TOP_Y || floorY + HOUSE_HEIGHT + 1 >= world.height) {
            return VillageState.none();
        }

        int clearMinX = houseBaseX - HOUSE_CLEAR_PADDING;
        int clearMaxX = houseBaseX + HOUSE_WIDTH + HOUSE_CLEAR_PADDING - 1;
        prepareFoundation(world, clearMinX, clearMaxX, floorY);
        clearAirSpace(world, clearMinX, floorY + 1, clearMaxX, floorY + HOUSE_HEIGHT + 1);
        placeLargeHouse(world, houseBaseX, floorY);

        int centerX = houseBaseX + HOUSE_WIDTH / 2;
        return VillageState.present(centerX, floorY + 1, houseBaseX, floorY, HOUSE_WIDTH, VILLAGE_RADIUS);
    }

    private static boolean isValidPlainsSite(World world, int houseBaseX, int plainsCenterX, int plainsHalfWidth) {
        int minX = houseBaseX - HOUSE_CLEAR_PADDING;
        int maxX = houseBaseX + HOUSE_WIDTH + HOUSE_CLEAR_PADDING - 1;
        int minSurface = Integer.MAX_VALUE;
        int maxSurface = Integer.MIN_VALUE;

        for (int x = minX; x <= maxX; x++) {
            if (!world.isInBounds(x, 1)) return false;
            if (Math.abs(x - plainsCenterX) > plainsHalfWidth) return false;
            if (world.getBiome(x) != BiomeType.PLAINS) return false;
            int surface = world.getSurfaceY(x);
            if (surface <= World.DEEPSLATE_TOP_Y) return false;
            minSurface = Math.min(minSurface, surface);
            maxSurface = Math.max(maxSurface, surface);
        }
        return maxSurface - minSurface <= MAX_SITE_SURFACE_DELTA;
    }

    private static int findFloorY(World world, int houseBaseX) {
        int floorY = 0;
        for (int x = houseBaseX; x < houseBaseX + HOUSE_WIDTH; x++) {
            floorY = Math.max(floorY, world.getSurfaceY(x));
        }
        return floorY;
    }

    private static void prepareFoundation(World world, int minX, int maxX, int floorY) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = Math.max(World.BEDROCK_TOP_Y + 1, floorY - 3); y < floorY; y++) {
                if (world.getBlock(x, y) == null) {
                    world.setBlock(x, y, WorldBlockFactory.create(x, y, "dirt"));
                }
            }
            world.setSurfaceY(x, floorY);
        }
    }

    private static void clearAirSpace(World world, int minX, int minY, int maxX, int maxY) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (world.isInBounds(x, y)) {
                    world.setBlock(x, y, null);
                }
            }
        }
    }

    private static void placeLargeHouse(World world, int baseX, int floorY) {
        for (int x = baseX; x < baseX + HOUSE_WIDTH; x++) {
            world.setBlock(x, floorY, WorldBlockFactory.create(x, floorY, "planks"));
        }

        for (int x = baseX + 2; x < baseX + HOUSE_WIDTH - 2; x++) {
            world.setBlock(x, floorY + 5, WorldBlockFactory.create(x, floorY + 5, "planks"));
        }

        for (int x = baseX; x < baseX + HOUSE_WIDTH; x++) {
            world.setBlock(x, floorY + 6, WorldBlockFactory.create(x, floorY + 6, "wood"));
        }

        for (int x = baseX + 2; x < baseX + HOUSE_WIDTH - 2; x++) {
            world.setBlock(x, floorY + 7, WorldBlockFactory.create(x, floorY + 7, "wood"));
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
