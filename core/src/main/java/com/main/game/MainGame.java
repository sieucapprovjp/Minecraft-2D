package com.main.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.game.screens.BaseScreen;

/**
 * Entry point của game — thay thế file MainGame.java hiện tại.
 * SpriteBatch được tạo một lần ở đây và share cho tất cả Screen
 * để tránh tạo nhiều batch gây tốn bộ nhớ.
 */
public class MainGame extends Game {

    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // TODO: setScreen(new MenuScreen(this)); — khi có MenuScreen
        // Tạm thời để trống, từng người tự setScreen khi test
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (getScreen() != null) getScreen().dispose();
    }
}
