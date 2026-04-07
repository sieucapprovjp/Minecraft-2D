package com.main.game.physics;

import com.main.game.entities.Entity;
import com.main.game.utils.Constants;

/**
 * Xử lý toàn bộ vật lý trong game.
 * LÂM HÙNG sẽ implement chi tiết class này.
 *
 * Hiện tại có sẵn:
 *  - applyGravity()  — áp dụng trọng lực lên entity
 *  - applyVelocity() — di chuyển entity theo velocity
 *
 * Lâm Hùng cần implement thêm:
 *  - checkBlockCollision() — kiểm tra va chạm với block
 *  - resolveCollision()    — xử lý sau khi phát hiện va chạm
 */
public class PhysicsEngine {

    /**
     * Áp dụng trọng lực và di chuyển entity.
     * Gọi mỗi frame từ GameScreen.update()
     */
    public void update(Entity entity, float delta) {
        applyGravity(entity, delta);
        applyVelocity(entity, delta);
    }

    /** Kéo entity xuống theo gravity, giới hạn ở TERMINAL_VELOCITY */
    private void applyGravity(Entity entity, float delta) {
        if (!entity.isOnGround()) {
            float vy = entity.getVelocity().y + Constants.GRAVITY * delta;
            entity.getVelocity().y = Math.max(vy, Constants.TERMINAL_VELOCITY);
        }
    }

    /** Di chuyển entity theo velocity hiện tại */
    private void applyVelocity(Entity entity, float delta) {
        entity.getPosition().x += entity.getVelocity().x * delta;
        entity.getPosition().y += entity.getVelocity().y * delta;
        // Sau khi di chuyển phải sync lại bounds để collision đúng
        entity.getBounds().setPosition(entity.getPosition());
    }

    // ─── TODO: Lâm Hùng implement tiếp ───────────────────────────

    // public void checkBlockCollision(Entity entity, World world) { ... }
    // public void resolveCollision(Entity entity, AbstractBlock block) { ... }
}
