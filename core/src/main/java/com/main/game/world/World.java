package com.main.game.world;

import com.main.game.blocks.AbstractBlock;
import com.main.game.utils.Constants;

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
 */
public class World {

    private final AbstractBlock[][] blocks;
    public  final int width;
    public  final int height;

    public World() {
        this.width  = Constants.WORLD_WIDTH;
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

    /** Block có solid không — Lâm Hùng dùng cho collision */
    public boolean isSolid(int x, int y) {
        AbstractBlock block = getBlock(x, y);
        return block != null && block.isSolid();
    }

    // ─── TODO: Kiên implement tiếp ────────────────────────────────

    // public void generate(long seed) { ... }
    // public void render(SpriteBatch batch, OrthographicCamera camera) { ... }
}
