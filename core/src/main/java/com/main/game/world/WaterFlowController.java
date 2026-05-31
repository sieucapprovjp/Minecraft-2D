package com.main.game.world;

import com.main.game.blocks.AbstractBlock;
import com.main.game.blocks.types.UtilityBlocks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WaterFlowController — Xử lý nước chảy xuống khi không có block bên dưới.
 *
 * Cơ chế:
 *  - Tracks tất cả water block trong world.
 *  - Mỗi frame, duyệt từ dưới lên trên để nước chảy đúng tầng.
 *  - Nếu block dưới water là không khí (và không phải water),
 *    tích lũy timer. Khi timer >= 1 giây, nước chảy xuống 1 block.
 *  - Nếu block dưới là solid hoặc water, reset timer (nước đứng yên).
 */
public class WaterFlowController {

    private static final float FLOW_INTERVAL = 1.0f; // 1 giây / 1 block

    // Packed position (long) -> accumulated timer
    private final Map<Long, Float> waterTimers = new HashMap<>();

    /**
     * Quét toàn bộ world để tìm water block và đưa vào danh sách theo dõi.
     * Gọi một lần sau khi world được generate.
     */
    public void buildWaterList(World world) {
        waterTimers.clear();
        for (int x = 0; x < world.width; x++) {
            for (int y = 0; y < world.height; y++) {
                AbstractBlock block = world.getBlock(x, y);
                if (block != null && block.isWater()) {
                    waterTimers.put(pack(x, y), 0f);
                }
            }
        }
    }

    /**
     * Thông báo khi block thay đổi (đặt/phá block) để cập nhật danh sách theo dõi.
     */
    public void onBlockChanged(int x, int y, AbstractBlock oldBlock, AbstractBlock newBlock) {
        long key = pack(x, y);
        if (oldBlock != null && oldBlock.isWater()) {
            waterTimers.remove(key);
        }
        if (newBlock != null && newBlock.isWater()) {
            waterTimers.put(key, 0f);
        }
    }

    /**
     * Pipeline chính: duyệt các water block từ dưới lên, tích lũy timer,
     * và flow nước xuống khi đủ thời gian.
     */
    public void update(float delta, World world) {
        if (waterTimers.isEmpty()) return;

        // Sắp xếp theo Y tăng dần (dưới lên) để nước tầng dưới chảy trước,
        // tạo hiệu ứng cascade đúng: nước tầng trên chảy xuống sau khi
        // tầng dưới đã rời đi.
        List<Long> positions = new ArrayList<>(waterTimers.keySet());
        positions.sort(Comparator.comparingInt(this::unpackY));

        for (long key : positions) {
            // Nếu block này đã bị xóa bởi một lần flow trước đó, bỏ qua
            if (!waterTimers.containsKey(key)) continue;

            int x = unpackX(key);
            int y = unpackY(key);

            // Kiểm tra block hiện tại còn là nước không
            AbstractBlock block = world.getBlock(x, y);
            if (block == null || !block.isWater()) {
                waterTimers.remove(key);
                continue;
            }

            // Kiểm tra block bên dưới: nếu không solid và không phải nước → flow được
            if (y > 0 && canFlowInto(world, x, y - 1)) {
                float timer = waterTimers.getOrDefault(key, 0f);
                timer += delta;
                waterTimers.put(key, timer);

                if (timer >= FLOW_INTERVAL) {
                    // Di chuyển nước xuống dưới (Filling logic: giữ nguyên block cũ, tạo block mới bên dưới)
                    world.setBlock(x, y - 1, new UtilityBlocks.WaterBlock(x, y - 1));

                    // Reset timer cho block hiện tại vì block bên dưới giờ đã là nước (không flow tiếp được)
                    waterTimers.put(key, 0f);

                    // Thêm block nước mới vào danh sách theo dõi để nó có thể tiếp tục chảy xuống
                    waterTimers.put(pack(x, y - 1), 0f);
                }
            } else {
                // Block bên dưới là solid hoặc nước → reset timer (nước đứng yên)
                waterTimers.put(key, 0f);
            }
        }
    }

    /**
     * Kiểm tra nước có thể chảy vào tile này không.
     * Tile phải ở trong bounds, không solid và không phải nước.
     */
    private boolean canFlowInto(World world, int x, int y) {
        if (!world.isInBounds(x, y)) return false;
        AbstractBlock block = world.getBlock(x, y);
        return block == null || (!block.isSolid() && !block.isWater());
    }

    private long pack(int x, int y) {
        return ((long) x << 32) | (y & 0xFFFFFFFFL);
    }

    private int unpackX(long key) {
        return (int) (key >> 32);
    }

    private int unpackY(long key) {
        return (int) key;
    }
}
