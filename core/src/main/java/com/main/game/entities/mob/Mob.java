package com.main.game.entities.mob;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.entities.Entity;
import com.main.game.entities.EntityState;
import com.main.game.entities.player.Player;
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
    public enum MobType {
        DOG, TAMED_HORSE,
        COW, PIG, SHEEP, CHICKEN, HORSE, WOLF, CAT, VILLAGER, COD, SALMON, TROPICAL_FISH, PUFFERFISH, DOLPHIN,
        ZOMBIE, HUSK, SKELETON, STRAY, PILLAGER, VINDICATOR, EVOKER, VEX, RAVAGER
    }

    private static final float JUMP_IMPULSE = 10f;
    private static final float PATROL_IDLE_DELAY = 1.2f;
    private static final float HURT_DURATION = 0.3f;
    private static final float DAMAGE_INVULN_DURATION = 0.8f;
    private static final float PASSIVE_PANIC_DURATION = 5f;
    private static final float VEX_HOVER_ABOVE_SURFACE = 2f;

    // ─── AI state ─────────────────────────────────────────────
    public enum AIState { PATROL, CHASE, ATTACK }
    private AIState aiState = AIState.PATROL;

    private float patrolOriginX;
    private float attackTimer = 0f;
    private float hurtTimer   = 0f;
    private float damageInvulnerabilityTimer = 0f;
    private float patrolIdleTimer = 0f;
    private float panicTimer = 0f;
    private float panicDirection = 1f;
    private int   health;
    private EntityState state = EntityState.IDLE;
    private float stateTime   = 0f;

    // ─── Animations ───────────────────────────────────────────
    private Animation<TextureRegion> animIdle;
    private Animation<TextureRegion> animWalk;
    private Animation<TextureRegion> animHurt;
    private final MobAssetPack assets = new MobAssetPack();
    private final MobBrain brain = new MobBrain();
    private final MobRenderer renderer = new MobRenderer();
    private final MobType type;
    private final VillagerProfession villagerProfession;
    private final MobProfile profile;
    private MobRangedAttackListener rangedAttackListener;
    private MobCastSpellListener castSpellListener;
    private MobMeleeAttackListener meleeAttackListener;

    // ─── Refs ─────────────────────────────────────────────────
    private Player          target;
    private final PhysicsEngine physics;
    private final World         world;

    // ───────────────────────────────────────────────────────────

    public Mob(float x, float y, MobType type, Player target, PhysicsEngine physics, World world) {
        this(x, y, type, VillagerProfession.UNEMPLOYED, target, physics, world);
    }

    public Mob(float x, float y, MobType type, VillagerProfession villagerProfession,
               Player target, PhysicsEngine physics, World world) {
        this(x, y, type, villagerProfession, target, physics, world, MobProfile.forType(type));
    }

    private Mob(float x, float y, MobType type, VillagerProfession villagerProfession,
                Player target, PhysicsEngine physics, World world, MobProfile profile) {
        super(x, y, profile.width, profile.height);
        this.patrolOriginX = x;
        this.target        = target;
        this.physics       = physics;
        this.world         = world;
        this.type = type;
        this.villagerProfession = type == MobType.VILLAGER
            ? (villagerProfession == null ? VillagerProfession.UNEMPLOYED : villagerProfession)
            : VillagerProfession.UNEMPLOYED;
        this.profile = profile;
        this.health = profile.maxHealth;

        loadAssets(type, this.villagerProfession);
    }

    public static int getRequiredSpawnWidth(MobType type) {
        return Math.max(1, (int) Math.ceil(MobProfile.forType(type).width));
    }

    public static int getRequiredSpawnHeight(MobType type) {
        return Math.max(1, (int) Math.ceil(MobProfile.forType(type).height));
    }

    public static void disposeSharedAssets() {
        MobAssetPack.disposeSharedAssets();
    }

    public static int getCachedAssetTypeCount() {
        return MobAssetPack.cachedMobTypeCount();
    }

    public static int getLoadedAssetTextureCount() {
        return MobAssetPack.loadedTextureCount();
    }

    // ─── Asset loading ─────────────────────────────────────────

    private void loadAssets(MobType type, VillagerProfession villagerProfession) {
        assets.load(type, villagerProfession);
        animIdle = assets.idle();
        animWalk = assets.walk();
        animHurt = assets.hurt();
    }

    // ─── Vòng đời ──────────────────────────────────────────────

    @Override
    public void update(float delta) {
        if (!isAlive) return;

        stateTime += delta;
        tickTimers(delta);
        brain.update(this);
        if (isPseudoFlying()) {
            updatePseudoFlying(delta);
        } else if (physics != null && world != null) {
            physics.update(this, world, delta);
        }
        updateEntityState();
        updateBounds();
    }

    @Override
    public void render(SpriteBatch batch) {
        renderer.render(batch, this, animIdle, animWalk, animHurt);
    }

    @Override
    public void dispose() {
        assets.dispose();
    }

    void doPatrol() {
        if (patrolIdleTimer > 0f) {
            velocity.x = 0f;
            return;
        }

        boolean hitWall = (Math.abs(velocity.x) < 0.01f && state == EntityState.RUN);
        boolean pastLeft = position.x < patrolOriginX - profile.patrolRange;
        boolean pastRight = position.x > patrolOriginX + profile.patrolRange;
        boolean movingFartherOut = (pastLeft && !facingRight) || (pastRight && facingRight);
        if (hitWall || movingFartherOut) {
            facingRight = !facingRight;
            patrolIdleTimer = PATROL_IDLE_DELAY;
            velocity.x = 0f;
            return;
        }
        velocity.x = facingRight ? profile.patrolSpeed : -profile.patrolSpeed;
        if (!isPseudoFlying() && MobMovementHelper.shouldJumpOverObstacle(this, world, facingRight)) {
            velocity.y = JUMP_IMPULSE;
            onGround = false;
        }
    }

    void doChase() {
        patrolIdleTimer = 0f;
        panicTimer = 0f;
        boolean playerRight = target.getX() > position.x;
        facingRight = playerRight;
        velocity.x  = playerRight ? profile.chaseSpeed : -profile.chaseSpeed;
        if (!isPseudoFlying() && MobMovementHelper.shouldJumpOverObstacle(this, world, facingRight)) {
            velocity.y = JUMP_IMPULSE;
            onGround = false;
        }
    }

    void doPanic() {
        patrolIdleTimer = 0f;
        facingRight = panicDirection > 0f;
        velocity.x = panicDirection * profile.patrolSpeed * 1.35f;
        if (!isPseudoFlying() && MobMovementHelper.shouldJumpOverObstacle(this, world, facingRight)) {
            velocity.y = JUMP_IMPULSE;
            onGround = false;
        }
    }

    void doAttack() {
        if (!MobSightHelper.hasLineOfSight(world, this, target)) {
            aiState = AIState.CHASE;
            return;
        }
        if (attackTimer <= 0f) {
            faceTarget();
            if (profile.attackStyle == MobAttackStyle.RANGED) {
                if (rangedAttackListener != null) {
                    rangedAttackListener.onMobRangedAttack(this, target, profile.attackDamage);
                }
            } else if (profile.attackStyle == MobAttackStyle.CASTER) {
                if (castSpellListener != null) {
                    castSpellListener.onMobCastSpell(this, target, profile.attackDamage);
                }
            } else {
                int healthBefore = target.getHealth();
                target.takeDamage(profile.attackDamage);
                if (target.getHealth() < healthBefore && meleeAttackListener != null) {
                    meleeAttackListener.onMobMeleeHitPlayer(this);
                }
            }
            attackTimer = profile.attackCooldown;
        }
    }

    // ─── Helpers ───────────────────────────────────────────────

    private void tickTimers(float delta) {
        if (attackTimer > 0) attackTimer -= delta;
        if (hurtTimer  > 0) hurtTimer  -= delta;
        if (damageInvulnerabilityTimer > 0) damageInvulnerabilityTimer -= delta;
        if (patrolIdleTimer > 0) patrolIdleTimer -= delta;
        if (panicTimer > 0) panicTimer -= delta;
    }

    float distanceTo(Entity other) {
        float dx = (other.getX() + other.getWidth() / 2f) - (position.x + width / 2f);
        float dy = (other.getY() + other.getHeight() / 2f) - (position.y + height / 2f);
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

    private void faceTarget() {
        if (target != null) {
            facingRight = target.getX() + target.getWidth() * 0.5f >= position.x + width * 0.5f;
        }
    }

    private void updatePseudoFlying(float delta) {
        position.x += velocity.x * Math.max(0f, delta);
        if (world != null) {
            position.x = Math.max(0f, Math.min(Math.max(0f, world.width - width), position.x));
            int tileX = Math.max(0, Math.min(world.width - 1, (int) Math.floor(position.x + width * 0.5f)));
            int surfaceY = world.getSurfaceY(tileX);
            if (surfaceY >= 0) {
                position.y = Math.min(world.height - height, surfaceY + VEX_HOVER_ABOVE_SURFACE);
            }
        }
        velocity.y = 0f;
        onGround = true;
    }

    private boolean isPseudoFlying() {
        return type == MobType.VEX;
    }

    // ─── Nhận damage ──────────────────────────────────────────

    public boolean takeDamage(int amount) {
        if (damageInvulnerabilityTimer > 0 || !isAlive) return false;
        health -= amount;
        hurtTimer = HURT_DURATION;
        damageInvulnerabilityTimer = health > 0 ? DAMAGE_INVULN_DURATION : HURT_DURATION;
        stateTime = 0f;
        if (health <= 0) {
            health  = 0;
            isAlive = false;
        }
        return true;
    }

    public void onPlayerHit(Player player) {
        if (player == null || !isAlive) return;
        if (isHostile()) {
            target = player;
            aiState = AIState.CHASE;
            return;
        }
        panicDirection = player.getX() + player.getWidth() / 2f < position.x + width / 2f ? 1f : -1f;
        panicTimer = PASSIVE_PANIC_DURATION;
        if (onGround) {
            velocity.y = JUMP_IMPULSE * 0.55f;
            onGround = false;
        }
    }

    public void applyKnockback(float x, float y) {
        if (!isAlive) return;
        velocity.x = x;
        velocity.y = Math.max(velocity.y, y);
        onGround = false;
    }

    // ─── Getters ───────────────────────────────────────────────

    public EntityState getState()   { return state;   }
    public AIState     getAIState() { return aiState;  }
    public MobType     getType()    { return type;     }
    public VillagerProfession getVillagerProfession() { return villagerProfession; }
    public int         getHealth()  { return health;   }
    public int         getAllegiance() { return profile.allegiance; }
    public boolean     isTamed()    { return profile.allegiance == MobAllegiance.TAMED; }
    public boolean     isPassive()  { return profile.allegiance == MobAllegiance.PASSIVE; }
    public boolean     isHostile()  { return profile.allegiance == MobAllegiance.HOSTILE; }
    boolean isPanicking() { return panicTimer > 0f; }
    float getStateTime() { return stateTime; }
    float getRenderPixelsPerTile() { return profile.renderPixelsPerTile; }
    float getMaxRenderWidth() { return profile.maxRenderWidth; }
    float getMaxRenderHeight() { return profile.maxRenderHeight; }

    public void setTarget(Player p)   { this.target  = p;   }
    public void setRangedAttackListener(MobRangedAttackListener rangedAttackListener) {
        this.rangedAttackListener = rangedAttackListener;
    }
    public void setCastSpellListener(MobCastSpellListener castSpellListener) {
        this.castSpellListener = castSpellListener;
    }
    public void setMeleeAttackListener(MobMeleeAttackListener meleeAttackListener) {
        this.meleeAttackListener = meleeAttackListener;
    }
    Player getTarget() { return target; }
    void setAiState(AIState aiState) { this.aiState = aiState; }
    float getAggroRadius() { return profile.aggroRadius; }
    float getDeAggroRadius() { return profile.deAggroRadius; }
    float getAttackRange() { return profile.attackRange; }
}
