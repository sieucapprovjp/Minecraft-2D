package com.main.game.physics;

import com.main.game.blocks.AbstractBlock;
import com.main.game.entities.Entity;
import com.main.game.utils.Constants;
import com.main.game.world.World;

/**
 * PhysicsEngine — Xử lý toàn bộ vật lý trong game.
 *
 * Thuật toán: Separated Axis AABB Collision
 *  1. Tính submergedRatio (tỉ lệ ngập nước)
 *  2. Áp dụng gravity (có buoyancy nếu ở trong nước)
 *  3. Áp dụng water drag nếu đang ngập
 *  4. Di chuyển theo trục X → resolve collision X (đẩy ra khỏi block ngang)
 *  5. Di chuyển theo trục Y → resolve collision Y (đẩy ra khỏi block dọc)
 *
 * Tách X/Y riêng để tránh kẹt góc block — entity luôn trượt mượt
 * dọc theo bề mặt thay vì bị "dính" vào góc.
 */
public class PhysicsEngine {

    // Epsilon nhỏ để tránh floating-point edge cases
    private static final float EPSILON = 0.001f;

    /**
     * Pipeline vật lý chính — gọi mỗi frame cho mỗi entity.
     */
    public void update(Entity entity, World world, float delta) {
        // Tính tỉ lệ ngập nước dựa trên AABB của entity
        float ratio = getSubmergedRatio(entity, world);
        entity.setSubmergedRatio(ratio);

        // Gravity (có buoyancy nếu ở trong nước)
        applyGravity(entity, delta, ratio);

        // Lực cản nước
        applyWaterDrag(entity, delta, ratio);

        // ── Resolve trục X trước ────────────────────────────────
        float dx = entity.getVelocity().x * delta;
        entity.getPosition().x += dx;
        resolveCollisionX(entity, world);

        // ── Resolve trục Y sau ──────────────────────────────────
        float dy = entity.getVelocity().y * delta;
        entity.getPosition().y += dy;
        resolveCollisionY(entity, world);

        // Fallback: không cho rơi khỏi world
        if (entity.getPosition().y < 0f) {
            entity.getPosition().y = 0f;
            entity.getVelocity().y = 0f;
            entity.setOnGround(true);
        }

        // Không cho đi ra ngoài biên trái/phải world
        if (entity.getPosition().x < 0f) {
            entity.getPosition().x = 0f;
        }
        if (entity.getPosition().x + entity.getWidth() > world.width) {
            entity.getPosition().x = world.width - entity.getWidth();
        }

        // Sync bounds cho collision detection khác (entity vs entity)
        entity.getBounds().setPosition(entity.getPosition());
    }

    /**
     * Tính tỉ lệ thể tích hitbox của entity đang nằm trong nước.
     * Duyệt các tile overlap với AABB, cộng dồn diện tích overlap
     * với các water block, chia cho tổng diện tích hitbox.
     */
    public float getSubmergedRatio(Entity entity, World world) {
        float ex = entity.getPosition().x;
        float ey = entity.getPosition().y;
        float ew = entity.getWidth();
        float eh = entity.getHeight();

        int tileLeft   = (int) Math.floor(ex);
        int tileRight  = (int) Math.floor(ex + ew - EPSILON);
        int tileBottom = (int) Math.floor(ey);
        int tileTop    = (int) Math.floor(ey + eh - EPSILON);

        float overlapSum = 0f;

        for (int tx = tileLeft; tx <= tileRight; tx++) {
            for (int ty = tileBottom; ty <= tileTop; ty++) {
                AbstractBlock block = world.getBlock(tx, ty);
                if (block == null || !block.isWater()) continue;

                // Tính overlap rect giữa entity AABB và tile [tx, tx+1] x [ty, ty+1]
                float ox = Math.max(ex, tx);
                float oy = Math.max(ey, ty);
                float ox2 = Math.min(ex + ew, tx + 1f);
                float oy2 = Math.min(ey + eh, ty + 1f);
                float ow = ox2 - ox;
                float oh = oy2 - oy;
                if (ow > 0f && oh > 0f) {
                    overlapSum += ow * oh;
                }
            }
        }

        float totalArea = ew * eh;
        if (totalArea <= 0f) return 0f;
        return Math.max(0f, Math.min(1f, overlapSum / totalArea));
    }

    /**
     * Kéo entity xuống theo gravity, giảm tác dụng nếu đang ở trong nước (buoyancy).
     * submergedRatio = 0 → gravity đầy đủ.
     * submergedRatio = 1 → gravity giảm WATER_BUOYANCY_STRENGTH%.
     */
    public void applyGravity(Entity entity, float delta, float submergedRatio) {
        float effectiveGravity = Constants.GRAVITY * (1f - Constants.WATER_BUOYANCY_STRENGTH * submergedRatio);
        float vy = entity.getVelocity().y + effectiveGravity * delta;
        entity.getVelocity().y = Math.max(vy, Constants.TERMINAL_VELOCITY);
    }

    /**
     * Lực cản nước: nhân vận tốc với damping factor mỗi giây.
     * Chỉ áp dụng khi entity đang ngập nước (submergedRatio > 0).
     */
    public void applyWaterDrag(Entity entity, float delta, float submergedRatio) {
        if (submergedRatio <= 0f) return;

        float dragX = (float) Math.pow(Constants.WATER_HORIZONTAL_DRAG, delta);
        float dragY = (float) Math.pow(Constants.WATER_VERTICAL_DRAG, delta);

        entity.getVelocity().x *= dragX;
        entity.getVelocity().y *= dragY;
    }

    // ─── Resolve trục X ──────────────────────────────────────────

    private void resolveCollisionX(Entity entity, World world) {
        float ex = entity.getPosition().x;
        float ey = entity.getPosition().y;
        float ew = entity.getWidth();
        float eh = entity.getHeight();

        int tileLeft   = (int) Math.floor(ex);
        int tileRight  = (int) Math.floor(ex + ew - EPSILON);
        int tileBottom = (int) Math.floor(ey + EPSILON);
        int tileTop    = (int) Math.floor(ey + eh - EPSILON);

        for (int tx = tileLeft; tx <= tileRight; tx++) {
            for (int ty = tileBottom; ty <= tileTop; ty++) {
                if (!world.isSolid(tx, ty)) continue;

                if (entity.getVelocity().x > 0) {
                    entity.getPosition().x = tx - ew;
                } else if (entity.getVelocity().x < 0) {
                    entity.getPosition().x = tx + 1f;
                }
                entity.getVelocity().x = 0f;
                return;
            }
        }
    }

    // ─── Resolve trục Y ──────────────────────────────────────────

    private void resolveCollisionY(Entity entity, World world) {
        float ex = entity.getPosition().x;
        float ey = entity.getPosition().y;
        float ew = entity.getWidth();
        float eh = entity.getHeight();

        int tileLeft   = (int) Math.floor(ex + EPSILON);
        int tileRight  = (int) Math.floor(ex + ew - EPSILON);
        int tileBottom = (int) Math.floor(ey);
        int tileTop    = (int) Math.floor(ey + eh - EPSILON);

        entity.setOnGround(false);

        for (int tx = tileLeft; tx <= tileRight; tx++) {
            for (int ty = tileBottom; ty <= tileTop; ty++) {
                if (!world.isSolid(tx, ty)) continue;

                if (entity.getVelocity().y <= 0) {
                    entity.getPosition().y = ty + 1f;
                    entity.getVelocity().y = 0f;
                    entity.setOnGround(true);
                } else {
                    entity.getPosition().y = ty - eh;
                    entity.getVelocity().y = 0f;
                }
                return;
            }
        }
    }
}
