package com.main.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.game.screens.BaseScreen;
import com.main.game.screens.GameScreen;

/**
 * Entry point của game — thay thế file MainGame.java hiện tại.
 * SpriteBatch được tạo một lần ở đây và share cho tất cả Screen
 * để tránh tạo nhiều batch gây tốn bộ nhớ.
 *
 * TODO(HUY-LEAD):
 *  - Chuẩn hóa lifecycle tài nguyên dùng chung (SpriteBatch, AssetManager).
 *  - Bổ sung cơ chế chuyển screen an toàn khi có Menu/Pause/GameOver.
 */
public class MainGame extends Game {

    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (getScreen() != null) getScreen().dispose();
    }
}
