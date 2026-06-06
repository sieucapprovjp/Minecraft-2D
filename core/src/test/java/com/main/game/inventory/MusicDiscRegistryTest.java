package com.main.game.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import org.junit.Test;

public class MusicDiscRegistryTest {

    @Test
    public void pigstepDiscMetadataIsRegistered() {
        MusicDiscRegistry.MusicDiscDefinition disc = MusicDiscRegistry.get("pigstep");

        assertTrue(MusicDiscRegistry.isMusicDisc("pigstep"));
        assertEquals("Pigstep", disc.getDisplayName());
        assertEquals("audio/ui_menu/disc/pigstep.ogg", disc.getMusicPath());
        assertEquals("pigstep", disc.getTextureName());
        assertTrue(assetFile(disc.getMusicPath()).isFile());
    }

    @Test
    public void lavaChickenSlotUsesUpdatedDiscAsset() {
        MusicDiscRegistry.MusicDiscDefinition disc = MusicDiscRegistry.get("lava_chicken");

        assertTrue(MusicDiscRegistry.isMusicDisc("lava_chicken"));
        assertEquals("N\u00e0 N\u00e1 Na Na Anh \u0110\u1ed9 Mixi", disc.getDisplayName());
        assertEquals("audio/ui_menu/disc/N\u00e0 N\u00e1 Na Na Anh \u0110\u1ed9 Mixi.mp3", disc.getMusicPath());
        assertEquals("lava_chicken", disc.getTextureName());
        assertTrue(assetFile(disc.getMusicPath()).isFile());
    }

    @Test
    public void musicDiscsStackToOne() {
        assertEquals(1, ItemRegistry.getMaxStack("pigstep"));
        assertEquals(1, ItemRegistry.getMaxStack("lava_chicken"));
    }

    private static File assetFile(String path) {
        File localAssets = new File("assets", path);
        if (localAssets.isFile()) {
            return localAssets;
        }
        return new File("../assets", path);
    }
}
