package com.main.game.evoker;

import com.badlogic.gdx.math.Rectangle;
import com.main.game.entities.player.Player;

final class EvokerFang {

    static final float LIFETIME_SECONDS = 0.5f;
    static final float OPEN_AFTER_SECONDS = 0.1f;
    static final float BASE_HITBOX_WIDTH = 0.75f;
    static final float BASE_HITBOX_HEIGHT = 1.0f;

    private final float centerX;
    private final float y;
    private final float delay;
    private final int damage;
    private final Rectangle bounds = new Rectangle();
    private float age;
    private boolean alive = true;
    private boolean damageDealt;

    EvokerFang(float centerX, float y, float delay, int damage) {
        this.centerX = centerX;
        this.y = y;
        this.delay = Math.max(0f, delay);
        this.damage = Math.max(0, damage);
        updateBounds();
    }

    boolean update(float delta, Player player) {
        if (!alive) {
            return false;
        }
        age += Math.max(0f, delta);
        if (activeAge() >= LIFETIME_SECONDS) {
            alive = false;
            return false;
        }
        updateBounds();
        if (canDamage() && !damageDealt && player != null && player.isAlive()
            && bounds.overlaps(player.getBounds())) {
            int healthBefore = player.getHealth();
            player.takeDamage(damage);
            damageDealt = true;
            return player.getHealth() < healthBefore;
        }
        return false;
    }

    boolean overlaps(Rectangle targetBounds) {
        return canDamage() && targetBounds != null && bounds.overlaps(targetBounds);
    }

    boolean isAlive() {
        return alive;
    }

    boolean isVisible() {
        return alive && age >= delay;
    }

    boolean isOpen() {
        return activeAge() >= OPEN_AFTER_SECONDS;
    }

    float getScale() {
        if (!isVisible()) {
            return 0f;
        }
        float progress = Math.min(1f, activeAge() / LIFETIME_SECONDS);
        return 0.2f + progress * 0.8f;
    }

    float getCenterX() {
        return centerX;
    }

    float getY() {
        return y;
    }

    float getDelay() {
        return delay;
    }

    Rectangle getBounds() {
        return bounds;
    }

    private boolean canDamage() {
        return isVisible() && isOpen() && alive;
    }

    private float activeAge() {
        return Math.max(0f, age - delay);
    }

    private void updateBounds() {
        float scale = Math.max(0.2f, getScale());
        float width = BASE_HITBOX_WIDTH * scale;
        float height = BASE_HITBOX_HEIGHT * scale;
        bounds.set(centerX - width * 0.5f, y, width, height);
    }
}
