package com.main.game.audio;

import com.main.game.raid.RaidState;

import java.util.Random;

public class GameplayMusicController {

    static final float NORMAL_COOLDOWN_MIN_SECONDS = 30f;
    static final float NORMAL_COOLDOWN_MAX_SECONDS = 100f;
    static final float RAID_MUSIC_DELAY_SECONDS = 5f;

    private final Random random;
    private final String[] normalTracks;
    private final String raidTrack;

    private boolean raidActive;
    private boolean raidTrackStarted;
    private float normalCooldownSeconds;
    private float raidDelaySeconds;

    public GameplayMusicController(Random random) {
        this(random, AudioCatalog.ingameMusicPaths(), AudioCatalog.raidMusicPath());
    }

    GameplayMusicController(Random random, String[] normalTracks, String raidTrack) {
        this.random = random == null ? new Random() : random;
        this.normalTracks = normalTracks == null ? new String[0] : normalTracks.clone();
        this.raidTrack = raidTrack;
        scheduleNormalCooldown();
    }

    public void update(float delta, AudioManager audioManager, RaidState raidState) {
        if (audioManager == null) {
            return;
        }
        float dt = Math.max(0f, delta);
        boolean nextRaidActive = isRaidActive(raidState);
        if (nextRaidActive != raidActive) {
            if (nextRaidActive) {
                startRaidOverride(audioManager);
            } else {
                endRaidOverride(audioManager);
            }
        }

        if (raidActive) {
            updateRaidMusic(dt, audioManager);
        } else {
            updateNormalMusic(dt, audioManager);
        }
    }

    public void stop(AudioManager audioManager) {
        raidActive = false;
        raidTrackStarted = false;
        if (audioManager != null) {
            audioManager.stopMusic();
        }
        scheduleNormalCooldown();
    }

    private void startRaidOverride(AudioManager audioManager) {
        raidActive = true;
        raidTrackStarted = false;
        raidDelaySeconds = RAID_MUSIC_DELAY_SECONDS;
        audioManager.stopMusic();
    }

    private void endRaidOverride(AudioManager audioManager) {
        raidActive = false;
        raidTrackStarted = false;
        audioManager.stopMusic();
        scheduleNormalCooldown();
    }

    private void updateRaidMusic(float delta, AudioManager audioManager) {
        if (raidTrackStarted || raidTrack == null) {
            return;
        }
        raidDelaySeconds = Math.max(0f, raidDelaySeconds - delta);
        if (raidDelaySeconds <= 0f) {
            audioManager.playMusicPath(raidTrack, false);
            raidTrackStarted = true;
        }
    }

    private void updateNormalMusic(float delta, AudioManager audioManager) {
        if (normalTracks.length == 0 || audioManager.isMusicPlaying()) {
            return;
        }
        normalCooldownSeconds = Math.max(0f, normalCooldownSeconds - delta);
        if (normalCooldownSeconds <= 0f) {
            audioManager.playMusicPath(randomNormalTrack(), false);
            scheduleNormalCooldown();
        }
    }

    private String randomNormalTrack() {
        return normalTracks[random.nextInt(normalTracks.length)];
    }

    private void scheduleNormalCooldown() {
        float span = NORMAL_COOLDOWN_MAX_SECONDS - NORMAL_COOLDOWN_MIN_SECONDS;
        normalCooldownSeconds = NORMAL_COOLDOWN_MIN_SECONDS + random.nextFloat() * span;
    }

    private static boolean isRaidActive(RaidState state) {
        return state == RaidState.COUNTDOWN
            || state == RaidState.WAVE_ACTIVE
            || state == RaidState.BETWEEN_WAVES;
    }
}
