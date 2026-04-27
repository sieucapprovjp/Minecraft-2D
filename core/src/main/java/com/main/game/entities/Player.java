package com.main.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.game.physics.PhysicsEngine;

/**
 * Class Player - điều khiển bởi người chơi.
 *
 * State machine: IDLE -> RUN -> JUMP -> FALL -> HURT -> DEAD
 * Input: A/D hoặc ←/→ để di chuyển, SPACE/W/↑ để nhảy.
 *
 * Kết nối PhysicsEngine (Lâm Hùng):
 *  - PhysicsEngine.applyGravity()  được gọi mỗi frame.
 *  - PhysicsEngine.resolveCollision() kiểm tra va chạm block.
 *  - onGround được set bởi PhysicsEngine sau resolve.
 *
 * TODO(DUOC-ENTITY):
 *  - Thêm animation khi có TextureAtlas (Việt Hùng cung cấp).
 *  - Thêm trạng thái HURT khi nhận damage từ mob.
 */
public class Player extends Entity {

    // ─── Hằng số di chuyển ─────────────────────────────────────
    public static final float MOVE_SPEED   = 5f;   // tile/s
    public static final float JUMP_IMPULSE = 12f;  // tile/s (velocity Y khi nhảy)
    public static final float PLAYER_W    = 0.8f;  // tile
    public static final float PLAYER_H    = 1.8f;  // tile

    private static final float HURT_DURATION = 0.5f; // giây bất tử sau khi bị đánh

    // ─── State ─────────────────────────────────────────────────
    private EntityState state      = EntityState.IDLE;
    private float       hurtTimer  = 0f;
    private int         health     = 20;
    private int         maxHealth  = 20;

    // ─── Tài nguyên tạm (placeholder) ──────────────────────────
    private Texture texture; // thay bằng TextureRegion từ Atlas sau

    // ─── Dependency ────────────────────────────────────────────
    private final PhysicsEngine physics;

    // ───────────────────────────────────────────────────────────

    public Player(float x, float y, PhysicsEngine physics) {
        super(x, y, PLAYER_W, PLAYER_H);
        this.physics = physics;

        // TODO(DUOC-ENTITY): thay bằng TextureAtlas + animation từ Việt Hùng khi có
        this.texture = new Texture(Gdx.files.internal("mvp/player/body2.png"));
    }

    // ─── Vòng đời ──────────────────────────────────────────────

    @Override
    public void update(float delta) {
        if (!isAlive) return;

        handleInput(delta);
        physics.applyGravity(this, delta);
        physics.resolveCollision(this, delta); // set onGround bên trong
        updateState(delta);
        updateBounds(); // sync Rectangle sau mỗi frame
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isAlive) return;

        float drawX = facingRight
            ? position.x
            : position.x + width; // flip thủ công khi không dùng animation
        float scaleX = facingRight ? 1f : -1f;

        // Placeholder: vẽ texture thô — thay bằng animation region sau
        batch.draw(
            texture,
            drawX, position.y,
            0, 0,
            width, height,
            scaleX, 1f,
            0,
            0, 0,
            texture.getWidth(), texture.getHeight(),
            false, false
        );
    }

    @Override
    public void dispose() {
        if (texture != null) texture.dispose();
    }

    // ─── Input ─────────────────────────────────────────────────

    /**
     * Đọc input bàn phím, cập nhật velocity X và kích hoạt nhảy.
     * PhysicsEngine sẽ lo velocity Y (gravity + jump).
     */
    private void handleInput(float delta) {
        // Di chuyển ngang
        float moveX = 0;
        if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
            moveX     = -MOVE_SPEED;
            facingRight = false;
        }
        if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
            moveX     = MOVE_SPEED;
            facingRight = true;
        }
        velocity.x = moveX;

        // Nhảy — chỉ được nhảy khi đang đứng đất
        if (onGround &&
            (Gdx.input.isKeyJustPressed(Keys.SPACE)
                || Gdx.input.isKeyJustPressed(Keys.W)
                || Gdx.input.isKeyJustPressed(Keys.UP))) {
            velocity.y = JUMP_IMPULSE;
            onGround   = false;
        }
    }

    // ─── State machine ─────────────────────────────────────────

    private void updateState(float delta) {
        // Đếm ngược hurt invincibility
        if (hurtTimer > 0) {
            hurtTimer -= delta;
            if (hurtTimer <= 0 && state == EntityState.HURT) {
                state = EntityState.IDLE;
            }
            return; // không đổi state khác khi đang HURT
        }

        if (!isAlive) {
            state = EntityState.DEAD;
            return;
        }

        if (!onGround) {
            state = velocity.y > 0 ? EntityState.JUMP : EntityState.FALL;
        } else if (Math.abs(velocity.x) > 0.01f) {
            state = EntityState.RUN;
        } else {
            state = EntityState.IDLE;
        }
    }

    // ─── Damage / Health ───────────────────────────────────────

    /**
     * Gọi khi player nhận damage (từ mob, lửa, ngã...).
     * @param amount lượng damage (half-heart = 1)
     */
    public void takeDamage(int amount) {
        if (hurtTimer > 0 || !isAlive) return; // đang bất tử
        health -= amount;
        if (health <= 0) {
            health   = 0;
            isAlive  = false;
            state    = EntityState.DEAD;
        } else {
            hurtTimer = HURT_DURATION;
            state     = EntityState.HURT;
        }
    }

    // ─── Getters ───────────────────────────────────────────────

    public EntityState getState()    { return state;      }
    public int         getHealth()   { return health;     }
    public int         getMaxHealth(){ return maxHealth;  }
    public boolean     isHurt()      { return hurtTimer > 0; }
}
