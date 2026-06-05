package com.main.game.projectile;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.main.game.world.World;

public class Projectile {

    static final float COLLISION_EPSILON = 0.001f;

    private final ProjectileType type;
    private final Vector2 position;
    private final Vector2 velocity;
    private final Rectangle bounds;
    private final int damage;
    private final float lifetime;
    private float age;
    private boolean alive = true;

    public Projectile(ProjectileType type, float x, float y, float width, float height,
                      float velocityX, float velocityY, int damage, float lifetime) {
        this.type = type == null ? ProjectileType.PILLAGER_ARROW : type;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(velocityX, velocityY);
        this.bounds = new Rectangle(x, y, width, height);
        this.damage = Math.max(0, damage);
        this.lifetime = Math.max(0.01f, lifetime);
    }

    public void update(float delta, World world) {
        if (!alive) {
            return;
        }

        age += Math.max(0f, delta);
        position.mulAdd(velocity, delta);
        bounds.setPosition(position);

        if (age >= lifetime || isOutsideWorld(world) || collidesWithSolid(world)) {
            alive = false;
        }
    }

    public boolean overlaps(Rectangle targetBounds) {
        return alive && targetBounds != null && bounds.overlaps(targetBounds);
    }

    public void kill() {
        alive = false;
    }

    public ProjectileType getType() {
        return type;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getWidth() {
        return bounds.width;
    }

    public float getHeight() {
        return bounds.height;
    }

    public float getVelocityX() {
        return velocity.x;
    }

    public float getVelocityY() {
        return velocity.y;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isAlive() {
        return alive;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    float getRotationDegrees() {
        return (float) Math.toDegrees(Math.atan2(velocity.y, velocity.x));
    }

    private boolean collidesWithSolid(World world) {
        if (world == null) {
            return false;
        }

        int minX = (int) Math.floor(bounds.x + COLLISION_EPSILON);
        int maxX = (int) Math.floor(bounds.x + bounds.width - COLLISION_EPSILON);
        int minY = (int) Math.floor(bounds.y + COLLISION_EPSILON);
        int maxY = (int) Math.floor(bounds.y + bounds.height - COLLISION_EPSILON);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (world.isSolid(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOutsideWorld(World world) {
        if (world == null) {
            return false;
        }
        float centerX = bounds.x + bounds.width * 0.5f;
        float centerY = bounds.y + bounds.height * 0.5f;
        return centerX < 0f || centerX >= world.width || centerY < 0f || centerY >= world.height;
    }
}
