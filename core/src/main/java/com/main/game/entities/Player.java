package com.main.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.physics.PhysicsEngine;

/**
 * Class Player - điều khiển bởi người chơi.
 *
 * State machine: IDLE -> RUN -> JUMP -> FALL -> HURT -> DEAD
 * Input: A/D hoặc ←/→ để di chuyển, SPACE/W/↑ để nhảy.
 *
 * Assets dùng:
 *  - IDLE  : mvp/player/idle.png
 *  - RUN   : mvp/player/walk_1.png .. walk_2.png
 *  - JUMP  : mvp/player/jump_0.png
 *  - FALL  : mvp/player/jump_1.png
 *  - HURT  : mvp/player/body2.png   (tạm dùng làm hurt flash)
 *
 * Kết nối PhysicsEngine (Lâm Hùng):
 *  - PhysicsEngine.applyGravity()  được gọi mỗi frame.
 *  - PhysicsEngine.resolveCollision() kiểm tra va chạm block.
 *  - onGround được set bởi PhysicsEngine sau resolve.
 */
public class Player extends Entity {

    // ─── Hằng số di chuyển ─────────────────────────────────────
    public static final float MOVE_SPEED   = 5f;   // tile/s
    public static final float JUMP_IMPULSE = 12f;  // tile/s
    public static final float PLAYER_W    = 0.8f;  // tile
    public static final float PLAYER_H    = 1.8f;  // tile

    private static final float HURT_DURATION   = 0.5f;
    private static final float WALK_FRAME_DUR  = 0.15f; // giây/frame
    private static final float HURT_FLASH_DUR  = 0.08f; // giây/frame blink

    // ─── Textures ──────────────────────────────────────────────
    private Texture texIdle;
    private Texture texWalk1, texWalk2;
    private Texture texJump0, texJump1;
    private Texture texHurt;

    // ─── Animations ────────────────────────────────────────────
    private Animation<TextureRegion> animIdle;
    private Animation<TextureRegion> animRun;
    private Animation<TextureRegion> animJump;
    private Animation<TextureRegion> animFall;
    private Animation<TextureRegion> animHurt;

    private float stateTime = 0f;

    // ─── State ─────────────────────────────────────────────────
    private EntityState state     = EntityState.IDLE;
    private float       hurtTimer = 0f;
    private int         health    = 20;
    private int         maxHealth = 20;
    private boolean     isBanned  = false;

    // ─── Dependency ────────────────────────────────────────────
    private final PhysicsEngine physics;

    // ───────────────────────────────────────────────────────────

    public Player(float x, float y, PhysicsEngine physics) {
        super(x, y, PLAYER_W, PLAYER_H);
        this.physics = physics;
        loadAssets();
    }

    // ─── Asset loading ─────────────────────────────────────────

    private void loadAssets() {
        texIdle  = new Texture(Gdx.files.internal("mvp/player/idle.png"));
        texWalk1 = new Texture(Gdx.files.internal("mvp/player/walk_1.png"));
        texWalk2 = new Texture(Gdx.files.internal("mvp/player/walk_2.png"));
        texJump0 = new Texture(Gdx.files.internal("mvp/player/jump_0.png"));
        texJump1 = new Texture(Gdx.files.internal("mvp/player/jump_1.png"));
        texHurt  = new Texture(Gdx.files.internal("mvp/player/body2.png"));

        animIdle = new Animation<>(0.5f,
            new TextureRegion(texIdle));
        animIdle.setPlayMode(Animation.PlayMode.LOOP);

        animRun = new Animation<>(WALK_FRAME_DUR,
            new TextureRegion(texWalk1),
            new TextureRegion(texWalk2));
        animRun.setPlayMode(Animation.PlayMode.LOOP);

        // JUMP: apex frame (jump_0), rising
        animJump = new Animation<>(0.1f,
            new TextureRegion(texJump0));
        animJump.setPlayMode(Animation.PlayMode.NORMAL);

        // FALL: descending frame (jump_1)
        animFall = new Animation<>(0.1f,
            new TextureRegion(texJump1));
        animFall.setPlayMode(Animation.PlayMode.NORMAL);

        // HURT: flash blink using body2
        animHurt = new Animation<>(HURT_FLASH_DUR,
            new TextureRegion(texHurt),
            new TextureRegion(texIdle));
        animHurt.setPlayMode(Animation.PlayMode.LOOP);
    }

    // ─── Vòng đời ──────────────────────────────────────────────

    @Override
    public void update(float delta) {
        if (!isAlive || isBanned) return;

        stateTime += delta;
        handleInput(delta);
        physics.applyGravity(this, delta);
        physics.resolveCollision(this, delta);
        updateState(delta);
        updateBounds();
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isAlive) return;

        TextureRegion frame = getCurrentFrame();
        if (frame == null) return;

        // Flip theo hướng nhìn
        boolean needFlip = (!facingRight && !frame.isFlipX())
            || ( facingRight &&  frame.isFlipX());
        if (needFlip) frame.flip(true, false);

        batch.draw(frame,
            position.x, position.y,
            width, height);
    }

    public void ban() {
        isBanned = true;
    }

    public boolean isBanned() {
        return isBanned;
    }

    @Override
    public void dispose() {
        texIdle.dispose();
        texWalk1.dispose();
        texWalk2.dispose();
        texJump0.dispose();
        texJump1.dispose();
        texHurt.dispose();
    }

    // ─── Animation helper ──────────────────────────────────────

    private TextureRegion getCurrentFrame() {
        switch (state) {
            case RUN:  return animRun.getKeyFrame(stateTime);
            case JUMP: return animJump.getKeyFrame(stateTime);
            case FALL: return animFall.getKeyFrame(stateTime);
            case HURT: return animHurt.getKeyFrame(stateTime);
            case IDLE:
            default:   return animIdle.getKeyFrame(stateTime);
        }
    }

    // ─── Input ─────────────────────────────────────────────────

    private void handleInput(float delta) {
        float moveX = 0;
        if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
            moveX       = -MOVE_SPEED;
            facingRight = false;
        }
        if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
            moveX       = MOVE_SPEED;
            facingRight = true;
        }
        velocity.x = moveX;

        if (onGround &&
            (Gdx.input.isKeyJustPressed(Keys.SPACE)
                || Gdx.input.isKeyJustPressed(Keys.W)
                || Gdx.input.isKeyJustPressed(Keys.UP))) {
            velocity.y = JUMP_IMPULSE;
            onGround   = false;
            stateTime  = 0f; // reset để jump animation bắt đầu từ đầu
        }
    }

    // ─── State machine ─────────────────────────────────────────

    private void updateState(float delta) {
        EntityState prev = state;

    @Override
    public void dispose() {
        texIdle.dispose();
        texWalk1.dispose();
        texWalk2.dispose();
        texJump0.dispose();
        texJump1.dispose();
        texHurt.dispose();
    }

    // ─── Animation helper ──────────────────────────────────────

    private TextureRegion getCurrentFrame() {
        switch (state) {
            case RUN:  return animRun.getKeyFrame(stateTime);
            case JUMP: return animJump.getKeyFrame(stateTime);
            case FALL: return animFall.getKeyFrame(stateTime);
            case HURT: return animHurt.getKeyFrame(stateTime);
            case IDLE:
            default:   return animIdle.getKeyFrame(stateTime);
        }
    }

    // ─── Input ─────────────────────────────────────────────────

    private void handleInput(float delta) {
        float moveX = 0;
        if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
            moveX       = -MOVE_SPEED;
            facingRight = false;
        }
        if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
            moveX       = MOVE_SPEED;
            facingRight = true;
        }
        velocity.x = moveX;

        if (onGround &&
            (Gdx.input.isKeyJustPressed(Keys.SPACE)
                || Gdx.input.isKeyJustPressed(Keys.W)
                || Gdx.input.isKeyJustPressed(Keys.UP))) {
            velocity.y = JUMP_IMPULSE;
            onGround   = false;
            stateTime  = 0f; // reset để jump animation bắt đầu từ đầu
        }
    }

    // ─── State machine ─────────────────────────────────────────

    private void updateState(float delta) {
        EntityState prev = state;

        if (hurtTimer > 0) {
            hurtTimer -= delta;
            state = EntityState.HURT;
            if (hurtTimer <= 0) {
                state     = EntityState.IDLE;
                stateTime = 0f;
            }
            return;
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

        // Reset stateTime khi đổi state để animation không bị lệch
        if (state != prev) stateTime = 0f;
    }

    // ─── Damage / Health ───────────────────────────────────────

    public void takeDamage(int amount) {
        if (hurtTimer > 0 || !isAlive) return;
        health -= amount;
        if (health <= 0) {
            health  = 0;
            isAlive = false;
            state   = EntityState.DEAD;
        } else {
            hurtTimer = HURT_DURATION;
            state     = EntityState.HURT;
            stateTime = 0f;
        }
    }

    // ─── Getters ───────────────────────────────────────────────

    public EntityState getState()     { return state;      }
    public int         getHealth()    { return health;     }
    public int         getMaxHealth() { return maxHealth;  }
    public boolean     isHurt()       { return hurtTimer > 0; }
}
