package com.main.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.main.game.GameState;
import com.main.game.time.DayNightCycle;

public class GameOverlayRenderer {

    private final Texture overlayTexture;
    private final Texture pauseTexture;
    private final Texture deathTexture;
    private final BitmapFont font;
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private final Matrix4 uiProjection = new Matrix4();

    public GameOverlayRenderer() {
        Pixmap overlayPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        overlayPixmap.setColor(Color.WHITE);
        overlayPixmap.fill();
        overlayTexture = new Texture(overlayPixmap);
        overlayPixmap.dispose();

        pauseTexture = new Texture(Gdx.files.internal("images/stage_sprite/pause.png"));
        deathTexture = new Texture(Gdx.files.internal("images/stage_sprite/death_screen.png"));
        font = new BitmapFont();
    }

    public void renderPause(SpriteBatch batch) {
        renderFullScreenTexture(batch, pauseTexture);
    }

    public void renderDeath(SpriteBatch batch) {
        renderFullScreenTexture(batch, deathTexture);
    }

    public void renderWorldDarkness(SpriteBatch batch, int globalLight) {
        if (globalLight <= 0) {
            return;
        }
        float darkness = Math.min(1f, globalLight / (float) DayNightCycle.MAX_GLOBAL_LIGHT);
        Color overlayColor = new Color(0f, 0f, 0f, darkness * 0.58f);

        uiProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiProjection);
        batch.begin();
        batch.setColor(overlayColor);
        batch.draw(overlayTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);
        batch.end();
    }

    public void renderBrightness(SpriteBatch batch, GameState gameState) {
        int brightness = gameState.brightness;
        float alpha;
        Color overlayColor;
        if (brightness < 50) {
            alpha = (50 - brightness) / 50f * 0.8f;
            overlayColor = new Color(0f, 0f, 0f, alpha);
        } else if (brightness > 50) {
            alpha = (brightness - 50) / 50f * 0.4f;
            overlayColor = new Color(1f, 1f, 1f, alpha);
        } else {
            return;
        }

        uiProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiProjection);
        batch.begin();
        batch.setColor(overlayColor);
        batch.draw(overlayTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);
        batch.end();
    }

    public void renderToast(SpriteBatch batch, String message, float alpha) {
        if (message == null || message.isEmpty() || alpha <= 0f) {
            return;
        }

        float clampedAlpha = Math.max(0f, Math.min(1f, alpha));
        uiProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiProjection);
        batch.begin();

        glyphLayout.setText(font, message);
        float paddingX = 18f;
        float paddingY = 10f;
        float boxW = glyphLayout.width + paddingX * 2f;
        float boxH = glyphLayout.height + paddingY * 2f;
        float boxX = (Gdx.graphics.getWidth() - boxW) * 0.5f;
        float boxY = 92f;

        batch.setColor(0f, 0f, 0f, 0.72f * clampedAlpha);
        batch.draw(overlayTexture, boxX, boxY, boxW, boxH);
        font.setColor(1f, 1f, 1f, clampedAlpha);
        font.draw(batch, glyphLayout, boxX + paddingX, boxY + paddingY + glyphLayout.height);

        batch.setColor(Color.WHITE);
        font.setColor(Color.WHITE);
        batch.end();
    }

    public void dispose() {
        overlayTexture.dispose();
        pauseTexture.dispose();
        deathTexture.dispose();
        font.dispose();
    }

    private void renderFullScreenTexture(SpriteBatch batch, Texture texture) {
        uiProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiProjection);
        batch.begin();
        batch.draw(texture, 0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }
}
