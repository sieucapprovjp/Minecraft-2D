

/**
 * Quản lý toàn bộ map game: lưu trữ và truy xuất block.
 * KIÊN sẽ implement terrain generation và camera control.
 *
 * Hiện tại có sẵn:
 *  - Mảng 2D lưu block
 *  - getBlock() / setBlock() để truy xuất
 *  - isInBounds() kiểm tra tọa độ hợp lệ
 *
 * Kiên cần implement thêm:
 *  - generate() — sinh địa hình
 *  - render()   — vẽ các tile nhìn thấy trong camera
 *
 * TODO(KIEN-WORLD):
 *  - Terrain hiện tại là bản nền (noise-lite), cần nâng cấp perlin/simplex + cave.
 *  - Tích hợp chunk data để tối ưu streaming world lớn.
 *  - Thêm API lấy spawn point cho player.
 */
package com.main.game.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.main.game.blocks.AbstractBlock;
import com.main.game.blocks.SimpleBlock;
import com.main.game.worldgen.BiomeType;
import com.main.game.worldgen.WorldBlockFactory;
import com.main.game.worldgen.cave.CaveGenerator;
import com.main.game.utils.Constants;
import com.badlogic.gdx.math.GridPoint2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Quản lý toàn bộ map game bằng chunk trong phạm vi WORLD_WIDTH/WORLD_HEIGHT.
 */
public class World {

    public static final int BEDROCK_TOP_Y = 3;
    public static final int DEEPSLATE_TOP_Y = 40;
    private static final String INITIAL_SPAWN_PLATFORM_ID = "initial_spawn_platform";
    private static final int PLAYER_SPAWN_WIDTH_TILES = 2;
    private static final int PLAYER_SPAWN_HEIGHT_TILES = 3;
    private static final int INITIAL_SPAWN_PLATFORM_HALF_WIDTH = 4;
    private static final int INITIAL_SPAWN_PLATFORM_SURFACE_GAP = 3;

    private final long seed;
    private final Map<GridPoint2, Chunk> chunks;
    private final Map<Integer, BiomeType> biomes;
    private final int[] surfaceByX;
    private int initialSpawnPlatformMinX = -1;
    private int initialSpawnPlatformMaxX = -1;
    private int initialSpawnPlatformY = -1;
    private boolean generated;
    public final int width;
    public final int height;

    public World(long seed) {
        this.seed = seed;
        this.width = Constants.WORLD_WIDTH;
        this.height = Constants.WORLD_HEIGHT;
        this.chunks = new HashMap<>();
        this.biomes = new HashMap<>();
        this.surfaceByX = new int[width];
        Arrays.fill(surfaceByX, -1);
    }

    private GridPoint2 getChunkCoord(int worldX, int worldY) {
        int cx = Math.floorDiv(worldX, Constants.CHUNK_SIZE);
        int cy = Math.floorDiv(worldY, Constants.CHUNK_SIZE);
        return new GridPoint2(cx, cy);
    }

    public AbstractBlock getBlock(int x, int y) {
        if (!isInBounds(x, y)) return null;
        GridPoint2 chunkPos = getChunkCoord(x, y);
        Chunk chunk = chunks.get(chunkPos);
        if (chunk == null) return null;
        return chunk.getBlock(Math.floorMod(x, Constants.CHUNK_SIZE), Math.floorMod(y, Constants.CHUNK_SIZE));
    }

    public void setBlock(int x, int y, AbstractBlock block) {
        if (!isInBounds(x, y)) return;
        GridPoint2 chunkPos = getChunkCoord(x, y);
        Chunk chunk = chunks.computeIfAbsent(chunkPos, k -> new Chunk(chunkPos.x, chunkPos.y));
        chunk.setBlock(Math.floorMod(x, Constants.CHUNK_SIZE), Math.floorMod(y, Constants.CHUNK_SIZE), block);
    }

    public void setBiome(int x, BiomeType biome) {
        if (biome == null || x < 0 || x >= width) return;
        biomes.put(x, biome);
    }

    public BiomeType getBiome(int x) {
        if (x < 0 || x >= width) return BiomeType.FOREST;
        return biomes.getOrDefault(x, BiomeType.FOREST);
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isSolid(int x, int y) {
        AbstractBlock block = getBlock(x, y);
        return block != null && block.isSolid();
    }

    public void generate() {
        if (generated) return;

        int maxChunkX = Math.floorDiv(width - 1, Constants.CHUNK_SIZE);
        int maxChunkY = Math.floorDiv(height - 1, Constants.CHUNK_SIZE);
        for (int cx = 0; cx <= maxChunkX; cx++) {
            for (int cy = 0; cy <= maxChunkY; cy++) {
                generateChunk(cx, cy);
            }
        }

        CaveGenerator.generate(this, seed);
        generated = true;
    }

    public int getSurfaceY(int x) {
        if (x < 0 || x >= width) return -1;
        if (surfaceByX[x] >= 0) return surfaceByX[x];
        for (int y = height - 1; y >= 0; y--) {
            if (isSolid(x, y)) {
                surfaceByX[x] = y;
                return y;
            }
        }
        return -1;
    }

    /** Load chunk trong phạm vi map hữu hạn xung quanh camera. */
    public void update(OrthographicCamera camera) {
        int camChunkX = Math.floorDiv((int) camera.position.x, Constants.CHUNK_SIZE);
        int camChunkY = Math.floorDiv((int) camera.position.y, Constants.CHUNK_SIZE);
        int loadRadius = 4;
        int minChunkX = 0;
        int maxChunkX = Math.floorDiv(width - 1, Constants.CHUNK_SIZE);
        int minChunkY = 0;
        int maxChunkY = Math.floorDiv(height - 1, Constants.CHUNK_SIZE);

        for (int cx = Math.max(minChunkX, camChunkX - loadRadius); cx <= Math.min(maxChunkX, camChunkX + loadRadius); cx++) {
            for (int cy = Math.max(minChunkY, camChunkY - loadRadius); cy <= Math.min(maxChunkY, camChunkY + loadRadius); cy++) {
                GridPoint2 pos = new GridPoint2(cx, cy);
                if (!chunks.containsKey(pos)) {
                    chunks.put(pos, new Chunk(cx, cy));
                    generateChunk(cx, cy);
                }
            }
        }
    }

    /** Logic sinh terrain hữu hạn; cave/ore V1 chạy sau khi toàn map đã có terrain. */
    public void generateChunk(int chunkX, int chunkY) {
        if (chunkX < 0 || chunkY < 0) return;
        int startX = chunkX * Constants.CHUNK_SIZE;
        int endX = Math.min(width, startX + Constants.CHUNK_SIZE);
        int startY = chunkY * Constants.CHUNK_SIZE;
        int endY = Math.min(height, startY + Constants.CHUNK_SIZE);
        if (startX >= width || startY >= height) return;

        Random random = new Random(this.seed + chunkX);
        int baseGround = height / 2;
        float amplitude = 12f;
        float frequency = 0.04f;

        for (int x = startX; x < endX; x++) {
            // Set Biome mặc định cho tọa độ này
            setBiome(x, BiomeType.FOREST);

            float noiseVal = getSmoothNoise1D(x * frequency, this.seed);
            float detailNoise = getSmoothNoise1D(x * frequency * 3f, this.seed + 1) * 0.2f;

            int surface = baseGround + (int) ((noiseVal + detailNoise) * amplitude);
            surface = Math.max(8, Math.min(height - 4, surface));
            surfaceByX[x] = surface;

            for (int y = startY; y < endY; y++) {
                AbstractBlock block = null;

                if (y <= BEDROCK_TOP_Y) {
                    block = new SimpleBlock(x, y, "bedrock", true, false, 999f, BlockPalette.getBedrock());
                } else if (y < surface - 3) {
                    String blockId = y <= DEEPSLATE_TOP_Y ? "deepslate" : "stone";
                    block = WorldBlockFactory.create(x, y, blockId);
                } else if (y >= surface - 3 && y < surface) {
                    block = new SimpleBlock(x, y, "dirt", true, true, 0.7f, BlockPalette.getDirt());
                } else if (y == surface) {
                    block = new SimpleBlock(x, y, "grass", true, true, 0.6f, BlockPalette.getGrass());
                }

                if (block != null) setBlock(x, y, block);
            }

            // Trồng cây
            if (startY <= surface + 1 && endY >= surface + 1) {
                if (x % 29 == 0 && random.nextFloat() < 0.65f) {
                    int trunkBaseY = surface + 1;
                    int trunkHeight = 3 + random.nextInt(2);
                    for (int ty = 0; ty < trunkHeight; ty++) {
                        setBlock(x, trunkBaseY + ty, new SimpleBlock(x, trunkBaseY + ty, "wood", true, true, 0.9f, BlockPalette.getWood()));
                    }
                    int leafY = trunkBaseY + trunkHeight;
                    for (int lx = x - 1; lx <= x + 1; lx++) {
                        for (int ly = leafY - 1; ly <= leafY; ly++) {
                            if (getBlock(lx, ly) == null) {
                                setBlock(lx, ly, new SimpleBlock(lx, ly, "leaves", false, true, 0.2f, BlockPalette.getLeaves()));
                            }
                        }
                    }
                }
            }
        }
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        float halfW = camera.viewportWidth  * camera.zoom / 2f;
        float halfH = camera.viewportHeight * camera.zoom / 2f;
        int minX = Math.max(0, (int) Math.floor(camera.position.x - halfW) - 1);
        int maxX = Math.min(width - 1, (int) Math.ceil(camera.position.x + halfW) + 1);
        int minY = Math.max(0, (int) Math.floor(camera.position.y - halfH) - 1);
        int maxY = Math.min(height - 1, (int) Math.ceil(camera.position.y + halfH) + 1);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                AbstractBlock block = getBlock(x, y);
                if (block != null) block.render(batch);
            }
        }
    }

    public Vector2 getInitialSpawnPoint() {
        int spawnX = getCenteredSpawnX();
        int platformY = prepareInitialSpawnPlatform(spawnX);
        return new Vector2(spawnX + 0.1f, platformY + 1f);
    }

    public Vector2 getSpawnPoint() {
        int spawnX = getCenteredSpawnX();
        int surface = Math.max(DEEPSLATE_TOP_Y + 1, getSurfaceY(spawnX));
        int spawnY = Math.min(height - PLAYER_SPAWN_HEIGHT_TILES - 1, surface + 1);
        clearPlayerSpawnSpace(spawnX, spawnY);
        return new Vector2(spawnX + 0.1f, spawnY);
    }

    public boolean isSafePlayerSpawn(int x, int y) {
        if (y <= 0 || y + PLAYER_SPAWN_HEIGHT_TILES >= height) return false;
        for (int tx = x; tx < x + PLAYER_SPAWN_WIDTH_TILES; tx++) {
            if (!isInBounds(tx, y - 1) || !isSolid(tx, y - 1)) return false;
            for (int ty = y; ty < y + PLAYER_SPAWN_HEIGHT_TILES; ty++) {
                if (!isInBounds(tx, ty) || isSolid(tx, ty)) return false;
            }
        }
        return true;
    }

    public void removeInitialSpawnPlatform() {
        if (initialSpawnPlatformY < 0) return;
        for (int x = initialSpawnPlatformMinX; x <= initialSpawnPlatformMaxX; x++) {
            AbstractBlock block = getBlock(x, initialSpawnPlatformY);
            if (block != null && INITIAL_SPAWN_PLATFORM_ID.equals(block.getBlockId())) {
                setBlock(x, initialSpawnPlatformY, null);
            }
        }
        initialSpawnPlatformMinX = -1;
        initialSpawnPlatformMaxX = -1;
        initialSpawnPlatformY = -1;
    }

    private int getCenteredSpawnX() {
        return Math.max(INITIAL_SPAWN_PLATFORM_HALF_WIDTH + 1,
            Math.min(width - INITIAL_SPAWN_PLATFORM_HALF_WIDTH - PLAYER_SPAWN_WIDTH_TILES - 1, width / 2));
    }

    private int prepareInitialSpawnPlatform(int centerX) {
        int surface = Math.max(DEEPSLATE_TOP_Y + 1, getSurfaceY(centerX));
        int platformY = Math.min(
            height - PLAYER_SPAWN_HEIGHT_TILES - 2,
            Math.max(surface + INITIAL_SPAWN_PLATFORM_SURFACE_GAP, DEEPSLATE_TOP_Y + 8)
        );
        int minX = centerX - INITIAL_SPAWN_PLATFORM_HALF_WIDTH;
        int maxX = centerX + INITIAL_SPAWN_PLATFORM_HALF_WIDTH + PLAYER_SPAWN_WIDTH_TILES - 1;
        for (int x = minX; x <= maxX; x++) {
            if (x < 1 || x >= width - 1) continue;
            setBlock(x, platformY, new SimpleBlock(x, platformY, INITIAL_SPAWN_PLATFORM_ID, true, false, 999f, null));
        }
        clearPlayerSpawnSpace(centerX, platformY + 1);
        initialSpawnPlatformMinX = minX;
        initialSpawnPlatformMaxX = maxX;
        initialSpawnPlatformY = platformY;
        return platformY;
    }

    private void clearPlayerSpawnSpace(int spawnX, int spawnY) {
        int maxY = Math.min(height - 1, spawnY + PLAYER_SPAWN_HEIGHT_TILES + 1);
        for (int x = spawnX; x < spawnX + PLAYER_SPAWN_WIDTH_TILES; x++) {
            if (x < 0 || x >= width) continue;
            for (int y = spawnY; y <= maxY; y++) {
                AbstractBlock block = getBlock(x, y);
                if (block != null && !INITIAL_SPAWN_PLATFORM_ID.equals(block.getBlockId())) {
                    setBlock(x, y, null);
                }
            }
        }
    }

    private float getSmoothNoise1D(float x, long seed) {
        int intX = (int) Math.floor(x);
        float fracX = x - intX;
        float v1 = getSeededRandom(intX, seed);
        float v2 = getSeededRandom(intX + 1, seed);
        float f = (1f - (float)Math.cos(fracX * Math.PI)) * 0.5f;
        return v1 * (1f - f) + v2 * f;
    }

    private float getSeededRandom(int x, long seed) {
        long n = x * 374761393L + seed * 668265263L;
        n = (n ^ (n >> 13)) * 1274126177L;
        return (((n & 0x7FFFFFFF) / (float) 0x7FFFFFFF) * 2f) - 1f;
    }
}
