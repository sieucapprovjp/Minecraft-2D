package com.main.game.audio;

import static org.junit.Assert.assertEquals;

import com.main.game.entities.EntityState;
import com.main.game.entities.mob.Mob;
import java.lang.reflect.Field;
import java.util.Random;
import org.junit.Test;

public class MobAmbientAudioControllerTest {

    @Test
    public void nearbyIdleRaidMobPlaysIdleAudioAfterDelay() {
        Mob pillager = new Mob(1f, 0f, Mob.MobType.PILLAGER, null, null, null);
        RecordingAudioManager audioManager = new RecordingAudioManager();
        MobAmbientAudioController controller = new MobAmbientAudioController(new Random(1L));

        controller.updateMobForTest(3f, pillager, 0f, 0f, 0.8f, 1.8f, audioManager);

        assertEquals(1, audioManager.idlePlayCount);
        assertEquals(Mob.MobType.PILLAGER, audioManager.lastIdleType);
        assertEquals(1, controller.trackedMobCount());
    }

    @Test
    public void farIdleRaidMobDoesNotPlayIdleAudio() {
        Mob vindicator = new Mob(30f, 0f, Mob.MobType.VINDICATOR, null, null, null);
        RecordingAudioManager audioManager = new RecordingAudioManager();
        MobAmbientAudioController controller = new MobAmbientAudioController(new Random(1L));

        controller.updateMobForTest(10f, vindicator, 0f, 0f, 0.8f, 1.8f, audioManager);

        assertEquals(0, audioManager.idlePlayCount);
    }

    @Test
    public void movingRavagerPlaysStepAudio() throws Exception {
        Mob ravager = new Mob(1f, 0f, Mob.MobType.RAVAGER, null, null, null);
        setMobState(ravager, EntityState.RUN);
        ravager.setOnGround(true);
        ravager.getVelocity().x = 1f;
        RecordingAudioManager audioManager = new RecordingAudioManager();
        MobAmbientAudioController controller = new MobAmbientAudioController(new Random(1L));

        controller.updateMobForTest(0.1f, ravager, 0f, 0f, 0.8f, 1.8f, audioManager);

        assertEquals(1, audioManager.stepPlayCount);
        assertEquals(Mob.MobType.RAVAGER, audioManager.lastStepType);
    }

    @Test
    public void movingVindicatorWithoutStepAssetsDoesNotPlayStepAudio() throws Exception {
        Mob vindicator = new Mob(1f, 0f, Mob.MobType.VINDICATOR, null, null, null);
        setMobState(vindicator, EntityState.RUN);
        vindicator.setOnGround(true);
        vindicator.getVelocity().x = 1f;
        RecordingAudioManager audioManager = new RecordingAudioManager();
        MobAmbientAudioController controller = new MobAmbientAudioController(new Random(1L));

        controller.updateMobForTest(0.1f, vindicator, 0f, 0f, 0.8f, 1.8f, audioManager);

        assertEquals(0, audioManager.stepPlayCount);
    }

    private static void setMobState(Mob mob, EntityState state) throws Exception {
        Field field = Mob.class.getDeclaredField("state");
        field.setAccessible(true);
        field.set(mob, state);
    }

    private static final class RecordingAudioManager extends AudioManager {
        int idlePlayCount;
        int stepPlayCount;
        Mob.MobType lastIdleType;
        Mob.MobType lastStepType;

        @Override
        public void playMobIdle(Mob.MobType type, float volume) {
            idlePlayCount++;
            lastIdleType = type;
        }

        @Override
        public void playMobStep(Mob.MobType type, float volume) {
            stepPlayCount++;
            lastStepType = type;
        }
    }
}
