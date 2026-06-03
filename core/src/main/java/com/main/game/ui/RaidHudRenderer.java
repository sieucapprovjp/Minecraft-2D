package com.main.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.main.game.entities.EntityManager;
import com.main.game.raid.RaidController;
import com.main.game.raid.RaidState;

public final class RaidHudRenderer {

    static final String BACKGROUND_TEXTURE_PATH = "red_background.png";
    static final String PROGRESS_TEXTURE_PATH = "red_progress.png";
    private static final float BAR_MAX_WIDTH = 520f;
    private static final float MIN_BAR_HEIGHT = 20f;
    private static final float MAX_BAR_HEIGHT = 24f;
    private static final float TOP_MARGIN = 18f;
    private static final Color TEXT_SHADOW_COLOR = new Color(0f, 0f, 0f, 0.85f);

    private final Matrix4 uiProjection = new Matrix4();
    private final Texture backgroundTexture;
    private final Texture progressTexture;
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    public RaidHudRenderer() {
        backgroundTexture = new Texture(Gdx.files.internal(BACKGROUND_TEXTURE_PATH));
        progressTexture = new Texture(Gdx.files.internal(PROGRESS_TEXTURE_PATH));
        font = new BitmapFont();
        font.getData().setScale(1.15f);
    }

    public void render(SpriteBatch batch, RaidController raidController, EntityManager entityManager) {
        if (batch == null || raidController == null || !isVisible(raidController.getState())) {
            return;
        }

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        float barW = Math.min(BAR_MAX_WIDTH, sw * 0.72f);
        float barH = barHeightForWidth(barW, backgroundTexture.getWidth(), backgroundTexture.getHeight());
        float barX = (sw - barW) * 0.5f;
        float barY = sh - TOP_MARGIN - barH;
        String label = labelFor(raidController.getState(), raidController.getCurrentWave());
        float progress = progressFor(raidController.getState(),
            RaidController.countAliveRaidMobs(entityManager),
            raidController.getCurrentWaveMobCount());

        uiProjection.setToOrtho2D(0, 0, sw, sh);
        batch.setProjectionMatrix(uiProjection);
        batch.begin();
        batch.draw(backgroundTexture, barX, barY, barW, barH);
        if (progress > 0f) {
            drawProgress(batch, progress, barX, barY, barW, barH);
        }
        drawCenteredText(batch, label, barX, barY, barW, barH);
        batch.setColor(Color.WHITE);
        batch.end();
    }

    public void dispose() {
        backgroundTexture.dispose();
        progressTexture.dispose();
        font.dispose();
    }

    static boolean isVisible(RaidState state) {
        return state != null && state != RaidState.IDLE;
    }

    static String labelFor(RaidState state, int currentWave) {
        if (state == RaidState.WAVE_ACTIVE) {
            return "Raid: Wave " + Math.max(1, currentWave);
        }
        if (state == RaidState.VICTORY) {
            return "Raid: Victory";
        }
        if (state == RaidState.FAILED) {
            return "Raid: Defeat";
        }
        return "RAID";
    }

    static float progressFor(RaidState state, int aliveRaidMobs, int currentWaveMobCount) {
        if (state != RaidState.WAVE_ACTIVE || currentWaveMobCount <= 0) {
            return 0f;
        }
        float progress = aliveRaidMobs / (float) currentWaveMobCount;
        return Math.max(0f, Math.min(1f, progress));
    }

    static float barHeightForWidth(float width, int textureWidth, int textureHeight) {
        if (textureWidth <= 0 || textureHeight <= 0) {
            return MIN_BAR_HEIGHT;
        }
        float proportionalHeight = width * textureHeight / (float) textureWidth;
        return Math.max(MIN_BAR_HEIGHT, Math.min(MAX_BAR_HEIGHT, proportionalHeight));
    }

    private void drawProgress(SpriteBatch batch, float progress, float x, float y, float width, float height) {
        int srcWidth = Math.max(1, Math.round(progressTexture.getWidth() * progress));
        float drawWidth = width * progress;
        batch.draw(progressTexture, x, y, drawWidth, height,
            0, 0, srcWidth, progressTexture.getHeight(), false, false);
    }

    private void drawCenteredText(SpriteBatch batch, String text, float x, float y, float width, float height) {
        layout.setText(font, text);
        float textX = x + (width - layout.width) * 0.5f;
        float textY = y + (height + layout.height) * 0.5f;
        font.setColor(TEXT_SHADOW_COLOR);
        font.draw(batch, text, textX + 1f, textY - 1f);
        font.setColor(Color.WHITE);
        font.draw(batch, text, textX, textY);
    }
}
