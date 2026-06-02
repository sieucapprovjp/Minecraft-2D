package com.main.game.worldgen.village;

import com.main.game.world.World;
import com.main.game.worldgen.BiomeType;
import com.main.game.worldgen.WorldBlockFactory;
import java.util.List;

public final class VillagePlacer {

    public static final int HOUSE_WIDTH = 21;
    public static final int HOUSE_HEIGHT = 9;
    public static final int VILLAGE_RADIUS = 42;

    private static final int SMALL_HOUSE_WIDTH = 13;
    private static final int SIDE_HOUSE_GAP = 6;
    private static final int HOUSE_CLEAR_PADDING = 2;
    private static final int MAX_SITE_SURFACE_DELTA = 2;
    private static final String COBBLESTONE_FLOOR_BLOCK = "village_cobblestone";
    private static final String MOSS_STONE_FLOOR_BLOCK = "village_moss_stone";
    private static final String ROOF_BLOCK = "planks";
    private static final String SIDE_SUPPORT_BLOCK = "village_cobblestone";

    private VillagePlacer() {
    }

    public static VillageState place(World world, long seed, int plainsCenterX, int plainsHalfWidth) {
        if (world == null) return VillageState.none();
        int houseBaseX = clamp(plainsCenterX - HOUSE_WIDTH / 2,
            SMALL_HOUSE_WIDTH + SIDE_HOUSE_GAP + HOUSE_CLEAR_PADDING,
            world.width - HOUSE_WIDTH - SIDE_HOUSE_GAP - SMALL_HOUSE_WIDTH - HOUSE_CLEAR_PADDING);
        int leftHouseBaseX = leftSmallHouseBaseX(houseBaseX);
        int rightHouseBaseX = rightSmallHouseBaseX(houseBaseX);
        int clearMinX = leftHouseBaseX - HOUSE_CLEAR_PADDING;
        int clearMaxX = rightHouseBaseX + SMALL_HOUSE_WIDTH + HOUSE_CLEAR_PADDING - 1;
        if (!isValidPlainsSite(world, clearMinX, clearMaxX, plainsCenterX, plainsHalfWidth)) {
            return VillageState.none();
        }

        int floorY = findFloorY(world, clearMinX, clearMaxX);
        if (floorY <= World.DEEPSLATE_TOP_Y || floorY + HOUSE_HEIGHT + 1 >= world.height) {
            return VillageState.none();
        }

        prepareFoundation(world, clearMinX, clearMaxX, floorY);
        clearAirSpace(world, clearMinX, floorY + 1, clearMaxX, floorY + HOUSE_HEIGHT + 1);
        placeLargeHouse(world, houseBaseX, floorY);
        placeSmallHouse(world, leftHouseBaseX, floorY, false);
        placeSmallHouse(world, rightHouseBaseX, floorY, true);

        int centerX = houseBaseX + HOUSE_WIDTH / 2;
        return VillageState.present(centerX, floorY + 1, houseBaseX, floorY, HOUSE_WIDTH, VILLAGE_RADIUS,
            List.of(
                new VillageSpawnPoint(leftHouseBaseX + SMALL_HOUSE_WIDTH / 2, floorY + 1),
                new VillageSpawnPoint(centerX, floorY + 1),
                new VillageSpawnPoint(rightHouseBaseX + SMALL_HOUSE_WIDTH / 2, floorY + 1)
            ));
    }

    private static boolean isValidPlainsSite(World world, int minX, int maxX, int plainsCenterX, int plainsHalfWidth) {
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

    private static int findFloorY(World world, int minX, int maxX) {
        int floorY = 0;
        for (int x = minX; x <= maxX; x++) {
            floorY = Math.max(floorY, world.getSurfaceY(x));
        }
        return floorY;
    }

    private static void prepareFoundation(World world, int minX, int maxX, int floorY) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = Math.max(World.BEDROCK_TOP_Y + 1, floorY - 3); y <= floorY; y++) {
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
        placeFloor(world, baseX, floorY, HOUSE_WIDTH);

        placeRow(world, baseX - 2, floorY + 5, HOUSE_WIDTH + 4, ROOF_BLOCK);
        placeRow(world, baseX - 1, floorY + 6, HOUSE_WIDTH + 2, ROOF_BLOCK);
        placeRow(world, baseX + 1, floorY + 7, HOUSE_WIDTH - 2, ROOF_BLOCK);
        placeRow(world, baseX + 3, floorY + 8, HOUSE_WIDTH - 6, ROOF_BLOCK);

        world.setBlock(baseX, floorY + 3, WorldBlockFactory.create(baseX, floorY + 3, SIDE_SUPPORT_BLOCK));
        world.setBlock(baseX, floorY + 4, WorldBlockFactory.create(baseX, floorY + 4, SIDE_SUPPORT_BLOCK));
        world.setBlock(baseX + HOUSE_WIDTH - 1, floorY + 3,
            WorldBlockFactory.create(baseX + HOUSE_WIDTH - 1, floorY + 3, SIDE_SUPPORT_BLOCK));
        world.setBlock(baseX + HOUSE_WIDTH - 1, floorY + 4,
            WorldBlockFactory.create(baseX + HOUSE_WIDTH - 1, floorY + 4, SIDE_SUPPORT_BLOCK));
        placeDecorations(world, baseX, floorY);
    }

    private static void placeSmallHouse(World world, int baseX, int floorY, boolean mirrorInterior) {
        placeFloor(world, baseX, floorY, SMALL_HOUSE_WIDTH);

        placeRow(world, baseX - 2, floorY + 4, SMALL_HOUSE_WIDTH + 4, ROOF_BLOCK);
        placeRow(world, baseX, floorY + 5, SMALL_HOUSE_WIDTH, ROOF_BLOCK);
        placeRow(world, baseX + 2, floorY + 6, SMALL_HOUSE_WIDTH - 4, ROOF_BLOCK);

        world.setBlock(baseX, floorY + 2, WorldBlockFactory.create(baseX, floorY + 2, SIDE_SUPPORT_BLOCK));
        world.setBlock(baseX, floorY + 3, WorldBlockFactory.create(baseX, floorY + 3, SIDE_SUPPORT_BLOCK));
        world.setBlock(baseX + SMALL_HOUSE_WIDTH - 1, floorY + 2,
            WorldBlockFactory.create(baseX + SMALL_HOUSE_WIDTH - 1, floorY + 2, SIDE_SUPPORT_BLOCK));
        world.setBlock(baseX + SMALL_HOUSE_WIDTH - 1, floorY + 3,
            WorldBlockFactory.create(baseX + SMALL_HOUSE_WIDTH - 1, floorY + 3, SIDE_SUPPORT_BLOCK));
        placeSmallHouseDecorations(world, baseX, floorY, mirrorInterior);
    }

    private static void placeDecorations(World world, int baseX, int floorY) {
        world.setBlock(baseX - 2, floorY + 5, WorldBlockFactory.create(baseX - 2, floorY + 5, "village_roof_stair_left"));
        world.setBlock(baseX + HOUSE_WIDTH + 1, floorY + 5,
            WorldBlockFactory.create(baseX + HOUSE_WIDTH + 1, floorY + 5, "village_roof_stair_right"));
        world.setBlock(baseX - 1, floorY + 6, WorldBlockFactory.create(baseX - 1, floorY + 6, "village_roof_stair_left"));
        world.setBlock(baseX + HOUSE_WIDTH, floorY + 6,
            WorldBlockFactory.create(baseX + HOUSE_WIDTH, floorY + 6, "village_roof_stair_right"));
        world.setBlock(baseX + 1, floorY + 7, WorldBlockFactory.create(baseX + 1, floorY + 7, "village_roof_stair_left"));
        world.setBlock(baseX + HOUSE_WIDTH - 2, floorY + 7,
            WorldBlockFactory.create(baseX + HOUSE_WIDTH - 2, floorY + 7, "village_roof_stair_right"));

        world.setBlock(baseX + 5, floorY + 4, WorldBlockFactory.create(baseX + 5, floorY + 4, "village_glass2"));
        world.setBlock(baseX + 6, floorY + 4, WorldBlockFactory.create(baseX + 6, floorY + 4, "village_glass3"));
        world.setBlock(baseX + 14, floorY + 4, WorldBlockFactory.create(baseX + 14, floorY + 4, "village_glass2"));
        world.setBlock(baseX + 15, floorY + 4, WorldBlockFactory.create(baseX + 15, floorY + 4, "village_glass3"));

        world.setBlock(baseX + 3, floorY + 1, WorldBlockFactory.create(baseX + 3, floorY + 1, "village_bookshelf"));
        world.setBlock(baseX + 4, floorY + 1, WorldBlockFactory.create(baseX + 4, floorY + 1, "village_bookshelf"));
        world.setBlock(baseX + 15, floorY + 1, WorldBlockFactory.create(baseX + 15, floorY + 1, "village_bed_left"));
        world.setBlock(baseX + 16, floorY + 1, WorldBlockFactory.create(baseX + 16, floorY + 1, "village_bed_right"));
    }

    private static void placeSmallHouseDecorations(World world, int baseX, int floorY, boolean mirrorInterior) {
        world.setBlock(baseX - 2, floorY + 4, WorldBlockFactory.create(baseX - 2, floorY + 4, "village_roof_stair_left"));
        world.setBlock(baseX + SMALL_HOUSE_WIDTH + 1, floorY + 4,
            WorldBlockFactory.create(baseX + SMALL_HOUSE_WIDTH + 1, floorY + 4, "village_roof_stair_right"));
        world.setBlock(baseX, floorY + 5, WorldBlockFactory.create(baseX, floorY + 5, "village_roof_stair_left"));
        world.setBlock(baseX + SMALL_HOUSE_WIDTH - 1, floorY + 5,
            WorldBlockFactory.create(baseX + SMALL_HOUSE_WIDTH - 1, floorY + 5, "village_roof_stair_right"));

        world.setBlock(baseX + 5, floorY + 3, WorldBlockFactory.create(baseX + 5, floorY + 3, "village_glass2"));
        world.setBlock(baseX + 6, floorY + 3, WorldBlockFactory.create(baseX + 6, floorY + 3, "village_glass3"));

        int bedLeftX = mirrorInterior ? baseX + 2 : baseX + 8;
        int shelfX = mirrorInterior ? baseX + 8 : baseX + 2;
        world.setBlock(shelfX, floorY + 1, WorldBlockFactory.create(shelfX, floorY + 1, "village_bookshelf"));
        world.setBlock(shelfX + 1, floorY + 1, WorldBlockFactory.create(shelfX + 1, floorY + 1, "village_bookshelf"));
        world.setBlock(bedLeftX, floorY + 1, WorldBlockFactory.create(bedLeftX, floorY + 1, "village_bed_left"));
        world.setBlock(bedLeftX + 1, floorY + 1, WorldBlockFactory.create(bedLeftX + 1, floorY + 1, "village_bed_right"));
    }

    private static void placeFloor(World world, int baseX, int floorY, int width) {
        for (int x = baseX; x < baseX + width; x++) {
            int localX = x - baseX;
            String floorBlock = usesMossStoneFloor(localX, width) ? MOSS_STONE_FLOOR_BLOCK : COBBLESTONE_FLOOR_BLOCK;
            world.setBlock(x, floorY, WorldBlockFactory.create(x, floorY, floorBlock));
        }
    }

    private static void placeRow(World world, int startX, int y, int width, String blockId) {
        for (int x = startX; x < startX + width; x++) {
            world.setBlock(x, y, WorldBlockFactory.create(x, y, blockId));
        }
    }

    private static boolean usesMossStoneFloor(int localX, int width) {
        return localX == 1
            || localX == width / 2
            || localX == width - 2
            || (width >= HOUSE_WIDTH && (localX == 6 || localX == width - 6));
    }

    private static int leftSmallHouseBaseX(int mainHouseBaseX) {
        return mainHouseBaseX - SIDE_HOUSE_GAP - SMALL_HOUSE_WIDTH;
    }

    private static int rightSmallHouseBaseX(int mainHouseBaseX) {
        return mainHouseBaseX + HOUSE_WIDTH + SIDE_HOUSE_GAP;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
