package com.main.game.audio;

import com.main.game.entities.mob.Mob;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AudioCatalogTest {

    @Test
    public void menuMusicUsesProvidedMenuAsset() {
        assertEquals("audio/ui_menu/music.mp3", AudioCatalog.musicPath(AudioId.MENU_MUSIC));
    }

    @Test
    public void gameplayMusicUsesIngameTracksAndRaidRubedo() {
        assertArrayEquals(
            new String[] {
                "audio/ui_menu/ingame_music/a_familiar_room.ogg",
                "audio/ui_menu/ingame_music/below_and_above.ogg",
                "audio/ui_menu/ingame_music/deeper.ogg",
                "audio/ui_menu/ingame_music/minecraft.ogg",
                "audio/ui_menu/ingame_music/sweden.ogg",
                "audio/ui_menu/ingame_music/yakusoku.ogg"
            },
            AudioCatalog.ingameMusicPaths()
        );
        assertEquals("audio/ui_menu/rubedo.ogg", AudioCatalog.raidMusicPath());
    }

    @Test
    public void playerDeathReusesPlayerHurtForV1() {
        assertArrayEquals(
            new String[] {"audio/player/hurt.mp3"},
            AudioCatalog.soundPaths(AudioId.PLAYER_DEATH)
        );
    }

    @Test
    public void utilityBlocksUseDoorAndFurnaceAssets() {
        assertArrayEquals(
            new String[] {"audio/chest_inventory/door/open_door.mp3"},
            AudioCatalog.soundPaths(AudioId.UTILITY_BLOCK_OPEN)
        );
        assertArrayEquals(
            new String[] {"audio/chest_inventory/door/close_door.mp3"},
            AudioCatalog.soundPaths(AudioId.UTILITY_BLOCK_CLOSE)
        );
        assertEquals(5, AudioCatalog.soundPaths(AudioId.FURNACE_CRACKLE).length);
    }

    @Test
    public void blockBreakUsesMaterialSpecificAssetsOnlyWhenAvailable() {
        assertArrayEquals(
            new String[] {
                "audio/block/stone1.ogg",
                "audio/block/stone2.ogg",
                "audio/block/stone3.ogg",
                "audio/block/stone4.ogg"
            },
            AudioCatalog.blockBreakPaths("stone")
        );
        assertArrayEquals(
            new String[] {
                "audio/block/grass1.ogg",
                "audio/block/grass2.ogg",
                "audio/block/grass3.ogg",
                "audio/block/grass4.ogg"
            },
            AudioCatalog.blockBreakPaths("grass")
        );
        assertArrayEquals(AudioCatalog.blockBreakPaths("grass"), AudioCatalog.blockBreakPaths("dirt"));
    }

    @Test
    public void zombieDeathUsesDedicatedUpdatedAsset() {
        assertArrayEquals(
            new String[] {
                "audio/mobs/zombie/zombiedeath.wav"
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
    public void vexUsesIdleHurtDeathAndChargeAudio() {
        assertEquals(4, AudioCatalog.mobIdlePaths(Mob.MobType.VEX).length);
        assertEquals(2, AudioCatalog.mobHurtPaths(Mob.MobType.VEX).length);
        assertEquals(2, AudioCatalog.mobDeathPaths(Mob.MobType.VEX).length);
        assertArrayEquals(
            new String[] {
                "audio/mobs/vex/charge1.ogg",
                "audio/mobs/vex/charge2.ogg",
                "audio/mobs/vex/charge3.ogg"
            },
            AudioCatalog.mobAttackPaths(Mob.MobType.VEX)
        );
    }

    @Test
    public void zombieHurtMappingStaysAvailable() {
        assertArrayEquals(
            new String[] {
                "audio/mobs/zombie/zombiehurt1.wav",
                "audio/mobs/zombie/zombiehurt2.wav"
            },
            AudioCatalog.mobHurtPaths(Mob.MobType.ZOMBIE)
        );
    }

    @Test
    public void catalogPathsPointAtExistingAssets() {
        Set<String> paths = new LinkedHashSet<>();
        for (AudioId id : AudioId.values()) {
            addAll(paths, AudioCatalog.soundPaths(id));
            String musicPath = AudioCatalog.musicPath(id);
            if (musicPath != null) {
                paths.add(musicPath);
            }
        }
        addAll(paths, AudioCatalog.ingameMusicPaths());
        paths.add(AudioCatalog.raidMusicPath());
        for (Mob.MobType type : Mob.MobType.values()) {
            addAll(paths, AudioCatalog.mobHurtPaths(type));
            addAll(paths, AudioCatalog.mobDeathPaths(type));
            addAll(paths, AudioCatalog.mobIdlePaths(type));
            addAll(paths, AudioCatalog.mobStepPaths(type));
            addAll(paths, AudioCatalog.mobAttackPaths(type));
        }
        for (String blockId : new String[] {"stone", "deepslate", "sand", "snow", "oak_log", "chest"}) {
            addAll(paths, AudioCatalog.blockBreakPaths(blockId));
        }

        for (String path : paths) {
            assertTrue("Missing audio asset: " + path, assetFile(path).isFile());
        }
    }

    private static void addAll(Set<String> paths, String[] newPaths) {
        paths.addAll(Arrays.asList(newPaths));
    }

    private static File assetFile(String path) {
        File localAssets = new File("assets", path);
        if (localAssets.isFile()) {
            return localAssets;
        }
        return new File("../assets", path);
    }
}
