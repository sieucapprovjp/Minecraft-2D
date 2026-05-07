package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Matrix4;
import com.main.game.MainGame;
import com.main.game.navigation.ScreenId;

public class StateScreen extends BaseScreen {

    private final ScreenId id;
    private BitmapFont font;
    private GlyphLayout layout;
    private Matrix4 uiProjection;

    public StateScreen(MainGame game, ScreenId id) {
        super(game);
        this.id = id;
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.layout = new GlyphLayout();
        this.uiProjection = new Matrix4();
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.getScreenRouter().request(ScreenId.GAME);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            game.getScreenRouter().request(ScreenId.GAME);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.getScreenRouter().request(ScreenId.MENU);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            game.getScreenRouter().request(ScreenId.GAME_OVER);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            game.getScreenRouter().request(ScreenId.PAUSE);
        }
    }

    @Override
    public void draw() {
        if (id == ScreenId.PAUSE) {
            Gdx.gl.glClearColor(0.15f, 0.15f, 0.18f, 1f);
        } else if (id == ScreenId.GAME_OVER) {
            Gdx.gl.glClearColor(0.28f, 0.08f, 0.08f, 1f);
        } else {
            Gdx.gl.glClearColor(0.10f, 0.12f, 0.16f, 1f);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        String title;
        String hint1;
        String hint2;

        if (id == ScreenId.PAUSE) {
            title = "PAUSED";
            hint1 = "ESC / G: Resume";
            hint2 = "M: Menu   K: Game Over";
        } else if (id == ScreenId.GAME_OVER) {
            title = "GAME OVER";
            hint1 = "G / ESC: Back To Game";
            hint2 = "M: Menu";
        } else {
            title = "MENU";
            hint1 = "G / ESC: Start Game";
            hint2 = "P: Pause   K: Game Over";
        }

        uiProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiProjection);
        batch.begin();

        drawCentered(title, Gdx.graphics.getHeight() * 0.62f);
        drawCentered(hint1, Gdx.graphics.getHeight() * 0.48f);
        drawCentered(hint2, Gdx.graphics.getHeight() * 0.40f);

        batch.end();
    }

    private void drawCentered(String text, float y) {
        layout.setText(font, text);
        float x = (Gdx.graphics.getWidth() - layout.width) * 0.5f;
        font.draw(batch, layout, x, y);
    }

    @Override
    public ScreenId getScreenId() {
        return id;
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
