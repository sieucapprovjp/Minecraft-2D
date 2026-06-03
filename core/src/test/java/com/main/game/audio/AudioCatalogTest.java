package com.main.game.audio;

import com.main.game.entities.mob.Mob;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class AudioCatalogTest {

    @Test
    public void menuMusicUsesProvidedMenuAsset() {
        assertEquals("audio/ui_menu/music.mp3", AudioCatalog.musicPath(AudioId.MENU_MUSIC));
    }

    @Test
    public void playerDeathReusesPlayerHurtForV1() {
        assertArrayEquals(
            new String[] {"audio/player/hurt.mp3"},
            AudioCatalog.soundPaths(AudioId.PLAYER_DEATH)
        );
    }

    @Test
    public void blockBreakUsesMaterialSpecificAssetsOnlyWhenAvailable() {
        assertArrayEquals(
            new String[] {
                "audio/block/stone1.mp3",
                "audio/block/stone2.mp3",
                "audio/block/stone3.mp3",
                "audio/block/stone4.mp3"
            },
            AudioCatalog.blockBreakPaths("stone")
        );
        assertEquals(0, AudioCatalog.blockBreakPaths("grass").length);
    }

    @Test
    public void mobDeathCanReuseMobHurtMappingForV1() {
        assertArrayEquals(
            new String[] {
                "audio/mobs/zombiehurt1.wav",
                "audio/mobs/zombiehurt2.wav"
            },
            AudioCatalog.mobDeathPaths(Mob.MobType.ZOMBIE)
        );
    }

    @Test
    public void evokerUsesDedicatedAudioAssets() {
        assertArrayEquals(
            new String[] {"audio/mobs/evoker/cast2.mp3"},
            AudioCatalog.soundPaths(AudioId.EVOKER_CAST)
        );
        assertArrayEquals(
            new String[] {"audio/mobs/evoker/fangs.mp3"},
            AudioCatalog.soundPaths(AudioId.EVOKER_FANGS)
        );
        assertArrayEquals(
            new String[] {
                "audio/mobs/evoker/idle1.mp3",
                "audio/mobs/evoker/idle2.mp3"
            },
            AudioCatalog.soundPaths(AudioId.EVOKER_IDLE)
        );
        assertArrayEquals(
            new String[] {"audio/mobs/evoker/hurt1.mp3"},
            AudioCatalog.mobHurtPaths(Mob.MobType.EVOKER)
        );
        assertArrayEquals(
            new String[] {"audio/mobs/evoker/death1.mp3"},
            AudioCatalog.mobDeathPaths(Mob.MobType.EVOKER)
        );
    }

    @Test
    public void raidUsesHornAndCelebrateAudio() {
        assertArrayEquals(
            new String[] {"audio/mobs/pillager/horn_celebrate.ogg"},
            AudioCatalog.soundPaths(AudioId.RAID_WAVE_HORN)
        );
        assertEquals(8, AudioCatalog.soundPaths(AudioId.RAID_CELEBRATE).length);
    }

    @Test
    public void pillagerUsesOggIdleHurtDeathAndIdleAttackAudio() {
        assertArrayEquals(
            new String[] {
                "audio/mobs/pillager/idle1.ogg",
                "audio/mobs/pillager/idle2.ogg",
                "audio/mobs/pillager/idle3.ogg",
                "audio/mobs/pillager/idle4.ogg"
            },
            AudioCatalog.mobIdlePaths(Mob.MobType.PILLAGER)
        );
        assertArrayEquals(AudioCatalog.mobIdlePaths(Mob.MobType.PILLAGER),
            AudioCatalog.mobAttackPaths(Mob.MobType.PILLAGER));
        assertEquals(3, AudioCatalog.mobHurtPaths(Mob.MobType.PILLAGER).length);
        assertEquals(2, AudioCatalog.mobDeathPaths(Mob.MobType.PILLAGER).length);
    }

    @Test
    public void vindicatorUsesOggIdleHurtDeathAndNoStepAudio() {
        assertEquals(5, AudioCatalog.mobIdlePaths(Mob.MobType.VINDICATOR).length);
        assertArrayEquals(AudioCatalog.mobIdlePaths(Mob.MobType.VINDICATOR),
            AudioCatalog.mobAttackPaths(Mob.MobType.VINDICATOR));
        assertEquals(3, AudioCatalog.mobHurtPaths(Mob.MobType.VINDICATOR).length);
        assertEquals(1, AudioCatalog.mobDeathPaths(Mob.MobType.VINDICATOR).length);
        assertEquals(0, AudioCatalog.mobStepPaths(Mob.MobType.VINDICATOR).length);
    }

    @Test
    public void ravagerUsesBiteAttackAndStepAudio() {
        assertEquals(8, AudioCatalog.mobIdlePaths(Mob.MobType.RAVAGER).length);
        assertEquals(4, AudioCatalog.mobHurtPaths(Mob.MobType.RAVAGER).length);
        assertEquals(3, AudioCatalog.mobDeathPaths(Mob.MobType.RAVAGER).length);
        assertEquals(5, AudioCatalog.mobStepPaths(Mob.MobType.RAVAGER).length);
        assertArrayEquals(
            new String[] {
                "audio/mobs/ravager/bite1.ogg",
                "audio/mobs/ravager/bite2.ogg",
                "audio/mobs/ravager/bite3.ogg"
            },
            AudioCatalog.mobAttackPaths(Mob.MobType.RAVAGER)
        );
    }

    @Test
    public void zombieHurtMappingStaysAvailable() {
        assertArrayEquals(
            new String[] {
                "audio/mobs/zombiehurt1.wav",
                "audio/mobs/zombiehurt2.wav"
            },
            AudioCatalog.mobHurtPaths(Mob.MobType.ZOMBIE)
        );
    }
}
