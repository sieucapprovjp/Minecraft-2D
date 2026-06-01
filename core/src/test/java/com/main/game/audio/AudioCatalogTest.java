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
            AudioCatalog.mobHurtPaths(Mob.MobType.ZOMBIE)
        );
    }
}
