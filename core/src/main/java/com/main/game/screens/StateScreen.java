package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.main.game.MainGame;
import com.main.game.navigation.ScreenId;

public class StateScreen extends BaseScreen {

    private final ScreenId id;

    public StateScreen(MainGame game, ScreenId id) {
        super(game);
        this.id = id;
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.getScreenRouter().request(ScreenId.GAME);
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
    }
}
