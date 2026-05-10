package com.main.game.physics;

import com.main.game.entities.Entity;
import com.main.game.utils.Constants;
import com.main.game.world.World;

/**
 * PhysicsEngine — Xử lý toàn bộ vật lý trong game.
 *
 * Thuật toán: Separated Axis AABB Collision
 *  1. Áp dụng gravity lên velocity.y
 *  2. Di chuyển theo trục X → resolve collision X (đẩy ra khỏi block ngang)
 *  3. Di chuyển theo trục Y → resolve collision Y (đẩy ra khỏi block dọc)
 *
 * Tách X/Y riêng để tránh kẹt góc block — entity luôn trượt mượt
 * dọc theo bề mặt thay vì bị "dính" vào góc.
 */
public class PhysicsEngine {

    // Epsilon nhỏ để tránh floating-point edge cases
    private static final float EPSILON = 0.001f;

    /**
     * Pipeline vật lý chính — gọi mỗi frame cho mỗi entity.
     * Thứ tự: gravity → move X → resolve X → move Y → resolve Y → sync bounds
     */
    public void update(Entity entity, World world, float delta) {
        applyGravity(entity, delta);

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
     * Kéo entity xuống theo gravity, giới hạn ở TERMINAL_VELOCITY.
     */
    public void applyGravity(Entity entity, float delta) {
        float vy = entity.getVelocity().y + Constants.GRAVITY * delta;
        entity.getVelocity().y = Math.max(vy, Constants.TERMINAL_VELOCITY);
    }

    // ─── Resolve trục X ──────────────────────────────────────────

    /**
     * Sau khi di chuyển theo X, kiểm tra overlap với block solid.
     * Nếu entity chồng lên block → đẩy ra ngoài theo hướng ngược lại.
     */
    private void resolveCollisionX(Entity entity, World world) {
        float ex = entity.getPosition().x;
        float ey = entity.getPosition().y;
        float ew = entity.getWidth();
        float eh = entity.getHeight();

        // Tìm range tile mà entity đang overlap
        int tileLeft   = (int) Math.floor(ex);
        int tileRight  = (int) Math.floor(ex + ew - EPSILON);
        int tileBottom = (int) Math.floor(ey + EPSILON);       // +epsilon: bỏ qua tile ngay dưới chân (ground)
        int tileTop    = (int) Math.floor(ey + eh - EPSILON);

        for (int tx = tileLeft; tx <= tileRight; tx++) {
            for (int ty = tileBottom; ty <= tileTop; ty++) {
                if (!world.isSolid(tx, ty)) continue;

                // Block AABB: [tx, tx+1] x [ty, ty+1]
                // Entity đang overlap block này theo X
                if (entity.getVelocity().x > 0) {
                    // Đi sang phải → đẩy entity sang trái (mép trái block)
                    entity.getPosition().x = tx - ew;
                } else if (entity.getVelocity().x < 0) {
                    // Đi sang trái → đẩy entity sang phải (mép phải block)
                    entity.getPosition().x = tx + 1f;
                }
                entity.getVelocity().x = 0f;
                return; // Chỉ cần resolve 1 block, vì đã đẩy ra ngoài
            }
        }
    }

    // ─── Resolve trục Y ──────────────────────────────────────────

    /**
     * Sau khi di chuyển theo Y, kiểm tra overlap với block solid.
     * Nếu entity chồng lên block:
     *  - Rơi xuống (vy < 0) → đặt lên trên block, set onGround = true
     *  - Bay lên (vy > 0)   → đẩy xuống dưới block, reset vy = 0
     */
    private void resolveCollisionY(Entity entity, World world) {
        float ex = entity.getPosition().x;
        float ey = entity.getPosition().y;
        float ew = entity.getWidth();
        float eh = entity.getHeight();

        int tileLeft   = (int) Math.floor(ex + EPSILON);       // +epsilon: cho phép sát mép
        int tileRight  = (int) Math.floor(ex + ew - EPSILON);
        int tileBottom = (int) Math.floor(ey);
        int tileTop    = (int) Math.floor(ey + eh - EPSILON);

        entity.setOnGround(false);

        for (int tx = tileLeft; tx <= tileRight; tx++) {
            for (int ty = tileBottom; ty <= tileTop; ty++) {
                if (!world.isSolid(tx, ty)) continue;

                if (entity.getVelocity().y <= 0) {
                    // Rơi xuống → đặt entity lên trên block
                    entity.getPosition().y = ty + 1f;
                    entity.getVelocity().y = 0f;
                    entity.setOnGround(true);
                } else {
                    // Bay lên → đập đầu vào trần → đẩy xuống dưới block
                    entity.getPosition().y = ty - eh;
                    entity.getVelocity().y = 0f;
                }
                return;
            }
        }
    }

    // ─── Legacy methods (giữ cho backward compat) ────────────────

    /**
     * @deprecated Dùng {@link #update(Entity, World, float)} thay thế.
     */
    @Deprecated
    public void resolveCollision(Entity entity, float delta) {
        if (entity.getPosition().y <= 0) {
            entity.getPosition().y = 0;
            entity.getVelocity().y = 0;
            entity.setOnGround(true);
        } else {
            entity.setOnGround(false);
        }
    }
}
