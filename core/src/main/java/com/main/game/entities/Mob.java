package com.main.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.physics.PhysicsEngine;
import com.main.game.world.World;

/**
 * Mob với AI đơn giản: PATROL <-> CHASE.
 *
 * Assets dùng (cow):
 *  - IDLE/LOOK : mvp/mob/cow/cow_look.png
 *  - RUN/PATROL: mvp/mob/cow/cow_walk_1.png .. cow_walk_6.png
 *  - HURT      : mvp/mob/cow/cow_hurt.png
 *
 * Hành vi:
 *  - PATROL : đi qua lại trong phạm vi patrolRange tile.
 *  - CHASE  : phát hiện Player trong aggroRadius, lao về phía Player.
 *  - ATTACK : trong attackRange, gây damage mỗi attackCooldown giây.
 *
 * TODO(DUOC-ENTITY):
 *  - Thêm IDLE timer ngắn khi đảo chiều patrol.
 *  - Mob nhảy qua chướng ngại vật khi chase (pathfinding đơn giản).
 *  - Thêm MobType riêng khi có asset skeleton.
 */
public class Mob extends Entity {

    // ─── Kiểu mob ─────────────────────────────────────────────
    public enum MobType { ZOMBIE, SKELETON }

    // ─── Hằng số mặc định ─────────────────────────────────────
    private static final float DEFAULT_PATROL_SPEED  = 2f;
    private static final float DEFAULT_CHASE_SPEED   = 3.5f;
    private static final float DEFAULT_AGGRO_RADIUS  = 8f;
    private static final float DEFAULT_DEAGGRO       = 14f;
    private static final float DEFAULT_ATTACK_RANGE  = 1.2f;
    private static final float DEFAULT_ATTACK_COOL   = 1.5f;
    private static final float DEFAULT_PATROL_RANGE  = 6f;
    private static final float MOB_W = 0.8f;
    private static final float MOB_H = 1.8f;

    private static final float WALK_FRAME_DUR = 0.12f; // giây/frame (6 frames cow walk)
    private static final float HURT_BLINK_DUR = 0.08f;

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
    private AIState aiState = AIState.PATROL;

    private float patrolOriginX;
    private float attackTimer = 0f;
    private float hurtTimer   = 0f;
    private int   health;
    private EntityState state = EntityState.IDLE;
    private float stateTime   = 0f;

    // ─── Textures ─────────────────────────────────────────────
    private Texture texLook;
    private Texture texHurt;
    private Texture[] texWalk; // cow_walk_1 .. cow_walk_6

    // ─── Animations ───────────────────────────────────────────
    private Animation<TextureRegion> animIdle;
    private Animation<TextureRegion> animWalk;
    private Animation<TextureRegion> animHurt;

    // ─── Refs ─────────────────────────────────────────────────
    private Player          target;
    private final PhysicsEngine physics;
    private final World         world;

    // ───────────────────────────────────────────────────────────

    public Mob(float x, float y, MobType type, Player target, PhysicsEngine physics, World world) {
        super(x, y, MOB_W, MOB_H);
        this.patrolOriginX = x;
        this.target        = target;
        this.physics       = physics;
        this.world         = world;

        switch (type) {
            case SKELETON:
                patrolSpeed    = 1.8f;
                chaseSpeed     = 3.0f;
                aggroRadius    = 12f;
                deAggroRadius  = 18f;
                attackRange    = 6f;   // bắn tên — TODO: projectile sau
                attackCooldown = 2.0f;
                patrolRange    = 5f;
                attackDamage   = 3;
                health         = 20;
                break;
            case ZOMBIE:
            default:
                patrolSpeed    = DEFAULT_PATROL_SPEED;
                chaseSpeed     = DEFAULT_CHASE_SPEED;
                aggroRadius    = DEFAULT_AGGRO_RADIUS;
                deAggroRadius  = DEFAULT_DEAGGRO;
                attackRange    = DEFAULT_ATTACK_RANGE;
                attackCooldown = DEFAULT_ATTACK_COOL;
                patrolRange    = DEFAULT_PATROL_RANGE;
                attackDamage   = 2;
                health         = 20;
                break;
        }

        loadAssets();
    }

    // ─── Asset loading ─────────────────────────────────────────

    private void loadAssets() {
        texLook = new Texture(Gdx.files.internal("mvp/mob/cow/cow_look.png"));
        texHurt = new Texture(Gdx.files.internal("mvp/mob/cow/cow_hurt.png"));

        // Tải 6 frame đi bộ
        texWalk = new Texture[6];
        for (int i = 0; i < 6; i++) {
            texWalk[i] = new Texture(
                Gdx.files.internal("mvp/mob/cow/cow_walk_" + (i + 1) + ".png"));
        }

        // IDLE: chỉ dùng cow_look (1 frame, loop chậm)
        animIdle = new Animation<>(0.6f,
            new TextureRegion(texLook));
        animIdle.setPlayMode(Animation.PlayMode.LOOP);

        // WALK: 6 frame cow_walk_1..6
        TextureRegion[] walkFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) walkFrames[i] = new TextureRegion(texWalk[i]);
        animWalk = new Animation<>(WALK_FRAME_DUR, walkFrames);
        animWalk.setPlayMode(Animation.PlayMode.LOOP);

        // HURT: blink giữa cow_hurt và cow_look
        animHurt = new Animation<>(HURT_BLINK_DUR,
            new TextureRegion(texHurt),
            new TextureRegion(texLook));
        animHurt.setPlayMode(Animation.PlayMode.LOOP);
    }

    // ─── Vòng đời ──────────────────────────────────────────────

    @Override
    public void update(float delta) {
        if (!isAlive) return;

        stateTime += delta;
        tickTimers(delta);
        updateAI(delta);
        physics.update(this, world, delta);
        updateEntityState();
        updateBounds();
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isAlive) return;

        TextureRegion frame = getCurrentFrame();
        if (frame == null) return;

        boolean needFlip = (!facingRight && !frame.isFlipX())
            || ( facingRight &&  frame.isFlipX());
        if (needFlip) frame.flip(true, false);

        batch.draw(frame,
            position.x, position.y,
            width, height);
    }

    @Override
    public void dispose() {
        texLook.dispose();
        texHurt.dispose();
        for (Texture t : texWalk) if (t != null) t.dispose();
    }

    // ─── Animation helper ──────────────────────────────────────

    private TextureRegion getCurrentFrame() {
        switch (state) {
            case RUN:  return animWalk.getKeyFrame(stateTime);
            case HURT: return animHurt.getKeyFrame(stateTime);
            case IDLE:
            default:   return animIdle.getKeyFrame(stateTime);
        }
    }

    // ─── AI ────────────────────────────────────────────────────

    private void updateAI(float delta) {
        float dist = distanceTo(target);

        switch (aiState) {
            case PATROL:
                if (dist <= aggroRadius && target.isAlive()) {
                    aiState = AIState.CHASE;
                } else {
                    doPatrol();
                }
                break;

            case CHASE:
                if (dist > deAggroRadius || !target.isAlive()) {
                    aiState    = AIState.PATROL;
                    velocity.x = 0;
                } else if (dist <= attackRange) {
                    aiState    = AIState.ATTACK;
                    velocity.x = 0;
                } else {
                    doChase();
                }
                break;

            case ATTACK:
                velocity.x = 0;
                if (dist > attackRange) {
                    aiState = AIState.CHASE;
                } else {
                    doAttack(delta);
                }
                break;
        }
    }

    private void doPatrol() {
        boolean hitWall    = (Math.abs(velocity.x) < 0.01f && state == EntityState.RUN);
        boolean outOfRange = (position.x < patrolOriginX - patrolRange)
            || (position.x > patrolOriginX + patrolRange);
        if (hitWall || outOfRange) facingRight = !facingRight;
        velocity.x = facingRight ? patrolSpeed : -patrolSpeed;
    }

    private void doChase() {
        boolean playerRight = target.getX() > position.x;
        facingRight = playerRight;
        velocity.x  = playerRight ? chaseSpeed : -chaseSpeed;
    }

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
        float dx = (other.getX() + other.width / 2) - (position.x + width / 2);
        float dy = (other.getY() + other.height / 2) - (position.y + height / 2);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private void updateEntityState() {
        EntityState prev = state;
        if (!isAlive)      { state = EntityState.DEAD; }
        else if (hurtTimer > 0) { state = EntityState.HURT; }
        else if (!onGround){ state = velocity.y > 0 ? EntityState.JUMP : EntityState.FALL; }
        else               { state = Math.abs(velocity.x) > 0.01f ? EntityState.RUN : EntityState.IDLE; }
        if (state != prev) stateTime = 0f;
    }

    // ─── Nhận damage ──────────────────────────────────────────

    public void takeDamage(int amount) {
        if (hurtTimer > 0 || !isAlive) return;
        health -= amount;
        hurtTimer = 0.3f;
        stateTime = 0f;
        if (health <= 0) {
            health  = 0;
            isAlive = false;
        }
    }

    // ─── Getters ───────────────────────────────────────────────

    public EntityState getState()   { return state;   }
    public AIState     getAIState() { return aiState;  }
    public int         getHealth()  { return health;   }

    public void setTarget(Player p)   { this.target  = p;   }
}
