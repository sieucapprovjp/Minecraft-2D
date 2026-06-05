package com.main.game.projectile;

import com.badlogic.gdx.math.Vector2;
import com.main.game.entities.mob.Mob;
import com.main.game.entities.player.Player;

final class ProjectileAim {

    private static final float MUZZLE_FORWARD_OFFSET = 0.55f;
    private static final float MUZZLE_HEIGHT_FACTOR = 0.78f;
    private static final float TARGET_HEIGHT_FACTOR = 0.62f;

    private ProjectileAim() {}

    static Vector2 directionToPlayer(Mob shooter, Player target, float spreadDegrees) {
        if (shooter == null || target == null) {
            return new Vector2(1f, 0f);
        }
        return directionBetween(
            shooter.getX() + shooter.getWidth() * 0.5f,
            shooter.getY() + shooter.getHeight() * MUZZLE_HEIGHT_FACTOR,
            target.getX() + target.getWidth() * 0.5f,
            target.getY() + target.getHeight() * TARGET_HEIGHT_FACTOR,
            spreadDegrees);
    }

    static Vector2 directionBetween(float shooterOriginX, float shooterOriginY,
                                    float targetX, float targetY,
                                    float spreadDegrees) {
        float dx = targetX - shooterOriginX;
        float dy = targetY - shooterOriginY;
        if (Math.abs(dx) < 0.001f && Math.abs(dy) < 0.001f) {
            dx = 1f;
        }
        return new Vector2(dx, dy).nor().rotateDeg(spreadDegrees);
    }

    static Vector2 spawnCenter(Mob shooter, Vector2 direction) {
        if (shooter == null) {
            return new Vector2();
        }
        return spawnCenter(shooter.getX() + shooter.getWidth() * 0.5f, shooter.getY(), shooter.getHeight(), direction);
    }

    static Vector2 spawnCenter(float shooterCenterX, float shooterY, float shooterHeight, Vector2 direction) {
        Vector2 dir = direction == null ? new Vector2(1f, 0f) : new Vector2(direction).nor();
        return new Vector2(
            shooterCenterX + MUZZLE_FORWARD_OFFSET * dir.x,
            shooterY + shooterHeight * MUZZLE_HEIGHT_FACTOR + MUZZLE_FORWARD_OFFSET * dir.y);
    }
}
