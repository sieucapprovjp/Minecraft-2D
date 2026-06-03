package com.main.game.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.main.game.GameState;
import com.main.game.entities.mob.Mob;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AudioManager {

    private static final float DEFAULT_SOUND_VOLUME = 0.75f;
    private static final float MUSIC_VOLUME = 0.35f;

    private final EnumMap<AudioId, Sound[]> soundCache = new EnumMap<>(AudioId.class);
    private final Map<String, Sound[]> pathGroupCache = new HashMap<>();
    private final Random random = new Random();

    private boolean soundEnabled = true;
    private boolean musicEnabled = true;
    private AudioId requestedMusicId;
    private String requestedMusicPath;
    private boolean requestedMusicLooping;
    private AudioId currentMusicId;
    private String currentMusicPath;
    private Music currentMusic;

    public void updateSettings(GameState gameState) {
        if (gameState == null) {
            return;
        }
        soundEnabled = gameState.soundEnabled;
        boolean nextMusicEnabled = gameState.musicEnabled;
        if (musicEnabled != nextMusicEnabled) {
            musicEnabled = nextMusicEnabled;
            if (!musicEnabled) {
                stopActiveMusic();
            } else if (requestedMusicPath != null) {
                playMusicPath(requestedMusicPath, requestedMusicLooping);
            } else if (requestedMusicId != null) {
                playMusic(requestedMusicId);
            }
        }
    }

    public void play(AudioId id) {
        play(id, DEFAULT_SOUND_VOLUME);
    }

    public void play(AudioId id, float volume) {
        if (!soundEnabled || id == null) {
            return;
        }
        Sound[] sounds = soundCache.computeIfAbsent(id, key -> loadSounds(AudioCatalog.soundPaths(key)));
        playRandom(sounds, volume);
    }

    public void playBlockBreak(String blockId) {
        if (!soundEnabled || blockId == null) {
            return;
        }
        playPathGroup(AudioCatalog.blockBreakPaths(blockId), DEFAULT_SOUND_VOLUME);
    }

    public void playMobHurt(Mob.MobType type) {
        if (!soundEnabled || type == null) {
            return;
        }
        playPathGroup(AudioCatalog.mobHurtPaths(type), DEFAULT_SOUND_VOLUME);
    }

    public void playMobDeath(Mob.MobType type) {
        if (!soundEnabled || type == null) {
            return;
        }
        playPathGroup(AudioCatalog.mobDeathPaths(type), DEFAULT_SOUND_VOLUME);
    }

    public void playMobIdle(Mob.MobType type, float volume) {
        if (!soundEnabled || type == null) {
            return;
        }
        playPathGroup(AudioCatalog.mobIdlePaths(type), volume);
    }

    public void playMobStep(Mob.MobType type, float volume) {
        if (!soundEnabled || type == null) {
            return;
        }
        playPathGroup(AudioCatalog.mobStepPaths(type), volume);
    }

    public void playMobAttack(Mob.MobType type) {
        if (!soundEnabled || type == null) {
            return;
        }
        playPathGroup(AudioCatalog.mobAttackPaths(type), DEFAULT_SOUND_VOLUME);
    }

    private void playPathGroup(String[] paths, float volume) {
        if (paths.length == 0) {
            return;
        }
        Sound[] sounds = pathGroupCache.computeIfAbsent(cacheKey(paths), key -> loadSounds(paths));
        playRandom(sounds, volume);
    }

    public void playMusic(AudioId id) {
        requestedMusicId = id;
        requestedMusicPath = null;
        if (!musicEnabled || id == null) {
            stopActiveMusic();
            return;
        }
        String path = AudioCatalog.musicPath(id);
        if (path == null) {
            return;
        }
        playMusicPath(path, true, id);
    }

    public void playMusicPath(String path, boolean looping) {
        playMusicPath(path, looping, null);
    }

    private void playMusicPath(String path, boolean looping, AudioId id) {
        requestedMusicId = id;
        requestedMusicPath = path;
        requestedMusicLooping = looping;
        if (!musicEnabled || path == null) {
            stopActiveMusic();
            return;
        }
        if (currentMusic != null && path.equals(currentMusicPath)) {
            currentMusic.setLooping(looping);
            if (!currentMusic.isPlaying()) {
                currentMusic.setPosition(0f);
                currentMusic.play();
            }
            return;
        }

        stopActiveMusic();
        FileHandle file = Gdx.files.internal(path);
        if (!file.exists()) {
            Gdx.app.log("AudioManager", "Missing music asset: " + path);
            return;
        }
        try {
            currentMusic = Gdx.audio.newMusic(file);
            currentMusicId = id;
            currentMusicPath = path;
            currentMusic.setLooping(looping);
            currentMusic.setVolume(MUSIC_VOLUME);
            currentMusic.play();
        } catch (RuntimeException ex) {
            Gdx.app.error("AudioManager", "Failed to load music asset: " + path, ex);
            currentMusic = null;
            currentMusicId = null;
            currentMusicPath = null;
        }
    }

    public void stopMusic() {
        requestedMusicId = null;
        requestedMusicPath = null;
        stopActiveMusic();
    }

    public boolean isMusicPlaying() {
        return currentMusic != null && currentMusic.isPlaying();
    }

    public void dispose() {
        stopMusic();
        for (Sound[] sounds : soundCache.values()) {
            disposeSounds(sounds);
        }
        for (Sound[] sounds : pathGroupCache.values()) {
            disposeSounds(sounds);
        }
        soundCache.clear();
        pathGroupCache.clear();
    }

    private Sound[] loadSounds(String[] paths) {
        if (paths == null || paths.length == 0) {
            return new Sound[0];
        }
        List<Sound> loaded = new ArrayList<>();
        for (String path : paths) {
            FileHandle file = Gdx.files.internal(path);
            if (!file.exists()) {
                Gdx.app.log("AudioManager", "Missing sound asset: " + path);
                continue;
            }
            String unsupportedReason = AudioFileSupport.unsupportedReason(file);
            if (unsupportedReason != null) {
                Gdx.app.log("AudioManager", "Skipping unsupported sound asset: " + path + " (" + unsupportedReason + ")");
                continue;
            }
            try {
                loaded.add(Gdx.audio.newSound(file));
            } catch (RuntimeException ex) {
                Gdx.app.error("AudioManager", "Failed to load sound asset: " + path, ex);
            }
        }
        return loaded.toArray(new Sound[0]);
    }

    private void playRandom(Sound[] sounds, float volume) {
        if (sounds == null || sounds.length == 0) {
            return;
        }
        Sound sound = sounds[random.nextInt(sounds.length)];
        sound.play(volume);
    }

    private String cacheKey(String[] paths) {
        return String.join("|", paths);
    }

    private void stopActiveMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
        }
        currentMusicId = null;
        currentMusicPath = null;
    }

    private void disposeSounds(Sound[] sounds) {
        if (sounds == null) {
            return;
        }
        for (Sound sound : sounds) {
            if (sound != null) {
                sound.dispose();
            }
        }
    }
}
