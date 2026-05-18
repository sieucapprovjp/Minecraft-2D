package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.main.game.GameState;
import com.main.game.MainGame;
import com.main.game.navigation.ScreenId;

public class SettingsScreen extends BaseScreen {

    private Texture bgTexture;
    private Texture panelTexture;
    private Texture tickTexture;
    private Texture sliderTrackTexture;
    private Texture sliderThumbTexture;

    private BitmapFont font;
    private GlyphLayout layout;

    private float soundBoxScale = 1f;
    private float musicBoxScale = 1f;
    private float doneScale = 1f;

    private float panelX, panelY, panelW, panelH;
    private float soundBoxX, soundBoxY, soundBoxW, soundBoxH;
    private float musicBoxX, musicBoxY, musicBoxW, musicBoxH;
    private float sliderX, sliderY, sliderW, sliderH;
    private float doneX, doneY, doneW, doneH;

    private boolean draggingSlider;

    public SettingsScreen(MainGame game) {
        super(game);
    }

    @Override
    public ScreenId getScreenId() {
        return ScreenId.SETTINGS;
    }

    @Override
    public void show() {
        bgTexture = new Texture(Gdx.files.internal("images/stage_sprite/empty2.png"));
        panelTexture = new Texture(Gdx.files.internal("images/stage_sprite/blank.png"));
        tickTexture = new Texture(Gdx.files.internal("images/unnamed/costume6.png"));

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fill();
        sliderTrackTexture = new Texture(pix);
        sliderThumbTexture = new Texture(pix);
        pix.dispose();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/2c90030680a2fafd21f53fd39a0862e7.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 26;
        params.color = Color.WHITE;
        params.borderWidth = 2f;
        params.borderColor = new Color(0f, 0f, 0f, 0.6f);
        font = generator.generateFont(params);
        generator.dispose();
        layout = new GlyphLayout();
    }

    @Override
    public void update(float delta) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        float mx = Gdx.input.getX();
        float my = sh - Gdx.input.getY();
        boolean clicked = Gdx.input.justTouched();

        updateLayout(sw, sh);

        GameState gameState = game.getGameState();

        boolean soundHover = mx >= soundBoxX && mx <= soundBoxX + soundBoxW && my >= soundBoxY && my <= soundBoxY + soundBoxH;
        boolean musicHover = mx >= musicBoxX && mx <= musicBoxX + musicBoxW && my >= musicBoxY && my <= musicBoxY + musicBoxH;
        boolean doneHover = mx >= doneX && mx <= doneX + doneW && my >= doneY && my <= doneY + doneH;

        float soundTarget = soundHover ? 1.05f : 1.0f;
        float musicTarget = musicHover ? 1.05f : 1.0f;
        soundBoxScale += (soundTarget - soundBoxScale) * 0.2f;
        musicBoxScale += (musicTarget - musicBoxScale) * 0.2f;
        doneScale += ((doneHover ? 1.05f : 1.0f) - doneScale) * 0.2f;

        if (clicked) {
            if (soundHover) {
                gameState.soundEnabled = !gameState.soundEnabled;
            } else if (musicHover) {
                gameState.musicEnabled = !gameState.musicEnabled;
            } else if (doneHover) {
                game.getScreenRouter().request(ScreenId.MODE_SELECT);
            } else if (mx >= sliderX && mx <= sliderX + sliderW && my >= sliderY - sliderH && my <= sliderY + sliderH * 2f) {
                draggingSlider = true;
                updateBrightness(mx);
            }
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (draggingSlider) {
                updateBrightness(mx);
            }
        } else {
            draggingSlider = false;
        }
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        updateLayout(sw, sh);

        batch.getProjectionMatrix().setToOrtho2D(0, 0, sw, sh);
        batch.begin();

        batch.draw(bgTexture, 0, 0, sw, sh);

        batch.draw(panelTexture, panelX, panelY, panelW, panelH);

        drawCheckbox(soundBoxX, soundBoxY, soundBoxW, soundBoxH, game.getGameState().soundEnabled, soundBoxScale);
        drawCheckbox(musicBoxX, musicBoxY, musicBoxW, musicBoxH, game.getGameState().musicEnabled, musicBoxScale);

        drawLabel("Sound", soundBoxX + soundBoxW + 20f, soundBoxY + soundBoxH * 0.75f);
        drawLabel("Music", musicBoxX + musicBoxW + 20f, musicBoxY + musicBoxH * 0.75f);

        drawLabel("Brightness", sliderX, sliderY + sliderH * 4f);
        drawSlider();

        drawScaledButton(panelTexture, doneX, doneY, doneW, doneH, doneScale, new Color(0.2f, 0.2f, 0.2f, 0.7f));
        drawCenteredText("DONE", doneX, doneY, doneW, doneH);

        batch.setColor(Color.WHITE);
        batch.end();
    }

    private void drawSlider() {
        float brightness = game.getGameState().brightness / 100f;
        batch.setColor(new Color(0.25f, 0.25f, 0.25f, 1f));
        batch.draw(sliderTrackTexture, sliderX, sliderY, sliderW, sliderH);

        float thumbW = 14f * (sliderH / 8f);
        float thumbH = 24f * (sliderH / 8f);
        float thumbX = sliderX + sliderW * brightness - thumbW / 2f;
        float thumbY = sliderY - (thumbH - sliderH) / 2f;
        batch.setColor(new Color(0.9f, 0.9f, 0.9f, 1f));
        batch.draw(sliderThumbTexture, thumbX, thumbY, thumbW, thumbH);
    }

    private void drawCheckbox(float x, float y, float w, float h, boolean checked, float scale) {
        float scaledW = w * scale;
        float scaledH = h * scale;
        float sx = x + (w - scaledW) / 2f;
        float sy = y + (h - scaledH) / 2f;
        batch.setColor(new Color(0.15f, 0.15f, 0.15f, 0.85f));
        batch.draw(panelTexture, sx, sy, scaledW, scaledH);
        if (checked) {
            float pad = Math.min(scaledW, scaledH) * 0.15f;
            batch.setColor(Color.WHITE);
            batch.draw(tickTexture, sx + pad, sy + pad, scaledW - pad * 2f, scaledH - pad * 2f);
        }
    }

    private void drawLabel(String text, float x, float y) {
        layout.setText(font, text);
        font.draw(batch, layout, x, y);
    }

    private void drawCenteredText(String text, float x, float y, float w, float h) {
        layout.setText(font, text);
        float tx = x + (w - layout.width) / 2f;
        float ty = y + (h + layout.height) / 2f;
        font.draw(batch, layout, tx, ty);
    }

    private void drawScaledButton(Texture tex, float x, float y, float w, float h, float scale, Color color) {
        float scaledW = w * scale;
        float scaledH = h * scale;
        float sx = x + (w - scaledW) / 2f;
        float sy = y + (h - scaledH) / 2f;
        batch.setColor(color);
        batch.draw(tex, sx, sy, scaledW, scaledH);
    }

    private void updateBrightness(float mouseX) {
        float percent = (mouseX - sliderX) / sliderW;
        game.getGameState().brightness = (int) MathUtils.clamp(percent * 100f, 0f, 100f);
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        super.dispose();
        if (bgTexture != null) bgTexture.dispose();
        if (panelTexture != null) panelTexture.dispose();
        if (tickTexture != null) tickTexture.dispose();
        if (sliderTrackTexture != null) sliderTrackTexture.dispose();
        if (sliderThumbTexture != null) sliderThumbTexture.dispose();
        if (font != null) font.dispose();
    }

    private void updateLayout(float sw, float sh) {
        float uiScale = Math.min(sw / 482f, sh / 344f);

        panelW = 360f * uiScale;
        panelH = 240f * uiScale;
        panelX = (sw - panelW) / 2f;
        panelY = (sh - panelH) / 2f;

        float boxSize = 28f * uiScale;
        soundBoxW = boxSize;
        soundBoxH = boxSize;
        soundBoxX = panelX + 40f * uiScale;
        soundBoxY = panelY + panelH - 80f * uiScale;

        musicBoxW = boxSize;
        musicBoxH = boxSize;
        musicBoxX = soundBoxX;
        musicBoxY = soundBoxY - 50f * uiScale;

        sliderW = panelW - 80f * uiScale;
        sliderH = 8f * uiScale;
        sliderX = panelX + 40f * uiScale;
        sliderY = panelY + 60f * uiScale;

        doneW = 140f * uiScale;
        doneH = 36f * uiScale;
        doneX = panelX + (panelW - doneW) / 2f;
        doneY = panelY + 18f * uiScale;
    }
}
