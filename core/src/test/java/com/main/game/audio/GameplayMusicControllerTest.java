package com.main.game.audio;

import com.main.game.raid.RaidState;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GameplayMusicControllerTest {

    @Test
    public void normalMusicWaitsForCooldownAndCurrentTrackToFinish() {
        RecordingAudioManager audioManager = new RecordingAudioManager();
        GameplayMusicController controller = new GameplayMusicController(
            new FixedRandom(),
            new String[] {"track-a.ogg", "track-b.ogg"},
            "raid.ogg");

        controller.update(29.9f, audioManager, RaidState.IDLE);
        assertEquals(0, audioManager.playCount);

        controller.update(0.2f, audioManager, RaidState.IDLE);
        assertEquals("track-a.ogg", audioManager.lastPath);
        assertFalse(audioManager.lastLooping);
        assertEquals(1, audioManager.playCount);

        controller.update(100f, audioManager, RaidState.IDLE);
        assertEquals(1, audioManager.playCount);

        audioManager.playing = false;
        controller.update(30.1f, audioManager, RaidState.IDLE);
        assertEquals(2, audioManager.playCount);
    }

    @Test
    public void raidStopsCurrentMusicThenStartsRubedoAfterDelay() {
        RecordingAudioManager audioManager = new RecordingAudioManager();
        GameplayMusicController controller = new GameplayMusicController(
            new FixedRandom(),
            new String[] {"track-a.ogg"},
            "raid.ogg");

        controller.update(30f, audioManager, RaidState.IDLE);
        assertEquals("track-a.ogg", audioManager.lastPath);
        assertTrue(audioManager.playing);

        controller.update(0f, audioManager, RaidState.COUNTDOWN);
        assertEquals(1, audioManager.stopCount);
        assertFalse(audioManager.playing);

        controller.update(4.9f, audioManager, RaidState.COUNTDOWN);
        assertEquals(1, audioManager.playCount);

        controller.update(0.1f, audioManager, RaidState.COUNTDOWN);
        assertEquals("raid.ogg", audioManager.lastPath);
        assertFalse(audioManager.lastLooping);
        assertEquals(2, audioManager.playCount);

        controller.update(0f, audioManager, RaidState.VICTORY);
        assertEquals(2, audioManager.stopCount);
        assertFalse(audioManager.playing);

        controller.update(29.9f, audioManager, RaidState.VICTORY);
        assertEquals(2, audioManager.playCount);
        controller.update(0.2f, audioManager, RaidState.VICTORY);
        assertEquals("track-a.ogg", audioManager.lastPath);
        assertEquals(3, audioManager.playCount);
    }

    private static final class FixedRandom extends Random {
        @Override
        public float nextFloat() {
            return 0f;
        }

        @Override
        public int nextInt(int bound) {
            return 0;
        }
    }

    private static final class RecordingAudioManager extends AudioManager {
        private String lastPath;
        private boolean lastLooping;
        private boolean playing;
        private int playCount;
        private int stopCount;

        @Override
        public void playMusicPath(String path, boolean looping) {
            lastPath = path;
            lastLooping = looping;
            playing = true;
            playCount++;
        }

        @Override
        public void stopMusic() {
            playing = false;
            stopCount++;
        }

        @Override
        public boolean isMusicPlaying() {
            return playing;
        }
    }
}
