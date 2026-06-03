package com.main.game.audio;

import com.main.game.entities.EntityManager;
import com.main.game.entities.EntityState;
import com.main.game.entities.mob.Mob;
import com.main.game.entities.player.Player;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class MobAmbientAudioController {

    private static final float HEARING_RANGE = 12f;
    private static final float MIN_IDLE_SECONDS = 0.8f;
    private static final float INITIAL_IDLE_COOLDOWN_MIN = 0.5f;
    private static final float INITIAL_IDLE_COOLDOWN_MAX = 2.0f;
    private static final float IDLE_COOLDOWN_MIN = 6f;
    private static final float IDLE_COOLDOWN_MAX = 10f;
    private static final float STEP_INTERVAL_SECONDS = 0.48f;
    private static final float IDLE_VOLUME = 0.55f;
    private static final float STEP_VOLUME = 0.55f;

    private final Map<Mob, AmbientState> states = new IdentityHashMap<>();
    private final Random random;

    public MobAmbientAudioController(Random random) {
        this.random = random == null ? new Random() : random;
    }

    public void update(float delta, EntityManager entityManager, Player player, AudioManager audioManager) {
        if (entityManager == null || player == null || audioManager == null) {
            states.clear();
            return;
        }

        float dt = Math.max(0f, delta);
        Set<Mob> activeMobs = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Mob mob : entityManager.getMobs()) {
            if (!isAmbientMob(mob)) {
                continue;
            }
            activeMobs.add(mob);
            updateMob(mob, player.getX(), player.getY(), player.getWidth(), player.getHeight(), audioManager, dt);
        }
        states.keySet().removeIf(mob -> !activeMobs.contains(mob));
    }

    int trackedMobCount() {
        return states.size();
    }

    void updateMobForTest(float delta, Mob mob, float playerX, float playerY,
                          float playerWidth, float playerHeight, AudioManager audioManager) {
        if (!isAmbientMob(mob) || audioManager == null) {
            return;
        }
        updateMob(mob, playerX, playerY, playerWidth, playerHeight, audioManager, Math.max(0f, delta));
    }

    private void updateMob(Mob mob, float playerX, float playerY, float playerWidth, float playerHeight,
                           AudioManager audioManager, float delta) {
        AmbientState state = states.computeIfAbsent(mob, key -> new AmbientState(randomRange(
            INITIAL_IDLE_COOLDOWN_MIN, INITIAL_IDLE_COOLDOWN_MAX)));
        state.idleCooldown = Math.max(0f, state.idleCooldown - delta);
        state.stepCooldown = Math.max(0f, state.stepCooldown - delta);

        if (!isWithinHearingRange(mob, playerX, playerY, playerWidth, playerHeight)) {
            state.idleSeconds = 0f;
            return;
        }

        updateIdleAudio(mob, audioManager, state, delta);
        updateStepAudio(mob, audioManager, state);
    }

    private void updateIdleAudio(Mob mob, AudioManager audioManager, AmbientState state, float delta) {
        if (mob.getState() != EntityState.IDLE || AudioCatalog.mobIdlePaths(mob.getType()).length == 0) {
            state.idleSeconds = 0f;
            return;
        }

        state.idleSeconds += delta;
        if (state.idleSeconds >= MIN_IDLE_SECONDS && state.idleCooldown <= 0f) {
            audioManager.playMobIdle(mob.getType(), IDLE_VOLUME);
            state.idleCooldown = randomRange(IDLE_COOLDOWN_MIN, IDLE_COOLDOWN_MAX);
            state.idleSeconds = 0f;
        }
    }

    private void updateStepAudio(Mob mob, AudioManager audioManager, AmbientState state) {
        if (AudioCatalog.mobStepPaths(mob.getType()).length == 0
            || mob.getState() != EntityState.RUN
            || !mob.isOnGround()
            || Math.abs(mob.getVelocity().x) < 0.05f
            || state.stepCooldown > 0f) {
            return;
        }
        audioManager.playMobStep(mob.getType(), STEP_VOLUME);
        state.stepCooldown = STEP_INTERVAL_SECONDS;
    }

    private boolean isWithinHearingRange(Mob mob, float playerX, float playerY,
                                         float playerWidth, float playerHeight) {
        float mobCenterX = mob.getX() + mob.getWidth() * 0.5f;
        float mobCenterY = mob.getY() + mob.getHeight() * 0.5f;
        float playerCenterX = playerX + playerWidth * 0.5f;
        float playerCenterY = playerY + playerHeight * 0.5f;
        float dx = mobCenterX - playerCenterX;
        float dy = mobCenterY - playerCenterY;
        return dx * dx + dy * dy <= HEARING_RANGE * HEARING_RANGE;
    }

    private boolean isAmbientMob(Mob mob) {
        return mob != null
            && mob.isAlive()
            && AudioCatalog.hasMobAmbientPaths(mob.getType());
    }

    private float randomRange(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    private static final class AmbientState {
        float idleSeconds;
        float idleCooldown;
        float stepCooldown;

        AmbientState(float idleCooldown) {
            this.idleCooldown = idleCooldown;
        }
    }
}
