package com.main.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.main.game.physics.PhysicsEngine;

/**
 * Mob với AI đơn giản: PATROL <-> CHASE.
 *
 * Hành vi:
 *  - PATROL : đi qua lại trong phạm vi patrolRange tile.
 *             Đảo chiều khi chạm tường (velocity.x bị triệt tiêu bởi PhysicsEngine)
 *             hoặc khi vượt quá giới hạn.
 *  - CHASE  : phát hiện Player trong aggroRadius, lao thẳng về phía Player.
 *             Thoát CHASE khi Player ra ngoài deAggroRadius.
 *  - ATTACK : trong attackRange, gây damage cho Player mỗi attackCooldown giây.
 *
 * Kết nối PhysicsEngine (Lâm Hùng): giống Player.
 *
 * TODO(DUOC-ENTITY):
 *  - Thêm IDLE timer ngắn khi đảo chiều patrol.
 *  - Thêm trạng thái HURT / DEAD animation.
 *  - Mob nhảy qua chướng ngại vật khi chase (cần pathfinding đơn giản).
 */
public class Mob extends Entity {

    // ─── Kiểu mob (mở rộng sau) ───────────────────────────────
    public enum MobType { ZOMBIE, SKELETON }

    // ─── Hằng số mặc định ─────────────────────────────────────
    private static final float DEFAULT_PATROL_SPEED  = 2f;
    private static final float DEFAULT_CHASE_SPEED   = 3.5f;
    private static final float DEFAULT_AGGRO_RADIUS  = 8f;   // tile
    private static final float DEFAULT_DEAGGRO       = 14f;  // tile
    private static final float DEFAULT_ATTACK_RANGE  = 1.2f; // tile
    private static final float DEFAULT_ATTACK_COOL   = 1.5f; // giây
    private static final float DEFAULT_PATROL_RANGE  = 6f;   // tile
    private static final float MOB_W = 0.8f;
    private static final float MOB_H = 1.8f;

    // ─── AI config ────────────────────────────────────────────
    private final float patrolSpeed;
    private final float chaseSpeed;
    private final float aggroRadius;
    private final float deAggroRadius;
    private final float attackRange;
    private final float attackCooldown;
    private final float patrolRange;
    private final int   attackDamage;

    // ─── AI state ─────────────────────────────────────────────
    private enum AIState { PATROL, CHASE, ATTACK }
    private AIState aiState    = AIState.PATROL;

    private float   patrolOriginX;        // vị trí gốc patrol
    private float   attackTimer  = 0f;
    private float   hurtTimer    = 0f;
    private int     health;
    private EntityState state   = EntityState.IDLE;

    // ─── Ref tới player ───────────────────────────────────────
    private Player          target;
    private final PhysicsEngine physics;
    private Texture         texture; // placeholder

    // ───────────────────────────────────────────────────────────

    public Mob(float x, float y, MobType type, Player target, PhysicsEngine physics) {
        super(x, y, MOB_W, MOB_H);
        this.patrolOriginX = x;
        this.target        = target;
        this.physics       = physics;

        // Config theo loại mob
        switch (type) {
            case SKELETON:
                patrolSpeed   = 1.8f;
                chaseSpeed    = 3.0f;
                aggroRadius   = 12f;
                deAggroRadius = 18f;
                attackRange   = 6f;   // bắn tên — TODO: projectile sau
                attackCooldown= 2.0f;
                patrolRange   = 5f;
                attackDamage  = 3;
                health        = 20;
                break;
            case ZOMBIE:
            default:
                patrolSpeed   = DEFAULT_PATROL_SPEED;
                chaseSpeed    = DEFAULT_CHASE_SPEED;
                aggroRadius   = DEFAULT_AGGRO_RADIUS;
                deAggroRadius = DEFAULT_DEAGGRO;
                attackRange   = DEFAULT_ATTACK_RANGE;
                attackCooldown= DEFAULT_ATTACK_COOL;
                patrolRange   = DEFAULT_PATROL_RANGE;
                attackDamage  = 2;
                health        = 20;
                break;
        }
    }

    // ─── Vòng đời ──────────────────────────────────────────────

    @Override
    public void update(float delta) {
        if (!isAlive) return;

        tickTimers(delta);
        updateAI(delta);
        physics.applyGravity(this, delta);
        physics.resolveCollision(this, delta);
        updateEntityState();
        updateBounds();
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isAlive) return;
        if (texture == null) return;

        float drawX  = facingRight ? position.x : position.x + width;
        float scaleX = facingRight ? 1f : -1f;

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

    // ─── AI ────────────────────────────────────────────────────

    private void updateAI(float delta) {
        float distToPlayer = distanceTo(target);

        // Chuyển state AI
        switch (aiState) {
            case PATROL:
                if (distToPlayer <= aggroRadius && target.isAlive()) {
                    aiState = AIState.CHASE;
                } else {
                    doPatrol();
                }
                break;

            case CHASE:
                if (distToPlayer > deAggroRadius || !target.isAlive()) {
                    aiState = AIState.PATROL;
                    velocity.x = 0;
                } else if (distToPlayer <= attackRange) {
                    aiState    = AIState.ATTACK;
                    velocity.x = 0;
                } else {
                    doChase();
                }
                break;

            case ATTACK:
                velocity.x = 0;
                if (distToPlayer > attackRange) {
                    aiState     = AIState.CHASE;
                } else {
                    doAttack(delta);
                }
                break;
        }
    }

    /** Di chuyển qua lại trong patrolRange quanh patrolOriginX */
    private void doPatrol() {
        // Đảo chiều khi vượt biên hoặc velocity bị triệt tiêu (chạm tường)
        boolean hitWall    = (Math.abs(velocity.x) < 0.01f && state == EntityState.RUN);
        boolean outOfRange = (position.x < patrolOriginX - patrolRange)
            || (position.x > patrolOriginX + patrolRange);

        if (hitWall || outOfRange) {
            facingRight = !facingRight;
        }

        velocity.x = facingRight ? patrolSpeed : -patrolSpeed;
    }

    /** Đuổi theo Player */
    private void doChase() {
        boolean playerToRight = target.getX() > position.x;
        facingRight = playerToRight;
        velocity.x  = playerToRight ? chaseSpeed : -chaseSpeed;
    }

    /** Tấn công Player nếu cooldown xong */
    private void doAttack(float delta) {
        if (attackTimer <= 0f) {
            target.takeDamage(attackDamage);
            attackTimer = attackCooldown;
        }
    }

    // ─── Helpers ───────────────────────────────────────────────

    private void tickTimers(float delta) {
        if (attackTimer > 0) attackTimer -= delta;
        if (hurtTimer  > 0) hurtTimer  -= delta;
    }

    private float distanceTo(Entity other) {
        // Tính từ tâm entity
        float dx = (other.getX() + other.width / 2) - (position.x + width / 2);
        float dy = (other.getY() + other.height / 2) - (position.y + height / 2);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private void updateEntityState() {
        if (!isAlive) { state = EntityState.DEAD; return; }
        if (hurtTimer > 0) { state = EntityState.HURT; return; }
        if (!onGround)     { state = velocity.y > 0 ? EntityState.JUMP : EntityState.FALL; return; }
        state = Math.abs(velocity.x) > 0.01f ? EntityState.RUN : EntityState.IDLE;
    }

    // ─── Nhận damage (Player/Arrow gọi) ───────────────────────

    public void takeDamage(int amount) {
        if (hurtTimer > 0 || !isAlive) return;
        health -= amount;
        hurtTimer = 0.3f;
        if (health <= 0) {
            health  = 0;
            isAlive = false;
        }
    }

    // ─── Getters ───────────────────────────────────────────────

    public EntityState getState()  { return state;   }
    public AIState     getAIState(){ return aiState;  }
    public int         getHealth() { return health;   }

    /** Cho phép đổi target (boss, multiplayer sau này) */
    public void setTarget(Player target) { this.target = target; }

    public void setTexture(Texture tex) { this.texture = tex; }
}
