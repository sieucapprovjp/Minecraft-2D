package com.main.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Abstract base cho mọi entity trong game: Player, Mob, Animal...
 *
 * Đã có sẵn:
 *  - Position, velocity (đơn vị tile)
 *  - Bounding box dùng cho collision (Lâm Hùng dùng cái này)
 *  - Trạng thái onGround, isAlive
 *  - Vòng đời update() / render() / dispose()
 */
public abstract class Entity {

    // ─── Transform ───────────────────────────────────────────────
    protected Vector2   position;   // góc dưới-trái, đơn vị tile
    protected Vector2   velocity;   // tile/s
    protected float     width;
    protected float     height;

    // ─── Collision box (Lâm Hùng dùng) ───────────────────────────
    protected Rectangle bounds;

    // ─── State ───────────────────────────────────────────────────
    protected boolean onGround    = false;
    protected boolean isAlive     = true;
    protected boolean facingRight = true;

    public Entity(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.width    = width;
        this.height   = height;
        this.bounds   = new Rectangle(x, y, width, height);
    }

    // ─── Vòng đời ────────────────────────────────────────────────

    /** Logic update mỗi frame */
    public abstract void update(float delta);

    /** Vẽ entity */
    public abstract void render(SpriteBatch batch);

    /** Giải phóng tài nguyên */
    public void dispose() {}

    // ─── Helpers ─────────────────────────────────────────────────

    /** Sync bounds theo position — gọi sau mỗi lần di chuyển */
    protected void updateBounds() {
        bounds.setPosition(position.x, position.y);
    }

    /** Kiểm tra va chạm với entity khác */
    public boolean overlaps(Entity other) {
        return this.bounds.overlaps(other.bounds);
    }

    // ─── Getters / Setters ────────────────────────────────────────
    public Vector2   getPosition()   { return position;    }
    public Vector2   getVelocity()   { return velocity;    }
    public Rectangle getBounds()     { return bounds;      }
    public boolean   isOnGround()    { return onGround;    }
    public boolean   isAlive()       { return isAlive;     }
    public boolean   isFacingRight() { return facingRight; }
    public float     getX()          { return position.x;  }
    public float     getY()          { return position.y;  }

    public void setOnGround(boolean v) { this.onGround = v; }
    public void setAlive(boolean v)    { this.isAlive  = v; }
}
