package com.main.game.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.main.game.blocks.AbstractBlock;
import com.main.game.blocks.SimpleBlock;
import com.main.game.utils.Constants;

import java.util.Random;

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
public class World {

    private final AbstractBlock[][] blocks;
    public final int width;
    public final int height;

    public World() {
        this.width = Constants.WORLD_WIDTH;
        this.height = Constants.WORLD_HEIGHT;
        this.blocks = new AbstractBlock[width][height];
    }

    /** Lấy block tại tọa độ tile (x, y) */
    public AbstractBlock getBlock(int x, int y) {
        if (!isInBounds(x, y)) return null;
        return blocks[x][y];
    }

    /** Đặt block tại tọa độ tile (x, y) */
    public void setBlock(int x, int y, AbstractBlock block) {
        if (!isInBounds(x, y)) return;
        blocks[x][y] = block;
    }

    /** Kiểm tra tọa độ có nằm trong world không */
    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /** Block có solid không — Dùng cho vật lý/collision */
    public boolean isSolid(int x, int y) {
        AbstractBlock block = getBlock(x, y);
        return block != null && block.isSolid();
    }

    /**
     * Sinh địa hình ngẫu nhiên bằng Fractal/Value Noise 1D
     */
    public void generate(long seed) {
        Random random = new Random(seed);
        int baseGround = height / 2;

        // Thông số cấu hình đồi núi
        float amplitude = 12f;
        float frequency = 0.04f;

        for (int x = 0; x < width; x++) {
            // Tính toán bề mặt bằng Noise Ổn định
            float noiseVal = getSmoothNoise1D(x * frequency, seed);
            float detailNoise = getSmoothNoise1D(x * frequency * 3f, seed + 1) * 0.2f;

            int surface = baseGround + (int) ((noiseVal + detailNoise) * amplitude);
            surface = Math.max(8, Math.min(height - 4, surface)); // Giới hạn an toàn

            // Đắp block đất đá
            for (int y = 0; y <= surface; y++) {
                AbstractBlock block;
                if (y == 0) {
                    block = new SimpleBlock(x, y, "bedrock", true, false, 999f, BlockPalette.getBedrock());
                } else if (y == surface) {
                    // Dùng Noise để tạo bãi cát ngẫu nhiên tự nhiên
                    boolean isSandPatch = getSmoothNoise1D(x * 0.1f, seed + 99) > 0.5f;
                    if (isSandPatch) {
                        block = new SimpleBlock(x, y, "sand", true, true, 0.5f, BlockPalette.getSand());
                    } else {
                        block = new SimpleBlock(x, y, "grass", true, true, 0.6f, BlockPalette.getGrass());
                    }
                } else if (y >= surface - 3) {
                    block = new SimpleBlock(x, y, "dirt", true, true, 0.7f, BlockPalette.getDirt());
                } else {
                    block = new SimpleBlock(x, y, "stone", true, true, 1.2f, BlockPalette.getStone());
                }
                setBlock(x, y, block);
            }

            // Trồng cây ngẫu nhiên
            if (x > 2 && x < width - 2 && x % 29 == 0 && random.nextFloat() < 0.65f) {
                int trunkBaseY = surface + 1;
                int trunkHeight = 3 + random.nextInt(2);

                for (int ty = 0; ty < trunkHeight && trunkBaseY + ty < height; ty++) {
                    setBlock(x, trunkBaseY + ty,
                        new SimpleBlock(x, trunkBaseY + ty, "wood", true, true, 0.9f, BlockPalette.getWood()));
                }

                int leafY = trunkBaseY + trunkHeight;
                for (int lx = x - 1; lx <= x + 1; lx++) {
                    for (int ly = leafY - 1; ly <= leafY; ly++) {
                        if (isInBounds(lx, ly) && getBlock(lx, ly) == null) {
                            setBlock(lx, ly,
                                new SimpleBlock(lx, ly, "leaves", false, true, 0.2f, BlockPalette.getLeaves()));
                        }
                    }
                }
            }
        }
    }

    /**
     * Chỉ vẽ các block nằm trong tầm nhìn của Camera (Culling)
     */
    public void render(SpriteBatch batch, OrthographicCamera camera) {
        float halfW = camera.viewportWidth  * camera.zoom / 2f;
        float halfH = camera.viewportHeight * camera.zoom / 2f;
        int minX = Math.max(0, (int) Math.floor(camera.position.x - halfW) - 1);
        int maxX = Math.min(width - 1, (int) Math.ceil(camera.position.x + halfW) + 1);
        int minY = Math.max(0, (int) Math.floor(camera.position.y - halfH) - 1);
        int maxY = Math.min(height - 1, (int) Math.ceil(camera.position.y + halfH) + 1);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                AbstractBlock block = blocks[x][y];
                if (block != null) {
                    block.render(batch);
                }
            }
        }
    }

    /**
     * TÌM VỊ TRÍ SPAWN CHO NHÂN VẬT
     * Thả người chơi xuống mặt đất ở ngay giữa bản đồ.
     */
    public Vector2 getSpawnPoint() {
        int spawnX = width / 2;

        // Quét từ trên trời xuống dưới đất tại cột giữa map để tìm block cứng đầu tiên
        for (int y = height - 1; y >= 0; y--) {
            if (isSolid(spawnX, y)) {
                // Trả về tọa độ ngay TRÊN block đó để nhân vật không bị kẹt vào đất
                return new Vector2(spawnX, y + 1);
            }
        }

        // Tọa độ dự phòng nếu lỗi map
        return new Vector2(spawnX, height / 2f);
    }

    // CÁC HÀM HỖ TRỢ SINH NOISE ĐỊA HÌNH

    private float getSmoothNoise1D(float x, long seed) {
        int intX = (int) Math.floor(x);
        float fracX = x - intX;

        float v1 = getSeededRandom(intX, seed);
        float v2 = getSeededRandom(intX + 1, seed);

        // Cosine Interpolation
        float f = (1f - (float)Math.cos(fracX * Math.PI)) * 0.5f;
        return v1 * (1f - f) + v2 * f;
    }

    private float getSeededRandom(int x, long seed) {
        long n = x * 374761393L + seed * 668265263L;
        n = (n ^ (n >> 13)) * 1274126177L;
        return (((n & 0x7FFFFFFF) / (float) 0x7FFFFFFF) * 2f) - 1f;
    }
}
