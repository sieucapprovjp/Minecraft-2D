package com.main.game.physics;

import com.badlogic.gdx.math.Rectangle;
import com.main.game.blocks.AbstractBlock;
import com.main.game.entities.Entity;
import com.main.game.utils.Constants;
import com.main.game.world.World;

/**
 * Xu ly toan bo vat ly trong game.
 */
public class PhysicsEngine {

    /**
     * Ap dung trong luc, di chuyen va xu ly va cham voi world.
     */
    public void update(Entity entity, World world, float delta) {
        entity.setOnGround(false);
        applyGravity(entity, delta);

        // 1. Cập nhật vị trí X và kiểm tra va chạm trục X
        entity.getPosition().x += entity.getVelocity().x * delta;
        entity.getBounds().x = entity.getPosition().x;
        checkCollisionX(entity, world);

        // 2. Cập nhật vị trí Y và kiểm tra va chạm trục Y
        entity.getPosition().y += entity.getVelocity().y * delta;
        entity.getBounds().y = entity.getPosition().y;
        checkCollisionY(entity, world);
    }

    /**
     * Overload tam thoi de khong pha vo cac cho goi cu chua truyen world.
     */
    public void update(Entity entity, float delta) {
        entity.setOnGround(false);
        applyGravity(entity, delta);
        entity.getPosition().x += entity.getVelocity().x * delta;
        entity.getPosition().y += entity.getVelocity().y * delta;
        entity.getBounds().setPosition(entity.getPosition().x, entity.getPosition().y);
    }

    /** Keo entity xuong theo gravity, gioi han o TERMINAL_VELOCITY. */
    public void applyGravity(Entity entity, float delta) {
        float vy = entity.getVelocity().y + Constants.GRAVITY * delta;
        entity.getVelocity().y = Math.max(vy, Constants.TERMINAL_VELOCITY);
    }

    /**
     * Legacy API cho code chua truyen world:
     * di chuyen theo velocity va clamp san y = 0.
     */
    public void resolveCollision(Entity entity, float delta) {
        entity.setOnGround(false);
        entity.getPosition().x += entity.getVelocity().x * delta;
        entity.getPosition().y += entity.getVelocity().y * delta;
        if (entity.getPosition().y < 0f) {
            entity.getPosition().y = 0f;
            entity.getVelocity().y = 0f;
            entity.setOnGround(true);
        }
        entity.getBounds().setPosition(entity.getPosition().x, entity.getPosition().y);
    }

    /** Kiểm tra và xử lý va chạm trên trục X (Tránh kẹt) */
    private void checkCollisionX(Entity entity, World world) {
        Rectangle bounds = entity.getBounds();

        // Phạm vi block cần kiểm tra xung quanh entity
        int minX = Math.max(0, (int) Math.floor(bounds.x) - 1);
        int maxX = Math.min(world.width - 1, (int) Math.floor(bounds.x + bounds.width) + 1);
        int minY = Math.max(0, (int) Math.floor(bounds.y) - 1);
        int maxY = Math.min(world.height - 1, (int) Math.floor(bounds.y + bounds.height) + 1);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                AbstractBlock block = world.getBlock(x, y);
                if (block != null && block.isSolid()) {
                    if (bounds.overlaps(block.getBounds())) {
                        // Resolve va chạm trên trục X
                        if (entity.getVelocity().x > 0) {
                            // Đi sang phải, đụng tường bên phải
                            entity.getPosition().x = block.getBounds().x - bounds.width;
                        } else if (entity.getVelocity().x < 0) {
                            // Đi sang trái, đụng tường bên trái
                            entity.getPosition().x = block.getBounds().x + block.getBounds().width;
                        }
                        entity.getVelocity().x = 0;
                        bounds.x = entity.getPosition().x; // Cập nhật lại bounds cho trục Y test tiếp
                    }
                }
            }
        }
    }

    /** Kiểm tra và xử lý va chạm trên trục Y (Ground detection) */
    private void checkCollisionY(Entity entity, World world) {
        Rectangle bounds = entity.getBounds();

        // Phạm vi block cần kiểm tra xung quanh entity
        int minX = Math.max(0, (int) Math.floor(bounds.x) - 1);
        int maxX = Math.min(world.width - 1, (int) Math.floor(bounds.x + bounds.width) + 1);
        int minY = Math.max(0, (int) Math.floor(bounds.y) - 1);
        int maxY = Math.min(world.height - 1, (int) Math.floor(bounds.y + bounds.height) + 1);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                AbstractBlock block = world.getBlock(x, y);
                if (block != null && block.isSolid()) {
                    if (bounds.overlaps(block.getBounds())) {
                        // Resolve va chạm trên trục Y
                        if (entity.getVelocity().y > 0) {
                            // Nhảy lên, đụng trần
                            entity.getPosition().y = block.getBounds().y - bounds.height;
                        } else if (entity.getVelocity().y < 0) {
                            // Rơi xuống, chạm đất (Ground detection chính xác)
                            entity.getPosition().y = block.getBounds().y + block.getBounds().height;
                            entity.setOnGround(true);
                        }
                        entity.getVelocity().y = 0;
                        bounds.y = entity.getPosition().y; // Cập nhật lại bounds
                    }
                }
            }
        }
    }
}
