package com.main.game.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.main.game.raid.RaidState;
import org.junit.Test;

public class RaidHudRendererTest {

    @Test
    public void raidBarIsHiddenOnlyWhenIdle() {
        assertFalse(RaidHudRenderer.isVisible(RaidState.IDLE));
        assertTrue(RaidHudRenderer.isVisible(RaidState.COUNTDOWN));
        assertTrue(RaidHudRenderer.isVisible(RaidState.WAVE_ACTIVE));
        assertTrue(RaidHudRenderer.isVisible(RaidState.VICTORY));
        assertTrue(RaidHudRenderer.isVisible(RaidState.FAILED));
    }

    @Test
    public void labelFollowsRaidState() {
        assertEquals("RAID", RaidHudRenderer.labelFor(RaidState.COUNTDOWN, 0));
        assertEquals("RAID", RaidHudRenderer.labelFor(RaidState.BETWEEN_WAVES, 1));
        assertEquals("Raid: Wave 1", RaidHudRenderer.labelFor(RaidState.WAVE_ACTIVE, 1));
        assertEquals("Raid: Wave 3", RaidHudRenderer.labelFor(RaidState.WAVE_ACTIVE, 3));
        assertEquals("Raid: Victory", RaidHudRenderer.labelFor(RaidState.VICTORY, 3));
        assertEquals("Raid: Defeat", RaidHudRenderer.labelFor(RaidState.FAILED, 2));
    }

    @Test
    public void progressIsEmptyUntilWaveStartsAndTracksAliveRaidMobs() {
        assertEquals(0f, RaidHudRenderer.progressFor(RaidState.COUNTDOWN, 4, 4), 0.001f);
        assertEquals(1f, RaidHudRenderer.progressFor(RaidState.WAVE_ACTIVE, 4, 4), 0.001f);
        assertEquals(0.5f, RaidHudRenderer.progressFor(RaidState.WAVE_ACTIVE, 2, 4), 0.001f);
        assertEquals(0f, RaidHudRenderer.progressFor(RaidState.VICTORY, 0, 4), 0.001f);
        assertEquals(0f, RaidHudRenderer.progressFor(RaidState.FAILED, 3, 4), 0.001f);
    }

    @Test
    public void progressIsClamped() {
        assertEquals(1f, RaidHudRenderer.progressFor(RaidState.WAVE_ACTIVE, 10, 4), 0.001f);
        assertEquals(0f, RaidHudRenderer.progressFor(RaidState.WAVE_ACTIVE, -1, 4), 0.001f);
        assertEquals(0f, RaidHudRenderer.progressFor(RaidState.WAVE_ACTIVE, 1, 0), 0.001f);
    }

    @Test
    public void usesProvidedRedProgressAssets() {
        assertEquals("red_background.png", RaidHudRenderer.BACKGROUND_TEXTURE_PATH);
        assertEquals("red_progress.png", RaidHudRenderer.PROGRESS_TEXTURE_PATH);
    }

    @Test
    public void barHeightKeepsThinTextureReadable() {
        assertEquals(20f, RaidHudRenderer.barHeightForWidth(520f, 182, 5), 0.001f);
        assertEquals(24f, RaidHudRenderer.barHeightForWidth(1000f, 182, 5), 0.001f);
    }
}
