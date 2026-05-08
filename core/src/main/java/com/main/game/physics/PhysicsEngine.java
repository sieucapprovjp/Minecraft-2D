package com.main.game.physics;

import com.main.game.entities.Entity;
import com.main.game.utils.Constants;
import com.main.game.world.World;

/**
 * Xử lý toàn bộ vật lý trong game.
 * LÂM HÙNG sẽ implement chi tiết class này.
 *
 * Hiện tại có sẵn:
 *  - applyGravity()  → áp dụng trọng lực lên entity
 *  - applyVelocity() → di chuyển entity theo velocity
 *
 * Lâm Hùng cần implement thêm:
 *  - checkBlockCollision() → kiểm tra va chạm với block
 *  - resolveCollision()    → xử lý sau khi phát hiện va chạm
 *
 * TODO(LHUNG-PHYSICS):
 *  - Tách resolve theo 2 trục X/Y để tránh kẹt góc.
 *  - Chốt quy tắc onGround và reset velocity khi va chạm.
 *  - Bổ sung collision với world qua API World.isSolid(x, y).
 */
public class PhysicsEngine {

    /**
     * Áp dụng trọng lực và di chuyển entity.
     * Gọi mỗi frame từ GameScreen.update()
     */
    public void update(Entity entity, World world, float delta) {
        applyGravity(entity, delta);
        applyVelocity(entity, delta);
        resolveWorldCollision(entity, world);
    }

    /**
     * Kéo entity xuống theo gravity, giới hạn ở TERMINAL_VELOCITY.
     * Được gọi bởi Player.update() và Mob.update() (DUOC-ENTITY).
     */
    public void applyGravity(Entity entity, float delta) {
        if (!entity.isOnGround()) {
            float vy = entity.getVelocity().y + Constants.GRAVITY * delta;
            entity.getVelocity().y = Math.max(vy, Constants.TERMINAL_VELOCITY);
        }
    }

    /**
     * Xử lý va chạm với block và cập nhật onGround.
     * TODO(LHUNG-PHYSICS): implement AABB collision đầy đủ tại đây.
     */
    public void resolveCollision(Entity entity, float delta) {
        // TODO(LHUNG-PHYSICS): implement chi tiết
        // Tạm thời chặn entity không rơi xuống dưới y = 0
        if (entity.getPosition().y <= 0) {
            entity.getPosition().y = 0;
            entity.getVelocity().y = 0;
            entity.setOnGround(true);
        } else {
            entity.setOnGround(false);
        }
        applyVelocity(entity, delta);
        // TODO: entity.getBounds().setPosition(entity.getPosition()); đã có trong applyVelocity
    }

    /** Di chuyển entity theo velocity hiện tại */
    private void applyVelocity(Entity entity, float delta) {
        entity.getPosition().x += entity.getVelocity().x * delta;
        entity.getPosition().y += entity.getVelocity().y * delta;
        // Sau khi di chuyển phải sync lại bounds để collision đúng
        entity.getBounds().setPosition(entity.getPosition());
    }

    private void resolveWorldCollision(Entity entity, World world) {
        float epsilon = 0.001f;

        int minX = (int) Math.floor(entity.getX());
        int maxX = (int) Math.floor(entity.getX() + entity.getWidth() - epsilon);
        int minY = (int) Math.floor(entity.getY());
        int maxY = (int) Math.floor(entity.getY() + entity.getHeight() - epsilon);

        entity.setOnGround(false);

        for (int x = minX; x <= maxX; x++) {
            if (!world.isSolid(x, minY)) {
                continue;
            }

            float correctedY = minY + 1f;
            entity.getPosition().y = correctedY;
            entity.getVelocity().y = 0f;
            entity.setOnGround(true);
            entity.getBounds().setPosition(entity.getPosition());
            return;
        }

        if (entity.getY() < 0f) {
            entity.getPosition().y = 0f;
            entity.getVelocity().y = 0f;
            entity.setOnGround(true);
            entity.getBounds().setPosition(entity.getPosition());
        }
    }

    // ─── TODO: Lâm Hùng implement tiếp ───────────────────────────

    // public void checkBlockCollision(Entity entity, World world) { ... }
}
